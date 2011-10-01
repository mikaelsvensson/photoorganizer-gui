package info.photoorganizer.gui.search;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class FolderContentMatchProvider implements MatchProvider
{
    private static final Logger L = info.photoorganizer.util.Log.getLogger(MatchProvider.class);

    private class FolderContentMatchIterator implements Iterator<Match>
    {
        private Queue<File> _files = null;

        private FolderContentMatchIterator(File folder)
        {
            super();
            addFilesForFolder(folder);
        }

        @Override
        public boolean hasNext()
        {
            return !_files.isEmpty();
        }

        @Override
        public Match next()
        {
            return new FileMatch(_files.poll(), _database);
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
        private void addFilesForFolder(File folder)
        {
            _files = new LinkedList<File>();
            for (File f : folder.listFiles())
            {
                if (f.isFile())
                {
                    _files.offer(f);
                }
            }
        }

    }

    private Database _database = null;
    
    private File _folder = null;

    public FolderContentMatchProvider(File folder, Database database)
    {
        super();
        _folder = folder;
        _database = database;
        L.fine("FolderContentMatchProvider for " + _folder + " has been CREATED.");
    }

    @Override
    public Iterator<Match> getItems()
    {
        return new FolderContentMatchIterator(_folder);
    }

}
