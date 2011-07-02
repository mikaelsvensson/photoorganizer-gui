package info.photoorganizer.gui.components.thumblist;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ListItemGroupGuiItem extends GuiItem
{
    public static final int HEIGHT = 30;
    
    private ImageGroup _group = null;

    public ImageGroup getGroup()
    {
        return _group;
    }

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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_group == null) ? 0 : _group.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ListItemGroupGuiItem other = (ListItemGroupGuiItem) obj;
        if (_group == null)
        {
            if (other._group != null)
                return false;
        }
        else if (!_group.equals(other._group))
            return false;
        return true;
    }

}
