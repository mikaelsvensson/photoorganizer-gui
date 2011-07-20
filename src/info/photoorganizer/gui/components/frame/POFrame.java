package info.photoorganizer.gui.components.frame;

import java.awt.Container;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseManager;
import info.photoorganizer.gui.components.tagfield.POTagFieldSuggestionProvider;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.KeywordSuggestionProvider;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.config.ConfigurationProperty;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class POFrame extends JFrame
{
    private Database _database = null;
    
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
    
    protected String getI18nText(String key)
    {
        return I18n.getInstance().getString(getClass(), key);
    }

    protected JPanel getRootPanel()
    {
        return (JPanel) getContentPane();
    }
    protected POFrame(PODialog owner, String title, boolean modal, CloseOperation defaultCloseOperation, Container root)
    {
        this(owner, title, modal, -1, -1, defaultCloseOperation, root);
    }
    
    protected POFrame(PODialog owner, String title, boolean modal, int width, int height, CloseOperation defaultCloseOperation, Container root)
    {
        super();
        setContentPane(root);
        setTitle(getI18nText(title));
        setDefaultCloseOperation(defaultCloseOperation.getValue());
        if (width >= 0 && height >= 0)
        {
            setSize(width, height);
//            recalculateContentPaneSize();
        }
    }
    
    private POTagFieldSuggestionProvider<KeywordTagDefinition> _keywordWordprovider = null;

    public POTagFieldSuggestionProvider<KeywordTagDefinition> getKeywordWordprovider()
    {
        if (null == _keywordWordprovider)
        {
            _keywordWordprovider = new KeywordSuggestionProvider(getDatabase().getRootKeyword());
        }
        return _keywordWordprovider;
    }
    
    protected POFrame(PODialog owner, String title, CloseOperation defaultCloseOperation, Container root)
    {
        this(owner, title, true, -1, -1, defaultCloseOperation, root);
    }
    
    protected POFrame(String title, CloseOperation defaultCloseOperation, Container root)
    {
        this(null, title, false, -1, -1, defaultCloseOperation, root);
    }
    
    protected POFrame(String title, int width, int height, CloseOperation defaultCloseOperation, Container root)
    {
        this(null, title, false, width, height, defaultCloseOperation, root);
    }
}
