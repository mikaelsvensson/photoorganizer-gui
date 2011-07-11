package info.photoorganizer.gui.search;

import info.photoorganizer.database.Database;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.SwingWorker;

public class Indexer extends SwingWorker<Void, File>
{

    private Database _database = null;
    
    private Event<IndexerEventListener, IndexerEvent> _event = new Event<IndexerEventListener, IndexerEvent>(
            new EventExecuter<IndexerEventListener, IndexerEvent>()
            {
                @Override
                public void fire(IndexerEventListener listener, IndexerEvent event)
                {
                    listener.fileIndexed(event);
                }
            });
    
    private Queue<File> _queue = new LinkedList<File>();
    
    public Indexer(Database database)
    {
        super();
        _database = database;
    }
    
    public void addIndexerEventListener(IndexerEventListener listener)
    {
        _event.addListener(listener);
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        while (!isCancelled())
        {
            try
            {
                while (true)
                {
                    File f = null;
                    synchronized (_queue)
                    {
                        f = _queue.poll();
                    }
                    if (f != null)
                    {
                        _database.indexPhoto(f);
                        publish(f);
                        
                        if (isCancelled())
                        {
                            throw new InterruptedException();
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                synchronized (_queue)
                {
                    _queue.wait(1000);
                }
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void process(List<File> chunks)
    {
        _event.fire(new IndexerEvent(this, chunks));
    }

    public void removeIndexerEventListener(IndexerEventListener listener)
    {
        _event.removeListener(listener);
    }
    
    public void addToQueue(File f)
    {
        synchronized (_queue)
        {
            _queue.offer(f);
            _queue.notifyAll();
        }
    }

}
