package info.photoorganizer.gui.appconfig.autoindexing;

import info.photoorganizer.gui.components.frame.POGuidePage;

abstract class Page extends POGuidePage
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