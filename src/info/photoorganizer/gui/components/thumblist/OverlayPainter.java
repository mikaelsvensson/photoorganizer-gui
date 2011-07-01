package info.photoorganizer.gui.components.thumblist;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface OverlayPainter
{
    void paint(Graphics g, ListItem item, Rectangle imageArea, Rectangle filenameArea, Rectangle metadataArea);
}
