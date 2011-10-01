package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.metadata.DefaultTagDefinition;
import info.photoorganizer.metadata.IntegerNumberTag;
import info.photoorganizer.metadata.IntegerNumberTagDefinition;
import info.photoorganizer.metadata.Orientation;
import info.photoorganizer.metadata.TagDefinition;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.omg.CORBA._PolicyStub;

public class DefaultListItem implements ListItem
{
    private static final Logger L = info.photoorganizer.util.Log.getLogger(POThumbList.class);
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_file == null) ? 0 : _file.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultListItem other = (DefaultListItem) obj;
        if (_file == null)
        {
            if (other._file != null)
                return false;
        }
        else if (!_file.equals(other._file))
            return false;
        return true;
    }

    private POThumbList _owner = null;
    private File _file = null;
    private Map<Object, Object> _metadata = null;
    private Image _image = null; 
    private Dimension _imagePreferredSize = null;
//    private Orientation _orientation = null;

    public DefaultListItem(File file, POThumbList owner)
    {
        super();
        _file = file;
        _owner = owner;
    }

    public DefaultListItem(File file, Map<Object, Object> metadata, POThumbList owner)
    {
        super();
        _file = file;
        _metadata = metadata;
        _owner = owner;
    }

    @Override
    public File getFile()
    {
        return _file;
    }

    @Override
    public Map<Object, Object> getMetadata()
    {
        if (null == _metadata)
        {
            _owner.addMetadataTask(this, true);
        }
        return _metadata;
    }

    public void setFile(File file)
    {
        _file = file;
    }

    public void setMetadata(Map<Object, Object> metadata)
    {
        _metadata = metadata;
        
//        _orientation = null;
//        for (Entry<Object, Object> entry : metadata.entrySet())
//        {
//            if (entry.getKey() instanceof IntegerNumberTagDefinition && entry.getValue() instanceof Integer)
//            {
//                IntegerNumberTagDefinition def = (IntegerNumberTagDefinition) entry.getKey();
//                Integer value = (Integer) entry.getValue();
//                if (def.getId().equals(DefaultTagDefinition.ORIENTATION.getId()))
//                {
//                    _orientation = Orientation.fromExifValue(value);
//                }
//            }
//            
//        }
    }

    @Override
    public Image getImage(Dimension preferredSize)
    {
        if (null == _image)
        {
            _owner.addImageTask(this, preferredSize, true);
        }
        else if (!_imagePreferredSize.equals(preferredSize))
        {
            L.fine("getImage " + _imagePreferredSize + " != " + preferredSize);
            _owner.addImageTask(this, preferredSize, true);
        }
        return _image;
    }
    
    public void setImage(Image image, Dimension preferredSize)
    {
        _image = image;
        _imagePreferredSize = preferredSize;
        L.fine("setImage " + _imagePreferredSize);
    }

//    @Override
//    public Orientation getOrientation()
//    {
//        return _orientation;
//    }

}
