package info.photoorganizer.gui.components.frame;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseManager;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.tagfield.POTagFieldSuggestionProvider;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.KeyModifiers;
import info.photoorganizer.gui.shared.KeyboardShortcutDefinition;
import info.photoorganizer.gui.shared.Keys;
import info.photoorganizer.gui.shared.KeywordSuggestionProvider;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.config.ConfigurationProperty;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public abstract class PODialog extends JDialog
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private POCloseReason _closeReason = POCloseReason.OK;

    private Database _database = null;

    private POTagFieldSuggestionProvider<KeywordTagDefinition> _keywordWordprovider = null;
    
    protected PODialog(Dialog owner, String title, boolean modal, CloseOperation defaultCloseOperation, Container root)
    {
        this(owner, title, modal, -1, -1, defaultCloseOperation, root);
    }
    protected PODialog(Dialog owner, String title, boolean modal, int width, int height, CloseOperation defaultCloseOperation, Container root)
    {
        super(owner, modal);
        init(title, width, height, defaultCloseOperation, root);
    }
    
    protected PODialog(Dialog owner, String title, CloseOperation defaultCloseOperation, Container root)
    {
        this(owner, title, true, -1, -1, defaultCloseOperation, root);
    }

    protected PODialog(Frame owner, String title, boolean modal, CloseOperation defaultCloseOperation, Container root)
    {
        this(owner, title, modal, -1, -1, defaultCloseOperation, root);
    }
    
    protected PODialog(Frame owner, String title, boolean modal, int width, int height, CloseOperation defaultCloseOperation, Container root)
    {
        super(owner, modal);
        init(title, width, height, defaultCloseOperation, root);
    }
    
    protected PODialog(Frame owner, String title, CloseOperation defaultCloseOperation, Container root)
    {
        this(owner, title, true, -1, -1, defaultCloseOperation, root);
    }
    
    protected PODialog(String title, CloseOperation defaultCloseOperation, Container root)
    {
        this((Frame)null, title, false, -1, -1, defaultCloseOperation, root);
    }
    
    protected PODialog(String title, int width, int height, CloseOperation defaultCloseOperation, Container root)
    {
        this((Frame)null, title, false, width, height, defaultCloseOperation, root);
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
    
    protected ActionMap getActionMap()
    {
        JPanel contentPane = (JPanel)getContentPane();
        return contentPane.getActionMap();
    }

    public POCloseReason getCloseReason()
    {
        return _closeReason;
    }
    
    protected Database getDatabase()
    {
        if (null == _database)
        {
            _database = DatabaseManager.getInstance().openDatabase(ConfigurationProperty.dbPath.get());
        }
        return _database;
    }
    
    protected String getI18nText(Class<?> bundle, String key)
    {
        return I18n.getInstance().getString(bundle, key);
    }

    protected String getI18nText(String key, Object... parameters)
    {
        return I18n.getInstance().getString(getClass(), key, parameters);
    }
    
    public POTagFieldSuggestionProvider<KeywordTagDefinition> getKeywordWordprovider()
    {
        if (null == _keywordWordprovider)
        {
            _keywordWordprovider = new KeywordSuggestionProvider(getDatabase().getRootKeyword());
        }
        return _keywordWordprovider;
    }
    
    protected JPanel getRootPanel()
    {
        return (JPanel) getContentPane();
    }

    private void init(String title,
            int width,
            int height,
            CloseOperation defaultCloseOperation,
            Container root)
    {
        setContentPane(root);
        setTitle(getI18nText(title));
        setDefaultCloseOperation(defaultCloseOperation.getValue());
        if (width >= 0 && height >= 0)
        {
            setSize(width, height);
            recalculateContentPaneSize();
        }
    }
    
    private void recalculateContentPaneSize()
    {
        getContentPane().setPreferredSize(getSize());
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

    public void setCloseReason(POCloseReason closeReason)
    {
        _closeReason = closeReason;
    }
    
    protected void setDefaultButton(JButton button)
    {
        getRootPane().setDefaultButton(button);
    }

    protected void dispose(POCloseReason reason)
    {
        setCloseReason(reason);
        dispose();
    }

    public POCloseReason showModal()
    {
        boolean modal = isModal();
        setModal(true);
        GuiComponentFactory.showModalDialog(this);
        setModal(modal);
        return getCloseReason();
    }
    
}
