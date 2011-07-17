package info.photoorganizer.gui.search;

import info.photoorganizer.metadata.Photo;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

public class Search extends SwingWorker<Void, Match>
{
    private Event<SearchResultListener, SearchResultEvent> _searchResultEvent = new Event<SearchResultListener, SearchResultEvent>(
            new EventExecuter<SearchResultListener, SearchResultEvent>()
            {
                @Override
                public void fire(SearchResultListener listener, SearchResultEvent event)
                {
                    listener.itemsAdded(event);
                }
            });
    
    private List<MatchProvider> _providers = new ArrayList<MatchProvider>();
    private SearchCriterion _criterion = null;

    @Override
    protected Void doInBackground() throws Exception
    {
        for (MatchProvider provider : _providers)
        {
            Iterator<Match> items = provider.getItems();
            while (items.hasNext() && !isCancelled())
            {
                Match match = items.next();
//                System.err.println("-------------------------------------------------------------- " + this + ".isCancelled()=" + isCancelled() + " when processing " + match);
                if (_criterion.accept(match.getPhoto()))
                {
                    publish(match);
                }
            }
        }
        return null;
    }

    public Search(List<MatchProvider> providers, SearchCriterion criterion)
    {
        super();
        _providers = providers;
        if (null == criterion)
        {
            criterion = new SearchCriterion()
            {
                @Override
                public boolean accept(Photo photo)
                {
                    return true;
                }
            };
        }
        _criterion = criterion;
    }

    @Override
    protected void process(List<Match> chunks)
    {
        _searchResultEvent.fire(new SearchResultEvent(this, chunks));
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
