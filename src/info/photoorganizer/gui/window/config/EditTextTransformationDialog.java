package info.photoorganizer.gui.window.config;

import info.photoorganizer.database.autoindexing.POFileFilter;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.POAction;
import info.photoorganizer.gui.components.POTwoLevelChoice;
import info.photoorganizer.gui.components.frame.POCloseReason;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.transform.ReplaceTransformer;
import info.photoorganizer.util.transform.TextCaseTransformer;
import info.photoorganizer.util.transform.TextCaseTransformer.Transformation;
import info.photoorganizer.util.transform.TextTransformer;

import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditTextTransformationDialog extends PODialog
{

    public enum TextTransformationOption
    {
        /*NO_TRANSFORMATION
        {
            @Override
            public TextTransformer createFilter()
            {
                return null;
            }
            
            @Override
            protected JPanel createUI(TextTransformer filter)
            {
                return GuiComponentFactory.createBoxLayoutPanel(
                        true, 
                        GuiComponentFactory.createLabel(
                                getI18nString("NO_CONFIGURATION_PARAMETERS")));
            }
            
            @Override
            public boolean handlesTextTransformation(TextTransformer filter)
            {
                return filter == null;
            }
        },
        */
        TEXT_CASE
        {
            JComboBox transformationList = null;
            
            @Override
            public TextTransformer createFilter()
            {
                if (transformationList != null && transformationList.getSelectedItem() != null)
                {
                    TextCaseTransformer.Transformation transformation = (Transformation) transformationList.getSelectedItem();
                    return new TextCaseTransformer(transformation.name());
                }
                else
                {
                    return new TextCaseTransformer(TextCaseTransformer.Transformation.LOWERCASE.name());
                }
            }
            
            @Override
            protected JPanel createUI(final TextTransformer filter)
            {
                TextCaseTransformer.Transformation initialTransformation = Transformation.LOWERCASE;
                if (handlesTextTransformation(filter))
                {
                    TextCaseTransformer textCaseTransformer = (TextCaseTransformer) filter;
                    if (null != textCaseTransformer.getParam())
                    {
                        initialTransformation = Transformation.valueOf(textCaseTransformer.getParam());
                    }
                }
                
                transformationList = GuiComponentFactory.createEnumDropDownList(Transformation.class, initialTransformation, new POActionListener()
                {
                    
                    @Override
                    public void actionPerformedImpl(ActionEvent event)
                    {
                        TextCaseTransformer textCaseTransformer = (TextCaseTransformer) filter;
                        textCaseTransformer.setParam(event.getActionCommand());
                    }
                });
                
                JPanel panel = GuiComponentFactory.createUserOptionsPanel(
                        GuiComponentFactory.createLabel(getI18nString("TRANSFORMATION_LABEL")),
                        transformationList
                );
                return panel;
            }
            
            @Override
            public boolean handlesTextTransformation(TextTransformer filter)
            {
                return filter instanceof TextCaseTransformer;
            }
        },
        REPLACE
        {
            JTextField oldTextField = null;
            JTextField replacementTextField = null;
            
            @Override
            public TextTransformer createFilter()
            {
                return new ReplaceTransformer("a", "b");
            }
            
            @Override
            protected JPanel createUI(final TextTransformer filter)
            {
                String oldText = "";
                String replacementText = "";
                if (handlesTextTransformation(filter))
                {
                    ReplaceTransformer replaceTransformer = (ReplaceTransformer) filter;
                    oldText = replaceTransformer.getParam(ReplaceTransformer.PARAM_OLD);
                    replacementText = replaceTransformer.getParam(ReplaceTransformer.PARAM_REPLACEMENT);
                }
                DocumentListener documentListener = new DocumentListener()
                {
                    
                    @Override
                    public void removeUpdate(DocumentEvent e)
                    {
                        update(e);
                    }
                    
                    @Override
                    public void insertUpdate(DocumentEvent e)
                    {
                        update(e);
                    }
                    
                    @Override
                    public void changedUpdate(DocumentEvent e)
                    {
                        update(e);
                    }
                    
                    private void update(DocumentEvent e)
                    {
                        ReplaceTransformer replaceTransformer = (ReplaceTransformer) filter;
                        replaceTransformer.setParam(ReplaceTransformer.PARAM_OLD, oldTextField.getText());
                        replaceTransformer.setParam(ReplaceTransformer.PARAM_REPLACEMENT, replacementTextField.getText());
                    }
                };
                oldTextField = GuiComponentFactory.createTextField(oldText);
                oldTextField.getDocument().addDocumentListener(documentListener);
                replacementTextField = GuiComponentFactory.createTextField(replacementText);
                replacementTextField.getDocument().addDocumentListener(documentListener);
                JPanel panel = GuiComponentFactory.createUserOptionsPanel(
                        GuiComponentFactory.createLabel(getI18nString("OLD_TEXT_LABEL")),
                        oldTextField,
                        GuiComponentFactory.createLabel(getI18nString("REPLACEMENT_TEXT_LABEL")),
                        replacementTextField
                );
                return panel;
            }
            
            @Override
            public boolean handlesTextTransformation(TextTransformer filter)
            {
                return filter instanceof ReplaceTransformer;
            }
        };
        
        private static final String EXTENSION_SEPARATOR = ",";
        
        private I18n i18n = I18n.getInstance();
        
        public abstract TextTransformer createFilter();
        
        protected abstract JPanel createUI(TextTransformer filter);
        
        public String getI18nString(String key, Object... parameters)
        {
            return i18n.getString(EditTextTransformationDialog.class, TextTransformationOption.class.getSimpleName() + "." + key, parameters);
        }
        
        public abstract boolean handlesTextTransformation(TextTransformer filter);
        
        @Override
        public String toString()
        {
            return getI18nString(name());
        }
        
        public static TextTransformationOption valueOf(TextTransformer transformer)
        {
            for (TextTransformationOption opt : values())
            {
                if (opt.handlesTextTransformation(transformer))
                {
                    return opt;
                }
            }
            return null;
        }
    }
    
    private TextTransformer cfg = null;
//    private boolean editMode = false;
    
    public TextTransformer getConfiguration()
    {
        return cfg;
    }

    public EditTextTransformationDialog(Dialog owner, TextTransformationOption option) throws CloneNotSupportedException
    {
        this(owner, "TITLE_NEW");
        
        cfg = option.createFilter();
        
        initComponents();
    }
    
    private EditTextTransformationDialog(Dialog owner, String title)
    {
        super(owner, title, CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBoxLayoutPanel(false));
    }
    
    public EditTextTransformationDialog(Dialog owner,
            TextTransformer initialOptionConfiguration)
            throws CloneNotSupportedException
    {
        this(owner, "TITLE_EDIT");
        
        TextTransformationOption option = TextTransformationOption.valueOf(initialOptionConfiguration);
        if (null != option)
        {
            cfg = initialOptionConfiguration.cloneDeep();
//            editMode = true;
        }
        else
        {
            throw new UnsupportedOperationException("Cannot edit transformer of type " + option.getClass().getName() + ".");
        }
        
        initComponents();
    }

    private POTwoLevelChoice<TextTransformationOption, JPanel> _options = null;

    private POTwoLevelChoice<TextTransformationOption, JPanel> getOptions()
    {
        if (null == _options)
        {
            LinkedHashMap<TextTransformationOption, JPanel> choices = new LinkedHashMap<TextTransformationOption, JPanel>();
            for (TextTransformationOption filterOption : TextTransformationOption.values())
            {
                TextTransformer filter = null; 
                if (filterOption.handlesTextTransformation(cfg))
                {
                    filter = cfg;
                }
                else
                {
                    filter = filterOption.createFilter();
                }
                JPanel filterPanel = filterOption.createUI(filter);
                filterPanel.putClientProperty("FILTER_MODEL", filter);
                choices.put(filterOption, filterPanel);
            }
            _options = new POTwoLevelChoice<TextTransformationOption, JPanel>(choices);
            _options.setSelected(TextTransformationOption.valueOf(cfg));
        }
        return _options;
    }

    private void initComponents()
    {
        final POAction okAction = new POAction(getI18nText("BUTTON_OK_LABEL"))
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                TextTransformer item = (TextTransformer)_options.getSelectedChoiceValue().getClientProperty("FILTER_MODEL");
                cfg = item;
                dispose(POCloseReason.OK);
            }
        };
        final POAction cancelAction = new POAction(getI18nText("BUTTON_CANCEL_LABEL"))
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose(POCloseReason.CANCEL);
            }
        };
        
//        TextTransformationOption option = TextTransformationOption.valueOf(cfg);
//        JComboBox transformersList = GuiComponentFactory.createEnumDropDownList(TextTransformationOption.class, option, new POActionListener()
//        {
//            
//            @Override
//            public void actionPerformedImpl(ActionEvent event)
//            {
//                if (getRootPanel().getComponentCount() > 0)
//                {
//                    TextTransformationOption option = TextTransformationOption.valueOf(event.getActionCommand());
//                    showOptionConfigPanel(option, null);
//                }
//            }
//        });
//        
//        transformersList.setEnabled(!editMode);
        
//        add(transformersList);
        add(getOptions());
        add(GuiComponentFactory.createFlowLayoutPanel(FlowLayoutAlignment.LEFT, 
                GuiComponentFactory.createButton(okAction),
                GuiComponentFactory.createButton(cancelAction)
                ));
//        showOptionConfigPanel(cfg);
    }
    
//    private CardLayout cardLayout = new CardLayout();
//    
//    private JPanel optionConfigPanel = null;
//    
//    private HashMap<TextTransformationOption, TextTransformer> options = new HashMap<EditTextTransformationDialog.TextTransformationOption, TextTransformer>();
//    
//    private void showOptionConfigPanel(TextTransformer initialConfig)
//    {
//        showOptionConfigPanel(TextTransformationOption.valueOf(initialConfig), initialConfig);
//    }
//    
//    private void showOptionConfigPanel(TextTransformationOption option, TextTransformer initialConfig)
//    {
//        if (!options.containsKey(option))
//        {
//            options.put(option, null != initialConfig ? initialConfig : option.createFilter());
//        }
//        
//        cfg = options.get(option);
//        
//        if (null != optionConfigPanel)
//        {
//            remove(optionConfigPanel);
//        }
//        optionConfigPanel = option.createUI(cfg);
//        add(optionConfigPanel, 1);
//        validate();
//        pack();
//    }

}
