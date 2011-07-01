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
        _painter.paint(item, g);
    }

    public ListItemGuiItem(ListItem item, ListItemPainter painter, Rectangle area)
    {
        super(area);
        this.item = item;
        _painter = painter;
        this.area = area;
    }

}
