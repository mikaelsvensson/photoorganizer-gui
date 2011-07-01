package info.photoorganizer.gui.components.thumblist;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import sun.jdbc.odbc.OdbcDef;

class MouseSelection implements MouseListener, MouseMotionListener
{
    private Rectangle _area = null;
    private Point _dragStart = null;
    public Rectangle getArea()
    {
        return _area;
    }

    private POThumbList _owner = null;
    
    public MouseSelection(POThumbList owner)
    {
        super();
        _owner = owner;
        _owner.addMouseMotionListener(this);
        _owner.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        Rectangle oldDragArea = _area;
        _dragStart = null;
        _area = null;
        if (null != oldDragArea)
        {
            _owner.repaint(oldDragArea);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (_dragStart == null)
        {
            _dragStart = e.getPoint();
            _area = new Rectangle(_dragStart);
        }
        else
        {
            Rectangle repaintArea = null;
            int x = e.getX();
            int y = e.getY();
            x = x > 0 ? x : 0;
            y = y > 0 ? y : 0;
            
            int left = x < _dragStart.x ? x : _dragStart.x;
            int right = x < _dragStart.x ? _dragStart.x : x;
            int top = y < _dragStart.y ? y : _dragStart.y;
            int bottom = y < _dragStart.y ? _dragStart.y : y;
            
            int width = right - left;
            int height = bottom - top;

            Rectangle newMouseDragArea = new Rectangle(left, top, width, height);
            repaintArea = newMouseDragArea.union(_area);
            _area = newMouseDragArea;
            
            System.out.println("Selection over these items: " + _owner.getItemsInArea(_area));
            
            _owner.repaint(repaintArea);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

}
