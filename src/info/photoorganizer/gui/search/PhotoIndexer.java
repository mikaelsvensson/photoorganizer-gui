package info.photoorganizer.gui.search;

import info.photoorganizer.database.Database;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.SwingWorker;

public class PhotoIndexer implements Runnable
{
    public synchronized boolean isCancelled()
    {
        return _cancelled;
    }

    private boolean _cancelled = false;

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
    
    public PhotoIndexer(Database database)
    {
        super();
        _database = database;
    }
    
    public void addIndexerEventListener(IndexerEventListener listener)
    {
        _event.addListener(listener);
    }

    public void removeIndexerEventListener(IndexerEventListener listener)
    {
        _event.removeListener(listener);
    }
    
    public void addToQueue(File f)
    {
        synchronized (_queue)
        {
            if (!_queue.contains(f))
            {
                _queue.offer(f);
                _queue.notifyAll();
            }
            else
            {
                System.err.println("File " + f + " already queued for indexing.");
            }
        }
    }

    @Override
    public void run()
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
                        _database.indexImage(f);
                        _event.fire(new IndexerEvent(this, f));
                        //publish(f);
                        
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
    }
    
    public void cancel()
    {
        _cancelled = true;
//        synchronized (_queue)
//        {
//            _queue.notifyAll();
//        }
    }

}
