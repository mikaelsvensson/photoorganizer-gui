package info.photoorganizer.gui.components.thumblist;

import java.util.List;
import java.util.Map;

public interface ImageGrouper
{
    String getName();
    String[] getHeaders();
    ListItem[] getItems(String header);
    List<ImageGroup> group(Iterable<ListItem> items);
}
