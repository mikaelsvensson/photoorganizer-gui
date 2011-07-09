package info.photoorganizer.gui.components.thumblist;

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface ListItemPainter
{
    /**
     * The number to multiple the height with to get the width, e.g. 3/2 or 4/3.
     * 
     * @param area
     *            The number of pixels that the list item may occupy on screen.
     * @return
     */
    double getWidthToHeightRatio(long area);

    /**
     * @param item
     *            the list item to paint.
     * @param isSelected
     * @param itemSize
     *            the area (width and height) that the list item is supposed to
     *            occupy on screen.
     * @param g
     *            the graphics object to use for painting. The clipping area
     *            will be smaller than {@code itemSize} if only a part of the
     *            list item is visible.
     */
    void paint(ListItem item, boolean isSelected, Dimension itemSize, Graphics2D g);
}
