package info.photoorganizer.gui.search;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

public abstract class ResultItemProducer extends SwingWorker<List<ResultItem>, List<ResultItem>>
{

    private List<ResultItem> _res = null;

    private Search _search = null;
    public ResultItemProducer()
    {
        this(null);
    }
    
    public ResultItemProducer(Search search)
    {
        super();
        _search = search;
    }
    
    protected synchronized void addResultItem(ResultItem item)
    {
        _res.add(item);
        
        int size = _res.size();
        if (publishInterimResult(size))
        {
            publish(_res); // Publish accumulated results...
            _res = new ArrayList<ResultItem>(); // ...and create a new list in which future results will be put.
        }
    }
    
    @Override
    protected List<ResultItem> doInBackground() throws Exception
    {
        _res = new ArrayList<ResultItem>();
        doInBackgroundImpl();
        return _res;
    }

    protected abstract void doInBackgroundImpl();

    @Override
    protected void done()
    {
        if (_res != null)
        {
            updateSearchResult(_res);
        }
    }

    @Override
    protected void process(List<List<ResultItem>> chunks)
    {
        //System.err.println("Interim results: " + chunks.toString());
        for (List<ResultItem> items : chunks)
        {
            updateSearchResult(items);
        }
    }

    protected boolean publishInterimResult(int i)
    {
        return i == 100;
//        double exp = Math.pow(10, Math.min(3, Math.floor(Math.log10(i))));
//        return i % exp == 0;
    }

    public void setSearch(Search search)
    {
        _search = search;
    }
    
    private void updateSearchResult(List<ResultItem> items)
    {
        System.err.println("Adding "+ items.size() + " results.");
        _search.addResult(items);
    }
}
