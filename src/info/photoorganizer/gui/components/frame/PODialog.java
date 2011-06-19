package info.photoorganizer.gui.components.frame;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseManager;
import info.photoorganizer.gui.components.PORadioButton;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.tagfield.POTagFieldSuggestionProvider;
import info.photoorganizer.gui.components.view.ViewLayout;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.KeyModifiers;
import info.photoorganizer.gui.shared.KeyboardShortcutDefinition;
import info.photoorganizer.gui.shared.Keys;
import info.photoorganizer.gui.shared.KeywordSuggestionProvider;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.gui.shared.POActionListenerEvent;
import info.photoorganizer.gui.shared.POActionListenerListener;
import info.photoorganizer.gui.shared.SpringUtilities;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.StringUtils;
import info.photoorganizer.util.config.ConfigurationProperty;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class PODialog extends JDialog
{
    private static final String ACCORDION_PANEL = "PODialogAccordionItem";

    private POTagFieldSuggestionProvider<KeywordTagDefinition> _keywordWordprovider = null;

    public POTagFieldSuggestionProvider<KeywordTagDefinition> getKeywordWordprovider()
    {
        if (null == _keywordWordprovider)
        {
            _keywordWordprovider = new KeywordSuggestionProvider(getDatabase().getRootKeyword());
        }
        return _keywordWordprovider;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static void addComponentsToContainer(Container panel, Component... components)
    {
        if (components != null)
        {
            for (Component c : components)
            {
                panel.add(c);
            }
        }
    }
    
    public static JPanel createBorderLayoutPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        return panel;
    }
    
    public static JPanel createViewLayoutPanel()
    {
        JPanel panel = new JPanel(new ViewLayout());
        return panel;
    }
    
    public static JPanel createBoxLayoutPanel(boolean horizontal, Component... components)
    {
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, horizontal ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        
        addComponentsToContainer(panel, components);
        
        return panel;
    }
    
    public static JPanel createFlowLayoutPanel(FlowLayoutAlignment align, Component... components)
    {
        JPanel panel = new JPanel(new FlowLayout(align.getValue()));
        
        addComponentsToContainer(panel, components);
        
        return panel;
    }
    
    public static JPanel createSpringLayoutPanel(int rows, int cols, Component... components)
    {
        JPanel panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        
        if (rows * cols == components.length)
        {
            addComponentsToContainer(panel, components);
            SpringUtilities.makeCompactGrid(panel, rows, cols, 0, 0, 0, 0);
        }
        
        return panel;
    }
    
    public static JPanel createUserOptionsPanel(Component... components)
    {
        int cols = 2; 
        int rows = components.length / cols;
        JPanel panel = createSpringLayoutPanel(rows, cols, components);
        return panel;
    }
    
    public static void show(final PODialog frame)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            
            @Override
            public void run()
            {
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    protected static JButton createButton(String text, POActionListener actionListener)
    {
        final JButton button = new JButton(text);
        if (null != actionListener)
        {
            button.addActionListener(actionListener);
            actionListener.addListener(new POActionListenerListener()
            {
                @Override
                public void actionListenerEnabledChange(POActionListenerEvent event)
                {
                    button.setEnabled(event.getSource().isEnabled());
                }
            });
            button.setEnabled(actionListener.isEnabled());
        }
        return button;
    }
    
    protected static JLabel createLabel(String text)
    {
        JLabel l = new JLabel(text);
        return l;
    }
    
    protected static <T extends Enum<?>> ButtonGroup createButtonGroup(Class<T> cls, POActionListener actionListener)
    {
        ButtonGroup group = new ButtonGroup();
        for (T e : cls.getEnumConstants())
        {
            JRadioButton button = new JRadioButton(e.toString());
            button.addActionListener(actionListener);
            button.setActionCommand(e.name());
            group.add(button);
        }
        return group;
    }
    
    private static ChangeListener _accordionRadioButton_onStateChanged = new ChangeListener()
    {

        @Override
        public void stateChanged(ChangeEvent e)
        {
            JRadioButton source = (JRadioButton) e.getSource();
            JPanel panel = (JPanel) source.getClientProperty(ACCORDION_PANEL);
            panel.setEnabled(source.isSelected());
        }
    };
    
    protected static JPanel createAccordion(Map<Object, JPanel> choices)
    {
        ButtonGroup buttonGroup = new ButtonGroup();

        JComponent[] components = new JComponent[choices.size() * 2];
        int i = 0;
        EmptyBorder panelBorder = null;
        for (Entry<Object, JPanel> entry : choices.entrySet())
        {
            Object labelObject = entry.getKey();
            JPanel panel = entry.getValue();
            
            PORadioButton button = new PORadioButton(labelObject.toString());
            button.setValue(labelObject);
            button.setAlignmentX(0);
            button.addChangeListener(_accordionRadioButton_onStateChanged);
            button.putClientProperty(ACCORDION_PANEL, panel);
            
            if (null == panelBorder)
            {
                panelBorder = new EmptyBorder(0, (int) (1.3 * button.getPreferredSize().getHeight()), 0, 0);
            }
            panel.setAlignmentX(0);
            panel.setBorder(panelBorder);
            
            components[i++] = button;
            components[i++] = panel;
            
            buttonGroup.add(button);
        }
        
        JPanel panel = createBoxLayoutPanel(false, components);
        
        return panel;
    }
    
    protected static JTextField createTextField(String initialText)
    {
        JTextField field = new JTextField(initialText);
        return field;
    }
    
    public POTagField<KeywordTagDefinition> createTagField(KeywordTagDefinition[] tags, int fieldWidth)
    {
        POTagField<KeywordTagDefinition> component = new POTagField<KeywordTagDefinition>(
                StringUtils.join(tags, KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR, true, KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK), 
                fieldWidth);
        component.setQuotationCharacter(KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK);
        component.setWordSeparator(KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR);
        component.setWordProvider(getKeywordWordprovider());
        return component;
    }
    
    protected static void initDefaultLookAndFeel()
    {
        PODialogTheme.init();
        PODialogTheme.applyTheme(PODialogTheme.LIGHT_GREY);
    }

    protected PODialog(PODialog owner, String title, boolean modal, CloseOperation defaultCloseOperation, JPanel rootPanel)
    {
        this(owner, title, modal, -1, -1, defaultCloseOperation, rootPanel);
    }
    
    protected PODialog(PODialog owner, String title, boolean modal, int width, int height, CloseOperation defaultCloseOperation, JPanel rootPanel)
    {
        super(owner, modal);
        setContentPane(rootPanel);
        setTitle(getI18nText(title));
        setDefaultCloseOperation(defaultCloseOperation.getValue());
        if (width >= 0 && height >= 0)
        {
            setSize(width, height);
            recalculateContentPaneSize();
        }
    }
    
    protected PODialog(PODialog owner, String title, CloseOperation defaultCloseOperation, JPanel rootPanel)
    {
        this(owner, title, true, -1, -1, defaultCloseOperation, rootPanel);
    }
    
    protected PODialog(String title, CloseOperation defaultCloseOperation, JPanel rootPanel)
    {
        this(null, title, false, -1, -1, defaultCloseOperation, rootPanel);
    }
    
    protected PODialog(String title, int width, int height, CloseOperation defaultCloseOperation, JPanel rootPanel)
    {
        this(null, title, false, width, height, defaultCloseOperation, rootPanel);
    }
    
    private void recalculateContentPaneSize()
    {
        getContentPane().setPreferredSize(getSize());
    }

    protected void addKeyboardShortcut(POActionListener action, String actionParameter, Keys key, KeyModifiers modifier)
    {
        addKeyboardShortcut(action, actionParameter, key, modifier, null);
    }
    
    protected void addKeyboardShortcut(POActionListener action, String actionParameter, Keys key, KeyModifiers modifier, JComponent owner)
    {
        if (null == owner)
        {
            owner = getRootPane();
        }
        owner.registerKeyboardAction(action, actionParameter, KeyStroke.getKeyStroke(key.getKeyCode(), modifier.getValue()), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    
    protected void addKeyboardShortcuts(KeyboardShortcutDefinition... definitions)
    {
        for (KeyboardShortcutDefinition definition : definitions)
        {
            addKeyboardShortcut(definition.getAction(), definition.getActionParameter(), definition.getKey(), definition.getModifiers(), definition.getOwner());
        }
    }

    private Database _database = null;
    
    protected Database getDatabase()
    {
        if (null == _database)
        {
            _database = DatabaseManager.getInstance().openDatabase(ConfigurationProperty.dbPath.get());
        }
        return _database;
    }
    
    protected String getI18nText(Class bundle, String key)
    {
        return I18n.getInstance().getString(bundle, key);
    }
    
    protected String getI18nText(String key)
    {
        return I18n.getInstance().getString(getClass(), key);
    }

    protected JPanel getRootPanel()
    {
        return (JPanel) getContentPane();
    }
    
    protected void setDefaultButton(JButton button)
    {
        getRootPane().setDefaultButton(button);
    }
    
    protected void setCancelButton(final JButton button)
    {
        addKeyboardShortcut(new POActionListener()
        {
            @Override
            public void actionPerformedImpl(ActionEvent event)
            {
                for (ActionListener al : button.getActionListeners())
                {
                    al.actionPerformed(new ActionEvent(PODialog.this, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        }, null, Keys.ESCAPE, KeyModifiers.NONE);
    }
}
