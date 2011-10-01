package info.photoorganizer.gui.appconfig.autoindexing;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.autoindexing.MetadataMappingConfiguration;
import info.photoorganizer.database.autoindexing.MetadataMappingConfigurationInterface;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.appconfig.autoindexing.EditTextTransformationDialog.TextTransformationOption;
import info.photoorganizer.gui.components.frame.POCloseReason;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.POAction;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.metadata.PhotoFileMetadataTag;
import info.photoorganizer.metadata.TagDefinition;
import info.photoorganizer.util.transform.TextCaseTransformer;
import info.photoorganizer.util.transform.TextTransformer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

class MetadataMappingPage extends Page
    {
        /**
         * 
         */
        private final EditIndexingConfigurationDialog _editIndexingConfigurationDialog;
        private final Database _database;

        /**
         * @param editIndexingConfigurationDialog
         */
        public MetadataMappingPage(EditIndexingConfigurationDialog editIndexingConfigurationDialog, Database database)
        {
            _editIndexingConfigurationDialog = editIndexingConfigurationDialog;
            _database = database;
        }

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
                for (MetadataMappingConfigurationInterface mapper : _editIndexingConfigurationDialog.getConfiguration().getMetadataMappers())
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
                        EditTextTransformationDialog dialog = new EditTextTransformationDialog(_editIndexingConfigurationDialog, TextTransformationOption.REPLACE);
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
                        EditTextTransformationDialog dialog = new EditTextTransformationDialog(_editIndexingConfigurationDialog, (TextTransformer) transformationsListMode.get(index));
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
            JComboBox targetDropDown = new JComboBox(_database.getTagDefinitions().toArray());
            targetDropDown.setSelectedItem(mappingCfg.getTarget(_database));
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