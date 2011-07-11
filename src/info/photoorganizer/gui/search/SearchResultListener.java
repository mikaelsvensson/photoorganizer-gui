package info.photoorganizer.gui.search;

import java.util.EventListener;

public interface SearchResultListener extends EventListener
{
    void itemsAdded(SearchResultEvent event);
}
