package info.photoorganizer.gui;

import info.photoorganizer.metadata.Photo;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class PhotoSelectionEvent extends EventObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<Photo> _added = new ArrayList<Photo>();
    private List<Photo> _removed = new ArrayList<Photo>();
    
    public PhotoSelectionEvent(Object source)
    {
        super(source);
    }
    
    public PhotoSelectionEvent(Object source, boolean photosWereAdded, Photo... photos)
    {
        this(source);
        if (photosWereAdded)
        {
            for (Photo p : photos) _added.add(p);
        }
        else
        {
            for (Photo p : photos) _removed.add(p);
        }
    }
    
    public PhotoSelectionEvent(Object source, Photo added, Photo removed)
    {
        this(source);
        _added.add(added);
        _removed.add(removed);
    }
    
    public PhotoSelectionEvent(Object source, List<Photo> added, List<Photo> removed)
    {
        this(source);
        _added.addAll(added);
        _removed.addAll(removed);
    }

    public synchronized List<Photo> getAdded()
    {
        return _added;
    }

    public synchronized List<Photo> getRemoved()
    {
        return _removed;
    }
    
    public boolean isAdded(Photo p)
    {
        return _added.contains(p);
    }
    
    public boolean isRemoved(Photo p)
    {
        return _removed.contains(p);
    }
}
