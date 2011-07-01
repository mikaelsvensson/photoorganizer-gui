package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import com.sun.jmx.remote.util.OrderClassLoaders;

public class POThumbList extends JPanel implements Scrollable, Iterable<ListItem>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private MouseSelection _mouseSelection = new MouseSelection(this);
    private Grid _grid = new FullWidthGrid();
    private ListItemPainter _painter = new SquarePainter();
    private int _zoom = 20;
    //private HashMap<ListItem, ListItemGuiItem> _itemProperties = null;
    private ArrayList<ListItem> _itemCache = null;
    private ImageGrouper _grouper = new FileNameGrouper();
    private static final Comparator<GuiItem> ITEM_SORTER = new Comparator<GuiItem>()
    {
        @Override
        public int compare(GuiItem item1, GuiItem item2)
        {
            int yDiff = item1.area.y - item2.area.y;
            if (yDiff == 0)
            {
                return item1.area.x - item2.area.x;
            }
            else
            {
                return yDiff;
            }
        }
    };
    
    private SortedSet<GuiItem> _guiItems = new TreeSet<GuiItem>(ITEM_SORTER);
    
    public Grid getGrid()
    {
        return _grid;
    }
    
    public List<ListItem> getItems()
    {
        return Collections.unmodifiableList(_itemCache);
    }
    
    public void setItems(Iterable<ListItem> items)
    {
//        _guiItems = new TreeSet<GuiItem>(ITEM_SORTER);
        _itemCache = new ArrayList<ListItem>();
//        _itemProperties = new HashMap<ListItem, ListItemGuiItem>();
        
        Iterator<ListItem> i = items.iterator();
        while (i.hasNext())
        {
            ListItem item = i.next();
//            ListItemGuiItem guiItem = new ListItemGuiItem(_painter);
//            guiItem.item = item;
            
            _itemCache.add(item);
//            _guiItems.add(guiItem);
        }
    }
    
    public void setGrid(Grid grid)
    {
        _grid = grid;
    }

    public POThumbList()
    {
        setPreferredSize(new Dimension(200, 50));
        setAutoscrolls(true);
        addMouseWheelListener(new MouseWheelListener()
        {
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                _zoom += e.getWheelRotation();
                if (_zoom < 1)
                {
                    _zoom = 1;
                }
                else if (_zoom > 100)
                {
                    _zoom = 100;
                }
                System.out.println("Zoom level: " + _zoom);
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionListener()
        {
            
            @Override
            public void mouseMoved(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mouseDragged(MouseEvent e)
            {
                scrollRectToVisible(new Rectangle(e.getX(), e.getY(), 1, 1));
            }
        });
        addMouseListener(new MouseListener()
        {
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mousePressed(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mouseEntered(MouseEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // TODO Auto-generated method stub
            }
        });
    }
    
    int _width = -1;

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g); // Paint background
        
        int w = getWidth();
        int h = getHeight();
        
        if (_width != w)
        {
            // Component has been resized. Recalculate whatever needs to be recalculated.
            onViewportSizeChange();
            _width = w;
        }
        
        Rectangle clipBounds = g.getClipBounds();
        
        Graphics2D g2d = (Graphics2D) g.create();

        paint(w, h, getVisibleRect().width, getVisibleRect().height, g2d);
    }

    private void paint(int componentWidth, int componentHeight, int visibleWidth, int visibleHeight, Graphics2D g)
    {
        //double zoomPercent = _zoom/100f;
        long itemArea = (visibleHeight * visibleWidth * _zoom * _zoom) / (10000); //(long) (visibleHeight * visibleWidth * zoomPercent);
        double itemAspectRatio = _painter.getWidthToHeightRatio(itemArea);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int accumulatedY = 0;

        double itemWidth = Math.sqrt(itemAspectRatio * itemArea);
        double itemHeight = Math.sqrt(itemArea * itemAspectRatio);
//        int itemsWide = ((_zoom-1) / 10)+1;
        int itemsWide = (int) (visibleWidth / itemWidth);
        itemWidth = visibleWidth / itemsWide;
        itemHeight = itemWidth / itemAspectRatio;
        //int availableWidth = visibleWidth;
        System.err.println("itemsWide=" + itemsWide + " itemWidth=" + itemWidth);
        
        Rectangle originalClip = g.getClipBounds();
        
        _guiItems = new TreeSet<GuiItem>(ITEM_SORTER);
        
        for (ImageGroup grp : _grouper.group(_itemCache))
        {
            _guiItems.add(new ListItemGroupGuiItem(grp, new Rectangle(0, accumulatedY, visibleWidth - 1, ListItemGroupGuiItem.HEIGHT)));
            accumulatedY += ListItemGroupGuiItem.HEIGHT;
            double x = 0;
            int accumulatedX = 0;
            int i = 0;
            int widthLeft = visibleWidth;
            for (ListItem item : grp.getItems())
            {
                if (i == itemsWide)
                {
                    i = 0;
                    x = 0;
                    accumulatedX = 0;
                    accumulatedY += itemHeight;
                }
                int left = (int) (i * itemWidth);
                int right = (int) ((i+1) * itemWidth);
                int itemW = right - left;
                x += itemHeight * itemAspectRatio;
                
                _guiItems.add(new ListItemGuiItem(item, _painter, new Rectangle(accumulatedX, accumulatedY, itemW - 1, (int)itemHeight - 1)));
                
                accumulatedX += itemW;
                i++;
            }
            accumulatedY += itemHeight;
        }
        
        
        g.setClip(originalClip);
        
        setPreferredSize(new Dimension(componentWidth, accumulatedY));
        System.out.println("Preferred size: " + getPreferredSize());
        for(GuiItem guiItem : _guiItems)
        {
            guiItem.paint(g);
        }
        drawSelectionRectangle(g);
        revalidate();
    }

    private void onViewportSizeChange()
    {
        int w = getWidth();
        int h = getHeight();
        _grid.updateCellSize(w, h);
    }
    
    private void drawSelectionRectangle(Graphics2D g)
    {
        Rectangle mouseDragArea = _mouseSelection.getArea();
        if (mouseDragArea != null)
        {
            g.setColor(new Color(200, 200, 200, 150));
            g.fill(mouseDragArea);
            g.setColor(Color.BLACK);
            g.drawRect(mouseDragArea.x, mouseDragArea.y, mouseDragArea.width-1, mouseDragArea.height-1);
            System.err.println("Mouse selection: " + mouseDragArea);
        }
    }

    private static class POThumbListTestDialog extends PODialog
    {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        protected POThumbListTestDialog(Container root)
        {
            super("TITLE", CloseOperation.DISPOSE_ON_CLOSE, root);
        }
        
    }
    
    private static class DemoListItem implements ListItem
    {
        File _f = null;

        public DemoListItem(File f)
        {
            super();
            _f = f;
        }

        @Override
        public Image getImage()
        {
            return null;
        }

        @Override
        public Map<Object, Object> getMetadata()
        {
            return null;
        }

        @Override
        public File getFile()
        {
            return _f;
        }

        @Override
        public String toString()
        {
            return _f.getName();
        }
        
    }
    
    public List<ListItem> getFolderList(String path)
    {
        List<ListItem> items = new ArrayList<ListItem>();
        for (File f : (new File(path)).listFiles())
        {
            items.add(new DemoListItem(f));
        }
        return items;
    }
    
    public static void main(String[] args)
    {
        POThumbList thumbList = new POThumbList();
        
        List<ListItem> items = thumbList.getFolderList("F:\\Fotografier\\Personer\\Sonja");
        thumbList.setItems(items);
        
        JPanel p = new JPanel();
        JScrollPane scrollPane = new JScrollPane(thumbList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        p.add(scrollPane);
        PODialog.show(new POThumbListTestDialog(scrollPane));
    }

    @Override
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return 100;
    }

    @Override
    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportWidth()
    {
        return true;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return 10;
    }

    @Override
    public Iterator<ListItem> iterator()
    {
        return _itemCache.iterator();
    }
    
    List<ListItem> getItemsInArea(Rectangle area)
    {
        List<ListItem> res = new ArrayList<ListItem>();
        for (GuiItem guiItem : _guiItems)
        {
            if (guiItem instanceof ListItemGuiItem)
            {
                if (guiItem.isWithin(area))
                {
                    res.add(((ListItemGuiItem) guiItem).item);
                }
            }
        }
        return res;
    }

}
