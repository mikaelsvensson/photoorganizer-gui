package info.photoorganizer.gui.components.thumblist;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.Map;

public class DefaultListItem implements ListItem
{
    private POThumbList _owner = null;
    private File _file = null;
    private Map<Object, Object> _metadata = null;

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
    }

    @Override
    public Image getImage(Dimension preferredSize)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
