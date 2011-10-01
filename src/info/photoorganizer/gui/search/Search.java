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
    private Event<SearchListener, SearchResultEvent> _searchResultFound = new Event<SearchListener, SearchResultEvent>(
            new EventExecuter<SearchListener, SearchResultEvent>()
            {
                @Override
                public void fire(SearchListener listener, SearchResultEvent event)
                {
                    listener.searchResultFound(event);
                }
            });
    private Event<SearchListener, SearchResultEvent> _searchStartedEvent = new Event<SearchListener, SearchResultEvent>(
            new EventExecuter<SearchListener, SearchResultEvent>()
            {
                @Override
                public void fire(SearchListener listener, SearchResultEvent event)
                {
                    listener.searchStarted(event);
                }
            });
    
    private List<MatchProvider> _providers = new ArrayList<MatchProvider>();
    private SearchCriterion _criterion = null;
    private boolean _firedStartedEvent = false;

    @Override
    protected Void doInBackground()/* throws Exception*/
    {
        try
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
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        if (!_firedStartedEvent)
        {
            _searchStartedEvent.fire(new SearchResultEvent(this));
            _firedStartedEvent = true;
        }
        _searchResultFound.fire(new SearchResultEvent(this, chunks));
    }
    
    public void addSearchResultListener(SearchListener listener)
    {
        _searchResultFound.addListener(listener);
        _searchStartedEvent.addListener(listener);
    }
    
    public void removeSearchResultListener(SearchListener listener)
    {
        _searchResultFound.removeListener(listener);
        _searchStartedEvent.removeListener(listener);
    }

    public List<SearchListener> getListeners()
    {
        return _searchResultFound.getListeners();
    }
    
    public void removeSearchResultListeners()
    {
        _searchResultFound.removeAllListeners();
        _searchStartedEvent.removeAllListeners();
    }
}
