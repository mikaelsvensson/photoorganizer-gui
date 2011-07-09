package info.photoorganizer.gui.components.thumblist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class SquarePainter implements ListItemPainter
{
    @Override
    public double getWidthToHeightRatio(long area)
    {
        return 1;
    }

    @Override
    public void paint(ListItem item, boolean isSelected, Dimension itemSize, Graphics2D g)
    {
        Rectangle r = g.getClipBounds();
        
        g.setColor(isSelected ? Color.GRAY : Color.YELLOW);
        g.fill(r);
        
        g.setColor(Color.BLACK);
        FontMetrics fontMetrics = g.getFontMetrics();
        Dimension imageSize = new Dimension(itemSize.width, itemSize.height-fontMetrics.getHeight());
        Image img = item.getImage(imageSize);
        if (img != null)
        {
            int height = img.getHeight(null);
            int width = img.getWidth(null);
            if (height == imageSize.height || width == imageSize.width)
            {
                // Acceptable image size
                g.drawImage(img, 0, 0, null);
            }
        }
        g.drawString(item.getFile().getName(), 0, itemSize.height - fontMetrics.getDescent());
    }

}
