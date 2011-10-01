package info.photoorganizer.gui.window.config;

import info.photoorganizer.gui.shared.POPanel;

public abstract class POConfigPanel extends POPanel
{
    
    public abstract void load();
    public abstract void save() throws Exception;
    
    public String getTitle()
    {
        return getI18nText(POConfigPanel.class, getClass().getName());
    }
    
    @Override
    public String toString()
    {
        return getI18nText("TITLE");
    }
    
    protected Config getOwner()
    {
        return (Config) getRootPane().getParent();
    }
    
}
