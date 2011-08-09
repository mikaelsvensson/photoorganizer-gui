package info.photoorganizer.gui.search;

import java.util.EventListener;

public interface SearchListener extends EventListener
{
    void searchResultFound(SearchResultEvent event);
    void searchStarted(SearchResultEvent event);
}
