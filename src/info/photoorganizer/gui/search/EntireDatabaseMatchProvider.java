package info.photoorganizer.gui.search;

import info.photoorganizer.database.Database;
import info.photoorganizer.metadata.Photo;

import java.util.Iterator;

public class EntireDatabaseMatchProvider implements MatchProvider
{
    private class EntireDatabaseMatchIterator implements Iterator<Match>
    {

        private Iterator<Photo> _photos;

        public EntireDatabaseMatchIterator(Database database)
        {
            _photos = database.getPhotos().iterator();
        }

        @Override
        public boolean hasNext()
        {
            return _photos.hasNext();
        }

        @Override
        public Match next()
        {
            return new DatabaseMatch(_photos.next());
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
    }

    private Database _database = null;
    
    public EntireDatabaseMatchProvider(Database database)
    {
        super();
        _database = database;
    }

    @Override
    public Iterator<Match> getItems()
    {
        return new EntireDatabaseMatchIterator(_database);
    }

}
