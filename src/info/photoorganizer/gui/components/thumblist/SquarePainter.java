package info.photoorganizer.gui.components.thumblist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class SquarePainter implements ListItemPainter
{

    @Override
    public double getWidthToHeightRatio(long area)
    {
        return 1;
    }

    @Override
    public void paint(ListItem item, Graphics2D g)
    {
        Rectangle r = g.getClipBounds();
        
        g.setColor(Color.YELLOW);
        g.fill(r);
        
        g.setColor(Color.BLACK);
        g.drawString(item.getFile().getName(), r.x + 10, r.y + r.height - 20);
    }

}
