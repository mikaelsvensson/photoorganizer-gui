package info.photoorganizer.gui.search;

import info.photoorganizer.gui.components.thumblist.ListItem;

public interface SearchResult
{
    void add(ListItem... item); // ?
    void addListener(SearchResultListener listener); // ?
    void stop();
}
