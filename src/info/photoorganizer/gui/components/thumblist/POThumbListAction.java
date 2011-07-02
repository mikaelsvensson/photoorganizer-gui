package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.gui.POAction;

public abstract class POThumbListAction extends POAction
{
    protected POThumbList _component = null;

    protected POThumbListAction(POThumbList component)
    {
        super();
        _component = component;
    }
}