package info.photoorganizer.gui.components.frame;

import info.photoorganizer.gui.shared.POPanel;

public abstract class POGuidePage extends POPanel
{

    public POGuidePage()
    {
        super();
    }
    
    protected boolean onShow()
    {
        return true;
    }
    
    protected boolean onOK()
    {
        return true;
    }

    protected abstract void initComponents();
    
    public abstract String getDescription();
    
}
