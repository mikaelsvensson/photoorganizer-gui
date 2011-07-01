package info.photoorganizer.gui.components.thumblist;

import java.awt.Image;

public interface ImageLoaderResponse
{
    Image getImage();
    boolean isDone();
    boolean abort();
    void join();
    boolean delay();
}
