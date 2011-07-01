package info.photoorganizer.gui.components.thumblist;

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

    void paint(ListItem item, Graphics2D g);
}
