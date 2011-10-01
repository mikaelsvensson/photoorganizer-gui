package info.photoorganizer.gui.window.config;

import info.photoorganizer.database.autoindexing.AllFilesFileFilter;
import info.photoorganizer.database.autoindexing.DefaultIndexingConfiguration;
import info.photoorganizer.database.autoindexing.IndexingConfigurationInterface;
import info.photoorganizer.database.autoindexing.MetadataMappingConfiguration;
import info.photoorganizer.database.autoindexing.MetadataMappingConfigurationInterface;
import info.photoorganizer.database.autoindexing.POFileFilter;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.POAction;
import info.photoorganizer.gui.RejectedFileExtensionFileFilter;
import info.photoorganizer.gui.components.POTwoLevelChoice;
import info.photoorganizer.gui.components.frame.POCloseReason;
import info.photoorganizer.gui.components.frame.POGuideDialog;
import info.photoorganizer.gui.components.frame.POGuidePage;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.gui.shared.POSimpleDocumentListener;
import info.photoorganizer.gui.window.config.EditTextTransformationDialog.TextTransformationOption;
import info.photoorganizer.metadata.PhotoFileMetadataTag;
import info.photoorganizer.metadata.RegexpFileFilter;
import info.photoorganizer.metadata.TagDefinition;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.StringUtils;
import info.photoorganizer.util.transform.TextCaseTransformer;
import info.photoorganizer.util.transform.TextTransformer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class EditIndexingConfigurationDialog extends POGuideDialog
{
    private enum FileFilterOption
    {
        NO_FILTER
        {
            @Override
            public POFileFilter createFilter()
            {
                return new AllFilesFileFilter();
            }
            
            @Override
            protected JPanel createUI(POFileFilter filter)
            {
                return new JPanel();
            }

            @Override
            public boolean handlesFileFilter(POFileFilter filter)
            {
                return filter instanceof AllFilesFileFilter;
            }
        },
        REGEXP
        {
            
            @Override
            public POFileFilter createFilter()
            {
                return new RegexpFileFilter();
            }

            @Override
            protected JPanel createUI(POFileFilter filter)
            {
                String filterText = "";
                JTextField textField = GuiComponentFactory.createTextField(filterText);
                if (handlesFileFilter(filter))
                {
                    final RegexpFileFilter regexpFilter = (RegexpFileFilter) filter;
                    filterText = regexpFilter.getParam();
                    textField.setText(filterText);
                    textField.getDocument().addDocumentListener(new POSimpleDocumentListener()
                    {
                        @Override
                        public void update(DocumentEvent e, String text)
                        {
                            regexpFilter.setParam(text);
                        }
                    });
                }
                JPanel panel = GuiComponentFactory.createUserOptionsPanel(
                        GuiComponentFactory.createLabel(getI18nString("EXPRESSION_LABEL")),
                        textField
                        );
                return panel;
            }

            @Override
            public boolean handlesFileFilter(POFileFilter filter)
            {
                return filter instanceof RegexpFileFilter;
            }
        },
        REJECTED_EXTENSIONS
        {
            @Override
            public POFileFilter createFilter()
            {
                return new RejectedFileExtensionFileFilter();
//                filter.setRejectedFileTypes(textField.getText().split(EXTENSION_SEPARATOR));
//                return filter;
            }

            @Override
            protected JPanel createUI(POFileFilter filter)
            {
                String extensionsText = "";
                JTextField textField = GuiComponentFactory.createTextField("");
                if (handlesFileFilter(filter))
                {
                    final RejectedFileExtensionFileFilter rejectedExtensionFilter = (RejectedFileExtensionFileFilter) filter;
                    extensionsText = StringUtils.join(rejectedExtensionFilter.getRejectedFileTypes().iterator(), EXTENSION_SEPARATOR);
                    textField.setText(extensionsText);
                    textField.getDocument().addDocumentListener(new POSimpleDocumentListener()
                    {
                        @Override
                        public void update(DocumentEvent e, String text)
                        {
                            rejectedExtensionFilter.setRejectedFileTypes(text.split(EXTENSION_SEPARATOR));
                        }
                    });
                }
                JPanel panel = GuiComponentFactory.createUserOptionsPanel(
                        GuiComponentFactory.createLabel(getI18nString("EXTENSIONS_LABEL")),
                        textField
                        );
                return panel;
            }

            @Override
            public boolean handlesFileFilter(POFileFilter filter)
            {
                return filter instanceof RejectedFileExtensionFileFilter;
            }
        };

        private static final String EXTENSION_SEPARATOR = ",";

        private I18n i18n = I18n.getInstance();

        public abstract POFileFilter createFilter();

        protected abstract JPanel createUI(POFileFilter filter);
        
        public String getI18nString(String key, Object... parameters)
        {
            return i18n.getString(EditIndexingConfigurationDialog.class,
                    FileFilterOption.class.getSimpleName() + "." + key, parameters);
        }
        
        public abstract boolean handlesFileFilter(POFileFilter filter);

        @Override
        public String toString()
        {
            return getI18nString(name());
        }

        public static FileFilterOption valueOf(POFileFilter filter)
        {
            for (FileFilterOption f : values())
            {
                if (f.handlesFileFilter(filter))
                {
                    return f;
                }
            }
            return null;
        }
    }
    
    
    
    private class FileFilterPage extends Page
    {
        private static final long serialVersionUID = 1L;
        
        private FileFilterPage()
        {
            super();
        }
        
        private POTwoLevelChoice<FileFilterOption, JPanel> _options = null;

        @Override
        protected void initComponents()
        {
            LinkedHashMap<FileFilterOption, JPanel> choices = new LinkedHashMap<EditIndexingConfigurationDialog.FileFilterOption, JPanel>();
            for (FileFilterOption filterOption : FileFilterOption.values())
            {
                POFileFilter filter = null; 
                if (filterOption.handlesFileFilter(cfg.getFileFilter()))
                {
                    filter = cfg.getFileFilter();
                }
                else
                {
                    filter = filterOption.createFilter();
                }
                JPanel filterPanel = filterOption.createUI(filter);
                filterPanel.putClientProperty("FILTER_MODEL", filter);
                choices.put(filterOption, filterPanel);
            }
            _options = new POTwoLevelChoice<EditIndexingConfigurationDialog.FileFilterOption, JPanel>(choices);
            _options.setSelected(EditIndexingConfigurationDialog.FileFilterOption.valueOf(cfg.getFileFilter()));
            add(_options);
        }

        @Override
        protected boolean onOK()
        {
            POFileFilter item = (POFileFilter)_options.getSelectedChoiceValue().getClientProperty("FILTER_MODEL");
            cfg.setFileFilter(item);
            return true;
        }

    }
    
    private class MetadataMappingPage extends Page
    {
        private final Border LEFT_PADDING_BORDER = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        private final CompoundBorder SEPARATION_BORDER = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0), 
                BorderFactory.createCompoundBorder(
                                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                                    BorderFactory.createEmptyBorder(0, 0, 10, 0)
                            ));
        private static final String MAPPING_OBJ_KEY = "MappingObject";
        private JScrollPane _list = null;
        private JPanel _mappings = null;
        private JButton _addMappingButton = null;
        
        @Override
        protected void initComponents()
        {
            add(getList(), BorderLayout.CENTER);
        }

        @Override
        protected boolean onOK()
        {
//            cfg.getMetadataMappers().clear();
//            for (Component panel : _mappings.getComponents())
//            {
//                cfg.getMetadataMappers().add((MetadataMappingConfigurationInterface) ((JComponent) panel).getClientProperty(MAPPING_OBJ_KEY));
//            }
            return true;
        }

        private JScrollPane getList()
        {
            if (_list == null)
            {
                _mappings = GuiComponentFactory.createBoxLayoutPanel(false);
                _mappings.add(GuiComponentFactory.createButton(getI18nText("ADD_MAPPING"), new POActionListener()
                {
                    @Override
                    public void actionPerformedImpl(ActionEvent event)
                    {
                        addMappingPanel(new MetadataMappingConfiguration(/*getDatabase()*/));
                    }
                }));
                _list = new JScrollPane(_mappings);
                for (MetadataMappingConfigurationInterface mapper : cfg.getMetadataMappers())
                {
                    addMappingPanel(mapper);
                }
                _list.setPreferredSize(new Dimension(800, 500));
            }
            return _list;
        }
        
        private void addMappingPanel(final MetadataMappingConfigurationInterface mappingCfg)
        {
            final DefaultListModel transformationsListMode = new DefaultListModel();
            for (TextTransformer existingTransformer : mappingCfg.getSourceTextTransformers())
            {
                transformationsListMode.addElement(existingTransformer);
            }
            transformationsListMode.addListDataListener(new ListDataListener()
            {
                
                @Override
                public void intervalRemoved(ListDataEvent e)
                {
                    updateBackedList();
                }
                
                @Override
                public void intervalAdded(ListDataEvent e)
                {
                    updateBackedList();
                }
                
                @Override
                public void contentsChanged(ListDataEvent e)
                {
                    updateBackedList();
                }
                
                private void updateBackedList()
                {
                    mappingCfg.getSourceTextTransformers().clear();
                    Enumeration<TextTransformer> elements = (Enumeration<TextTransformer>) transformationsListMode.elements();
                    while (elements.hasMoreElements())
                    {
                        mappingCfg.getSourceTextTransformers().add(elements.nextElement());
                    }
                }
            });
            final JList transformationsList = new JList(transformationsListMode);
            
            final POAction addTextTransformationAction = new POAction(getI18nText("ADD_TEXT_TRANSFORMATION_LABEL"))
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    TextCaseTransformer textCaseTransformer = new TextCaseTransformer(TextCaseTransformer.Transformation.CAPITALIZE.name());
                    try
                    {
                        EditTextTransformationDialog dialog = new EditTextTransformationDialog(EditIndexingConfigurationDialog.this, TextTransformationOption.REPLACE);
                        GuiComponentFactory.showModalDialog(dialog);
                        if (dialog.getCloseReason() == POCloseReason.OK)
                        {
                            transformationsListMode.addElement(dialog.getConfiguration());
                        }
                    }
                    catch (CloneNotSupportedException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            };
            final POAction editTextTransformationAction = new POAction(getI18nText("EDIT_TEXT_TRANSFORMATION_LABEL"))
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    TextCaseTransformer textCaseTransformer = new TextCaseTransformer(TextCaseTransformer.Transformation.CAPITALIZE.name());
                    try
                    {
                        int index = transformationsList.getSelectedIndex();
                        EditTextTransformationDialog dialog = new EditTextTransformationDialog(EditIndexingConfigurationDialog.this, (TextTransformer) transformationsListMode.get(index));
                        GuiComponentFactory.showModalDialog(dialog);
                        if (dialog.getCloseReason() == POCloseReason.OK)
                        {
                            transformationsListMode.setElementAt(dialog.getConfiguration(), index);
                        }
                    }
                    catch (CloneNotSupportedException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            };
            final POAction removeTextTransformationAction = new POAction(getI18nText("REMOVE_TEXT_TRANSFORMATION_LABEL"))
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (transformationsList.getSelectedIndex() >= 0)
                    {
                        transformationsListMode.remove(transformationsList.getSelectedIndex());
                    }
                }
            };
            final POAction promoteTextTransformationAction = new POAction(getI18nText("PROMOTE_TEXT_TRANSFORMATION_LABEL"))
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int index = transformationsList.getSelectedIndex();
                    if (index >= 1)
                    {
                        transformationsListMode.add(index - 1, transformationsListMode.remove(index));
                        transformationsList.setSelectedIndex(index - 1);
                    }
                }
            };
            final POAction demoteTextTransformationAction = new POAction(getI18nText("DEMOTE_TEXT_TRANSFORMATION_LABEL"))
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int index = transformationsList.getSelectedIndex();
                    if (index >= 0 && index < transformationsListMode.size() - 1)
                    {
                        transformationsListMode.add(index, transformationsListMode.remove(index + 1));
                        transformationsList.setSelectedIndex(index + 1);
                    }
                }
            };
            
            JLabel sourceLabel = GuiComponentFactory.createLabel(getI18nText("MAPPING_SOURCE"));
            JComboBox sourceList = GuiComponentFactory.createEnumDropDownList(PhotoFileMetadataTag.class, mappingCfg.getSource(), null);
            sourceList.setSelectedItem(mappingCfg.getSource());
            sourceList.setBorder(LEFT_PADDING_BORDER);
            sourceList.addItemListener(new ItemListener()
            {
                
                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    mappingCfg.setSource((PhotoFileMetadataTag) e.getItem());
                }
            });
            
            JLabel targetLabel = GuiComponentFactory.createLabel(getI18nText("MAPPING_TARGET"));
            JComboBox targetDropDown = new JComboBox(getDatabase().getTagDefinitions().toArray());
            targetDropDown.setSelectedItem(mappingCfg.getTarget(getDatabase()));
            targetDropDown.setBorder(LEFT_PADDING_BORDER);
            targetDropDown.addItemListener(new ItemListener()
            {
                
                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    mappingCfg.setTarget((TagDefinition) e.getItem());
                }
            });
            
            
            String transformationsCaptionText = getI18nText(mappingCfg.getSourceTextTransformers().size() > 0 ? "TEXT_TRANSFORMATION_LIST_NONEMPTY" : "TEXT_TRANSFORMATION_LIST_EMPTY", mappingCfg.getSourceTextTransformers().size());
            JPanel transformationsPanel = GuiComponentFactory.createBoxLayoutPanel(
                    false,
                    GuiComponentFactory.createLabel(transformationsCaptionText),
                    transformationsList, 
                    GuiComponentFactory.createFlowLayoutPanel(
                            FlowLayoutAlignment.LEFT, 
                            GuiComponentFactory.createButton(addTextTransformationAction),
                            GuiComponentFactory.createButton(editTextTransformationAction),
                            GuiComponentFactory.createButton(removeTextTransformationAction),
                            GuiComponentFactory.createButton(promoteTextTransformationAction),
                            GuiComponentFactory.createButton(demoteTextTransformationAction)
                            )
                    );
            
            JPanel panel = new JPanel(new GridBagLayout());
            
            JComponent sep = new JLabel();
            sep.setBorder(SEPARATION_BORDER);
            
            GridBagConstraints separatorConstraints = new GridBagConstraints();
            separatorConstraints.fill = GridBagConstraints.HORIZONTAL;
            separatorConstraints.gridx = 0;
            separatorConstraints.gridwidth = 2;
            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints optConstraints = new GridBagConstraints();
            optConstraints.gridx = 1;
            optConstraints.fill = GridBagConstraints.HORIZONTAL;
            if (_mappings.getComponentCount() > 1)
            {
                panel.add(sep, separatorConstraints);
            }
                
            panel.add(sourceLabel, labelConstraints);
            panel.add(sourceList, optConstraints);
            JLabel transformersLabel = GuiComponentFactory.createLabel(getI18nText("MAPPING_TEXTTRANSFORMERS"));
            panel.add(transformersLabel, labelConstraints);
            panel.add(transformationsPanel, optConstraints);
            panel.add(targetLabel, labelConstraints);
            panel.add(targetDropDown, optConstraints);
            
//            JPanel panel = GuiComponentFactory.createUserOptionsPanel(
//                    GuiComponentFactory.createLabel(getI18nText("MAPPING_SOURCE")),
//                    GuiComponentFactory.createEnumDropDownList(PhotoFileMetadataTag.class, mappingCfg.getSource(), null),
//                    GuiComponentFactory.createLabel(getI18nText("MAPPING_TEXTTRANSFORMERS")),
//                    new JPanel(),
//                    targetLabel,
//                    targetList
//                    );
            panel.putClientProperty(MAPPING_OBJ_KEY, mappingCfg);
            _mappings.add(panel);
            _mappings.revalidate();
        }
    }

    private abstract class Page extends POGuidePage
    {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public String getDescription()
        {
            return getI18nText(EditIndexingConfigurationDialog.class,
                    getClass().getSimpleName() + ".DESCRIPTION");
        }

        @Override
        public String getName()
        {
            return getI18nText(EditIndexingConfigurationDialog.class,
                    getClass().getSimpleName() + ".TITLE");
        }
        
        protected String getI18nText(String key, Object... parameters)
        {
            return super.getI18nText(EditIndexingConfigurationDialog.class,
                    getClass().getSimpleName() + "." + key, parameters);
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private POGuidePage[] _pages = null;

    private IndexingConfigurationInterface cfg = null;

    public EditIndexingConfigurationDialog(Dialog owner)
            throws CloneNotSupportedException
    {
        this(owner, null);
    }

    public EditIndexingConfigurationDialog(Dialog owner,
            IndexingConfigurationInterface originalConfiguration)
            throws CloneNotSupportedException
    {
        super(owner, null != originalConfiguration ? "TITLE_EDIT" : "TITLE_NEW");

        if (null != originalConfiguration)
        {
            cfg = originalConfiguration.cloneDeep();
        }
        else
        {
            cfg = new DefaultIndexingConfiguration();
        }
        
        initComponents();
    }

    public IndexingConfigurationInterface getConfiguration()
    {
        return cfg;
    }

    @Override
    protected POGuidePage[] getPagesImpl()
    {
        if (null == _pages)
        {
            _pages = new POGuidePage[] {
                    new FileFilterPage(),
                    new MetadataMappingPage()
            };
        }
        return _pages;
    }

}
