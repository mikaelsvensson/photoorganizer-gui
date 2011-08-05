package info.photoorganizer.gui.components.thumblist;

import java.io.File;
import java.util.EventObject;
import java.util.List;

public class SelectionEvent extends EventObject
{

    private List<File> _selectedFiles = null;
    
    public SelectionEvent(Object source, List<File> selectedFiles)
    {
        super(source);
        _selectedFiles = selectedFiles;
    }

    public synchronized List<File> getSelectedFiles()
    {
        return _selectedFiles;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SelectionEvent(Object source)
    {
        super(source);
    }

    @Override
    public POThumbList getSource()
    {
        return (POThumbList) super.getSource();
    }

}
