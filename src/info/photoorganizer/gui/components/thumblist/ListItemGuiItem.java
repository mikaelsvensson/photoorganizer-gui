package info.photoorganizer.gui.components.thumblist;

import java.awt.Graphics2D;
import java.awt.Rectangle;


class ListItemGuiItem extends GuiItem
{
    private boolean _checked = false;
    
    private ListItemPainter _painter = null;
    
    private boolean _selected = false;

    private ListItem item = null;

    public ListItemGuiItem(ListItem item, ListItemPainter painter, Rectangle area, POThumbList owner)
    {
        super(area, owner);
        this.item = item;
        _painter = painter;
        this.area = area;
    }

    public ListItemGuiItem(ListItemPainter painter, Rectangle area, POThumbList owner)
    {
        super(area, owner);
        _painter = painter;
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
    public synchronized ListItem getItem()
    {
        return item;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        return result;
    }
    
    public synchronized boolean isChecked()
    {
        return _checked;
    }

    public synchronized boolean isSelected()
    {
        return _selected;
    }

    @Override
    public void paintImpl(Graphics2D g)
    {
        _painter.paint(item, _selected, area.getSize(), g);
    }

    public synchronized void setChecked(boolean checked)
    {
        _checked = checked;
    }

    public synchronized boolean setSelected(boolean selected)
    {
        boolean diff = _selected != selected;
        
        _selected = selected;
        
        if (diff)
        {
            owner.selectionHasChanged();
        }
        return diff;
    }

}
