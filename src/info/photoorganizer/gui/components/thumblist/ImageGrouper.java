package info.photoorganizer.gui.components.thumblist;

import java.util.List;

public interface ImageGrouper
{
    String getName();
    String[] getHeaders();
    ListItem[] getItems(String header);
    List<ImageGroup> group(Iterable<ListItem> items);
}
