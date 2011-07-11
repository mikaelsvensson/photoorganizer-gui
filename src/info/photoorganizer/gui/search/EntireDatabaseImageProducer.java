package info.photoorganizer.gui.search;

import info.photoorganizer.database.Database;
import info.photoorganizer.metadata.Image;

import java.util.Iterator;

public class EntireDatabaseImageProducer extends ResultItemProducer
{
    public EntireDatabaseImageProducer(Database database)
    {
        super();
        _database = database;
    }

    private Database _database = null;

    @Override
    protected void doInBackgroundImpl()
    {
        Iterator<Image> images = _database.getImages();
        
        while (images.hasNext() && !isCancelled())
        {
            addResultItem(new ResultItem(images.next()));
        }
        
        if (isCancelled())
        {
            System.err.println("EntireDatabaseImageProducer has been cancelled.");
        }
    }
    
}
