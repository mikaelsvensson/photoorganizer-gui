package info.photoorganizer.gui.components.thumblist;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public abstract class GuiItem
{
    protected GuiItem(Rectangle area)
    {
        super();
        this.area = area;
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
        //System.err.println("Painting " + this.getClass().getSimpleName() + " with clip area " + area.toString());
        
        Shape oldClip = g.getClip();
        
        g.setClip(area);
        g.translate(area.x, area.y);

        paintImpl(g);
        
        g.translate(-area.x, -area.y);
        g.setClip(oldClip);
    }

}
