package info.photoorganizer.gui.components.thumblist;

import java.awt.Image;
import java.io.File;
import java.util.Map;

public interface ListItem
{
    Image getImage();
    Map<Object, Object> getMetadata();
    File getFile();
}
