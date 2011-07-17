package info.photoorganizer.gui.search;

import java.util.EventObject;
import java.util.List;

public class SearchResultEvent extends EventObject
{
    
    private List<Match> _newMatches = null;

    public synchronized List<Match> getNewMatches()
    {
        return _newMatches;
    }

    public SearchResultEvent(Object source)
    {
        super(source);
    }
    
    public SearchResultEvent(Object source, List<Match> newMatches)
    {
        super(source);
        _newMatches = newMatches;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 5835859709095768528L;

}
