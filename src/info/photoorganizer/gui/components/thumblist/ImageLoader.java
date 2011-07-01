package info.photoorganizer.gui.components.thumblist;

import java.awt.Image;
import java.io.File;

public interface ImageLoader
{
    Image getImage(File file, int width, int height);
    ImageLoaderResponse getImageAsync(File file, int width, int height);
}
