package info.photoorganizer.gui.appconfig.autoindexing;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.autoindexing.DefaultIndexingConfiguration;
import info.photoorganizer.database.autoindexing.IndexingConfigurationInterface;
import info.photoorganizer.gui.components.frame.POGuideDialog;
import info.photoorganizer.gui.components.frame.POGuidePage;

import java.awt.Dialog;

public class EditIndexingConfigurationDialog extends POGuideDialog
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private POGuidePage[] _pages = null;

    private IndexingConfigurationInterface cfg = null;

    public EditIndexingConfigurationDialog(Dialog owner, Database database)
            throws CloneNotSupportedException
    {
        this(owner, null, database);
    }

    public EditIndexingConfigurationDialog(Dialog owner,
            IndexingConfigurationInterface originalConfiguration,
            Database database)
            throws CloneNotSupportedException
    {
        super(owner, null != originalConfiguration ? "TITLE_EDIT" : "TITLE_NEW", database);

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
                    new FileFilterPage(getConfiguration()),
                    new MetadataMappingPage(this, getDatabase())
            };
        }
        return _pages;
    }

}
