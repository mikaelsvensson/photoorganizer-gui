package info.photoorganizer.gui.search;

import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class IndexerEvent extends EventObject
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private List<File> _indexedFiles = new ArrayList<File>();

    public IndexerEvent(Object source, File...files)
    {
        super(source);
        
        for (File f : files)
        {
            _indexedFiles.add(f);
        }
    }
    
    public IndexerEvent(Object source, List<File> files)
    {
        super(source);
        
        _indexedFiles.addAll(files);
    }

    public synchronized List<File> getIndexedFiles()
    {
        return _indexedFiles;
    }

}
