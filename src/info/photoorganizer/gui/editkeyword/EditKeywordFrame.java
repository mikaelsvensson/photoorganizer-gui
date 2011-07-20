package info.photoorganizer.gui.editkeyword;

import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.WordInfo;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EditKeywordFrame extends PODialog
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JButton _cancelButton = null;
    private POActionListener _cancelButtonActionListener = new POActionListener()
    {
        
        @Override
        public void actionPerformedImpl(ActionEvent event)
        {
            dispose();
        }
    };
    
    private KeywordTagDefinition _keyword = null;
    
    private JTextField _nameField = null;
    
    private POTagField<KeywordTagDefinition> _synonymsField = null;
    
    private JButton _okButton = null;
    
    private JPanel _typeButtonGroup = null;
    
    private JTextField _locationCity = null;
    private JTextField _locationCountry = null;
    private JTextField _locationLatitude = null;
    
    public JTextField getLocationCity()
    {
        if (null == _locationCity)
        {
            _locationCity = GuiComponentFactory.createTextField("");
        }
        return _locationCity;
    }

    public JTextField getLocationCountry()
    {
        if (null == _locationCountry)
        {
            _locationCountry = GuiComponentFactory.createTextField("");
        }
        return _locationCountry;
    }

    public JTextField getLocationLatitude()
    {
        if (null == _locationLatitude)
        {
            _locationLatitude = GuiComponentFactory.createTextField("");
        }
        return _locationLatitude;
    }

    public JTextField getLocationLongitude()
    {
        if (null == _locationLongitude)
        {
            _locationLongitude = GuiComponentFactory.createTextField("");
        }
        return _locationLongitude;
    }

    private JTextField _locationLongitude = null;
    
    public JPanel getTypeButtonGroup()
    {
        if (null == _typeButtonGroup)
        {
//            ButtonGroup buttonGroup = createButtonGroup(KeywordType.class, _typeActionListener);
//            AbstractButton[] radioButtons = new AbstractButton[buttonGroup.getButtonCount()];
//            Enumeration<AbstractButton> elements = buttonGroup.getElements();
//            int i = 0;
//            while (elements.hasMoreElements())
//            {
//                radioButtons[i++] = elements.nextElement();
//            }
//            _typeButtonGroup = createBoxLayoutPanel(true, radioButtons);
            
            
//            HashMap<Object, JPanel> choices = new HashMap<Object, JPanel>();
//            choices.put(KeywordExtension.LOCATION, createUserOptionsPanel(
//                    createLabel(getI18nText("LOCATION_CITY")), getLocationCity(),
//                    createLabel(getI18nText("LOCATION_COUNTRY")), getLocationCountry(),
//                    createLabel(getI18nText("LOCATION_LATITUDE")), getLocationLatitude(),
//                    createLabel(getI18nText("LOCATION_LONGITUDE")), getLocationLongitude()
//                    ));
//            _typeButtonGroup = createAccordion(choices);
        }
        return _typeButtonGroup;
    }
    
//    private POActionListener _typeActionListener = new POActionListener()
//    {
//        
//        @Override
//        public void actionPerformedImpl(ActionEvent event)
//        {
//            System.out.println(KeywordExtension.valueOf(event.getActionCommand()).ordinal());
//        }
//    };

    private POActionListener _okButtonActionListener = new POActionListener()
    {
        
        @Override
        public void actionPerformedImpl(ActionEvent event)
        {
            okButton_actionPerformedImpl();
        }

    };
    
    private void okButton_actionPerformedImpl()
    {
        _keyword.setName(getNameField().getText());
        
        _keyword.removeAllSynonyms();
        for (WordInfo word : getSynonymsField().getWords())
        {
            KeywordTagDefinition rootKeyword = getDatabase().getRootKeyword();
            KeywordTagDefinition k = rootKeyword.getChildByName(word.getWord(), true);
            if (null == k)
            {
                k = rootKeyword.addChild(word.getWord());
            }
            try
            {
                KeywordTagDefinition.addSynonym(_keyword, k, true);
            }
            catch (DatabaseStorageException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        dispose();
    }
    
    public EditKeywordFrame(PODialog owner, KeywordTagDefinition keywordToEdit)
    {
        super(owner, "TITLE", CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBoxLayoutPanel(false));
        
        _keyword = keywordToEdit;
        
        initComponents();
    }
    
    public JButton getCancelButton()
    {
        if (null == _cancelButton)
        {
            _cancelButton = GuiComponentFactory.createButton(getI18nText("CANCEL_BUTTON_TEXT"), _cancelButtonActionListener);
        }
        return _cancelButton;
    }
    
    public KeywordTagDefinition getKeyword()
    {
        return _keyword;
    }
    
    public JTextField getNameField()
    {
        if (null == _nameField)
        {
            _nameField = GuiComponentFactory.createTextField(_keyword.getName());
        }
        return _nameField;
    }
    
    public POTagField<KeywordTagDefinition> getSynonymsField()
    {
        if (null == _synonymsField)
        {
            _synonymsField = GuiComponentFactory.createTagField(_keyword.getSynonyms(), 30, getKeywordWordprovider());
        }
        return _synonymsField;
    }
    
    public JButton getOkButton()
    {
        if (null == _okButton)
        {
            _okButton = GuiComponentFactory.createButton(getI18nText("OK_BUTTON_TEXT"), _okButtonActionListener);
        }
        return _okButton;
    }
    
    private void initComponents()
    {
        Container contentPane = getContentPane();
        
        JPanel springLayoutPanel = GuiComponentFactory.createUserOptionsPanel(
                GuiComponentFactory.createLabel(getI18nText("NAME_LABEL")),
                getNameField(),
                GuiComponentFactory.createLabel(getI18nText("SYNONYMS_LABEL")),
                getSynonymsField(),
                GuiComponentFactory.createLabel(getI18nText("TYPE_LABEL")),
                getTypeButtonGroup());
        
        JPanel buttonPanel = GuiComponentFactory.createFlowLayoutPanel(FlowLayoutAlignment.RIGHT, getOkButton(), getCancelButton());
        
        GuiComponentFactory.addComponentsToContainer(contentPane, springLayoutPanel, buttonPanel);
        
        setDefaultButton(getOkButton());
        setCancelButton(getCancelButton());
    }
    
}
