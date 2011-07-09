package info.photoorganizer.gui.components.thumblist;

import java.awt.Graphics2D;
import java.awt.Rectangle;


class ListItemGuiItem extends GuiItem
{
    public boolean isSelected = false;
    public boolean isChecked = false;
    public ListItem item = null;
    
    private ListItemPainter _painter = null;
    
    public ListItemGuiItem(ListItemPainter painter, Rectangle area)
    {
        super(area);
        _painter = painter;
    }

    @Override
    public void paintImpl(Graphics2D g)
    {
        _painter.paint(item, isSelected, area.getSize(), g);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
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
        ListItemGuiItem other = (ListItemGuiItem) obj;
        if (item == null)
        {
            if (other.item != null)
                return false;
        }
        else if (!item.equals(other.item))
            return false;
        return true;
    }

    public ListItemGuiItem(ListItem item, ListItemPainter painter, Rectangle area)
    {
        super(area);
        this.item = item;
        _painter = painter;
        this.area = area;
    }

}
