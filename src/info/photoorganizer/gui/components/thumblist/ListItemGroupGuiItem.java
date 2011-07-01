package info.photoorganizer.gui.components.thumblist;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ListItemGroupGuiItem extends GuiItem
{
    public static final int HEIGHT = 30;
    
    private ImageGroup _group = null;

    public ListItemGroupGuiItem(ImageGroup group, Rectangle area)
    {
        super(area);
        _group = group;
    }

    @Override
    public void paintImpl(Graphics2D g)
    {
        g.setColor(Color.WHITE);
        g.fill(g.getClipBounds());
        g.setColor(Color.BLACK);
        g.drawString(_group.getHeader(), 0, 20);
    }

}
