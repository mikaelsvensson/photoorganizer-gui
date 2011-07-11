package info.photoorganizer.gui.components.thumblist;

import java.util.EventListener;

public interface ListItemListener extends EventListener
{
    void listItemChanged(ListItemEvent event);
}
