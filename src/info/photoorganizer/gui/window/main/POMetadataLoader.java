package info.photoorganizer.gui.window.main;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.components.thumblist.MetadataLoader;
import info.photoorganizer.metadata.DefaultTagDefinition;
import info.photoorganizer.metadata.Photo;
import info.photoorganizer.metadata.Tag;
import info.photoorganizer.metadata.TagDefinition;
import info.photoorganizer.metadata.ValueTag;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class POMetadataLoader implements MetadataLoader
{
    /**
     * 
     */
    private final Database _database;
    private TagDefinition fNumber;
    private TagDefinition dateTaken;

    /**
     * @param database
     */
    POMetadataLoader(Database database)
    {
        _database = database;
        fNumber = _database.getTagDefinition(DefaultTagDefinition.F_NUMBER.getId());
        dateTaken = _database.getTagDefinition(DefaultTagDefinition.DATE_TAKEN.getId());
        //TagDefinition orientation = _database.getTagDefinition(DefaultTagDefinition.ORIENTATION.getId());
    }

    @Override
    public Map<Object, Object> getMetadata(File file)
    {
        Map<Object, Object> res = new HashMap<Object, Object>();
        Photo photo = _database.getPhoto(file);
        if (null != photo)
        {
            Iterator<Tag<? extends TagDefinition>> tags = photo.getTags();
            while (tags.hasNext())
            {
                Tag<? extends TagDefinition> tag = tags.next();
                if (tag instanceof ValueTag)
                {
                    if (tag.getDefinition().equals(fNumber) || tag.getDefinition().equals(dateTaken)/* || tag.getDefinition().equals(orientation)*/)
                    {
                        res.put(tag.getDefinition(), ((ValueTag)tag).getValue());
                    }
                }
            }
        }
        return res;
    }
}