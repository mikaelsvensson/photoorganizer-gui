package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.metadata.Orientation;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.Map;

public interface ListItem
{
    /**
     * Should return an image very quickly, or at least not perform any length
     * resize operations or things of that nature.
     * 
     * @param preferredSize
     * @return
     */
    Image getImage(Dimension preferredSize);
    Map<Object, Object> getMetadata();
    File getFile();
//    Orientation getOrientation();
}
