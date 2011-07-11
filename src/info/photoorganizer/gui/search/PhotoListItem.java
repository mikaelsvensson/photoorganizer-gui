package info.photoorganizer.gui.search;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.components.thumblist.ListItem;
import info.photoorganizer.gui.components.thumblist.ListItemEvent;
import info.photoorganizer.gui.components.thumblist.ListItemEventProvider;
import info.photoorganizer.gui.components.thumblist.ListItemListener;
import info.photoorganizer.metadata.Tag;
import info.photoorganizer.metadata.TagDefinition;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PhotoListItem implements ListItem, IndexerEventListener, ListItemEventProvider
{
    private Event<ListItemListener, ListItemEvent> _event = new Event<ListItemListener, ListItemEvent>(
            new EventExecuter<ListItemListener, ListItemEvent>()
            {

                @Override
                public void fire(ListItemListener listener, ListItemEvent event)
                {
                    listener.listItemChanged(event);
                }
            });
    
    private Map<Object, Object> _metadata = null;
    //private Indexer _indexer = null;
    private PhotoIndexer _indexer = null;
    private Database _database = null;
    
    //public PhotoListItem(ResultItem resultItem, Indexer indexer)
    public PhotoListItem(ResultItem resultItem, PhotoIndexer indexer, Database database)
    {
        super();
        _resultItem = resultItem;
        _indexer = indexer;
        _database = database;
    }

    private ResultItem _resultItem = null;

    @Override
    public Image getImage(Dimension preferredSize)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Object, Object> getMetadata()
    {
        info.photoorganizer.metadata.Photo image = _resultItem.getPhoto();
        if (image != null)
        {
            if (null == _metadata)
            {
                _metadata = new HashMap<Object, Object>();
                Iterator<Tag<? extends TagDefinition>> tags = image.getTags();
                while (tags.hasNext())
                {
                    Tag<? extends TagDefinition> tag = tags.next();
                    _metadata.put(tag.getDefinition().getName(), tag.toString());
                }
                //_indexer.removeIndexerEventListener(this);
            }
        }
        else
        {
            // Queue image for indexing
            _indexer.addIndexerEventListener(this);
            _indexer.addToQueue(getFile());
        }
        return _metadata;
    }

    @Override
    public File getFile()
    {
        return _resultItem.getPhotoFile();
    }

    @Override
    public void fileIndexed(IndexerEvent event)
    {
        for (File f : event.getIndexedFiles())
        {
            if (f.equals(getFile()))
            {
                _resultItem = new ResultItem(_database.getPhoto(f));
                _event.fire(new ListItemEvent(this));
            }
        }
    }

    @Override
    public void addListItemEventListener(ListItemListener listener)
    {
        _event.addListener(listener);
    }

    @Override
    public void removeListItemEventListener(ListItemListener listener)
    {
        _event.removeListener(listener);
    }

}
