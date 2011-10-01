package info.photoorganizer.gui.search;

import info.photoorganizer.database.Database;
import info.photoorganizer.metadata.Photo;
import info.photoorganizer.util.config.ConfigurationProperty;

import java.io.File;

public class FileMatch implements Match
{
    private File file = null;
    private Database database = null;

    public FileMatch(File file, Database database)
    {
        super();
        this.file = file;
        this.database = database;
    }

    @Override
    public Photo getPhoto()
    {
        Photo photo = database.getPhoto(file);
        if (null == photo)
        {
            photo = database.indexPhoto(file, ConfigurationProperty.indexingConfigurationList.get());
//            photo = database.indexPhoto(file, database.getIndexingConfigurations());
        }
        return photo;
    }

    @Override
    public String toString()
    {
        return file.getName();
    }

}
