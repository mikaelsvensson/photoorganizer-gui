package info.photoorganizer.gui.search;

import info.photoorganizer.gui.components.thumblist.ListItem;

public interface ListItemFilter
{
    boolean accept(ListItem item);
}
