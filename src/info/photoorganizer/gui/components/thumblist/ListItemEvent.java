package info.photoorganizer.gui.components.thumblist;

import java.util.EventObject;

public class ListItemEvent extends EventObject
{

    @Override
    public ListItem getSource()
    {
        return (ListItem) super.getSource();
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ListItemEvent(Object source)
    {
        super(source);
    }

}
