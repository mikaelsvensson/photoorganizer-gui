package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.metadata.Orientation;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

public interface ImageLoader
{
    Image getImage(File file, Dimension preferredSize, Orientation imageOrientationInFile);
}
