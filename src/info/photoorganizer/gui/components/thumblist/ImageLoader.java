package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.metadata.Orientation;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

public interface ImageLoader
{
    /**
     * Returns an image representation of the specified file.
     * 
     * @param file file to create image for
     * @param size dimension of returned image
     * @param imageOrientationInFile information about how image data is stored in the specified file
     * @return
     */
    Image getImage(File file, Dimension size/*, Orientation imageOrientationInFile*/);
}
