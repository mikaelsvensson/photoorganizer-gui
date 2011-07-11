package info.photoorganizer.gui.search;

import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Search implements Runnable
{
    private List<ResultItem> _result = new ArrayList<ResultItem>();
    private ListItemFilter _query = null;
    private ResultItemProducer[] _producers = null;
    
    private Event<SearchResultListener, SearchResultEvent> _searchResultEvent = new Event<SearchResultListener, SearchResultEvent>(
            new EventExecuter<SearchResultListener, SearchResultEvent>()
            {
                @Override
                public void fire(SearchResultListener listener, SearchResultEvent event)
                {
                    listener.itemsAdded(event);
                }
            });
    
    public synchronized void addResult(List<ResultItem> items)
    {
        _result.addAll(items);
        System.err.println("There are now " + _result.size() + " files to filter.");
        _searchResultEvent.fire(new SearchResultEvent(this));
    }

    public Search (ListItemFilter query, ResultItemProducer... producers)
    {
        _query = query;
        _producers = producers;
    }
    
    public void cancel()
    {
        for (ResultItemProducer prod : _producers)
        {
            prod.cancel(false);
        }
    }
    
    public synchronized List<ResultItem> getResult()
    {
        return Collections.unmodifiableList(_result);
    }
    
    @Override
    public void run()
    {
        for (ResultItemProducer prod : _producers)
        {
            prod.setSearch(this);
            prod.execute();
        }
    }
    
    public void addSearchResultListener(SearchResultListener listener)
    {
        _searchResultEvent.addListener(listener);
    }
    
    public void removeSearchResultListener(SearchResultListener listener)
    {
        _searchResultEvent.removeListener(listener);
    }
}
