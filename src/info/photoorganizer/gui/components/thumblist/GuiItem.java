package info.photoorganizer.gui.components.thumblist;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public abstract class GuiItem
{
    protected POThumbList owner = null;
    protected GuiItem(Rectangle area, POThumbList owner)
    {
        super();
        this.area = area;
        this.owner = owner;
    }

    /**
     * The "screen area" that the list item "occupies". Used for "collision detection". 
     */
    public Rectangle area = null;
    
    public boolean isVisible = true;
    
    public boolean intersects(Rectangle r)
    {
        return area.intersects(r);
    }
    
    public boolean contains(Point p)
    {
        return area.contains(p);
    }
    
    public abstract void paintImpl(Graphics2D g);
    
    public void paint(Graphics2D g)
    {
        Shape oldClip = g.getClip();

        /*
         * Set clip area so that gui item paint can only paint in the screen
         * area which is the intersection of the total area to repaint and the
         * area which the list item occupies (this is important when only a part
         * of the list item is supposed to be visible due to viewport scrolling
         * -- the gui item painter must be prevented from painting the part of
         * the gui item which should be hidden).
         */
        g.setClip(g.getClipBounds().intersection(area));
        g.translate(area.x, area.y);
        
        paintImpl(g);
        
        g.translate(-area.x, -area.y);
        g.setClip(oldClip);
    }

}
