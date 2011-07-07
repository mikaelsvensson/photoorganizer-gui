package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.KeyModifiers;
import info.photoorganizer.gui.shared.Keys;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;

import sun.net.www.content.image.gif;

import com.sun.jmx.remote.util.OrderClassLoaders;

public class POThumbList extends JPanel implements Scrollable, Iterable<ListItem>
{
    public final static String ACTIONNAME_FOCUS_NEXT = "focusNext";
    public final static String ACTIONNAME_FOCUS_PREVIOUS = "focusPrevious";
    public final static String ACTIONNAME_FOCUS_NEXT_GROUP = "focusNextGroup";
    public final static String ACTIONNAME_FOCUS_PREVIOUS_GROUP = "focusPreviousGroup";
    public final static String ACTIONNAME_FOCUS_UP = "focusUp";
    public final static String ACTIONNAME_FOCUS_DOWN= "focusDown";

    private static class DemoListItem implements ListItem
    {
        File _f = null;

        public DemoListItem(File f)
        {
            super();
            _f = f;
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
            DemoListItem other = (DemoListItem) obj;
            if (_f == null)
            {
                if (other._f != null)
                    return false;
            }
            else if (!_f.equals(other._f))
                return false;
            return true;
        }

        @Override
        public File getFile()
        {
            return _f;
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
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_f == null) ? 0 : _f.hashCode());
            return result;
        }

        @Override
        public String toString()
        {
            return _f.getName();
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
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static List<ListItem> getFolderList(String path)
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
    
    boolean _firstPaint = true;
    
    private ImageGrouper _grouper = new FileNameGrouper();
    
    private Map<ListItemGroupGuiItem, List<ListItemGuiItem>> _guiItemMap = new HashMap<ListItemGroupGuiItem, List<ListItemGuiItem>>();
    private List<GuiItem> _guiItemList = new ArrayList<GuiItem>();
    
    private GuiItem _focusedGuiItem = null;
    
    private ArrayList<ListItem> _items = null;
    
    private Rectangle _mouseSelectionArea = null;
    
    private Point _mouseSelectionDragStart = null;
    
    private ListItemPainter _painter = new SquarePainter();
    
    int _width = -1;
    
    private int _zoom = 20;
    
    public POThumbList()
    {
        setPreferredSize(new Dimension(200, 50));
        setAutoscrolls(true);
        setFocusable(true);
        
        getActionMap().put(ACTIONNAME_FOCUS_NEXT, new MoveFocusAction(this, GuiItemRelationship.NEXT));
        getActionMap().put(ACTIONNAME_FOCUS_PREVIOUS, new MoveFocusAction(this, GuiItemRelationship.PREVIOUS));
        getActionMap().put(ACTIONNAME_FOCUS_NEXT_GROUP, new MoveFocusAction(this, GuiItemRelationship.NEXT_GROUP));
        getActionMap().put(ACTIONNAME_FOCUS_PREVIOUS_GROUP, new MoveFocusAction(this, GuiItemRelationship.PREVIOUS_GROUP));
        getActionMap().put(ACTIONNAME_FOCUS_UP, new MoveFocusAction(this, GuiItemRelationship.UP));
        getActionMap().put(ACTIONNAME_FOCUS_DOWN, new MoveFocusAction(this, GuiItemRelationship.DOWN));
        
        getInputMap().put(KeyStroke.getKeyStroke(Keys.DOWN.getKeyCode(), KeyModifiers.NONE.getValue()), ACTIONNAME_FOCUS_DOWN);
        getInputMap().put(KeyStroke.getKeyStroke(Keys.UP.getKeyCode(), KeyModifiers.NONE.getValue()), ACTIONNAME_FOCUS_UP);
        getInputMap().put(KeyStroke.getKeyStroke(Keys.LEFT.getKeyCode(), KeyModifiers.NONE.getValue()), ACTIONNAME_FOCUS_PREVIOUS);
        getInputMap().put(KeyStroke.getKeyStroke(Keys.RIGHT.getKeyCode(), KeyModifiers.NONE.getValue()), ACTIONNAME_FOCUS_NEXT);
        getInputMap().put(KeyStroke.getKeyStroke(Keys.LEFT.getKeyCode(), KeyModifiers.CTRL.getValue()), ACTIONNAME_FOCUS_PREVIOUS_GROUP);
        getInputMap().put(KeyStroke.getKeyStroke(Keys.RIGHT.getKeyCode(), KeyModifiers.CTRL.getValue()), ACTIONNAME_FOCUS_NEXT_GROUP);
        
        addFocusListener(new FocusListener()
        {
            
            @Override
            public void focusLost(FocusEvent e)
            {
                repaint();
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
                repaint();
            }
        });
        
        addMouseWheelListener(new MouseWheelListener()
        {
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if (e.isControlDown())
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
                    relayout();
                }
            }
        });
        addMouseMotionListener(new MouseMotionListener()
        {
            
            @Override
            public void mouseDragged(MouseEvent e)
            {
                scrollRectToVisible(new Rectangle(e.getX(), e.getY(), 1, 1));
                
                
                if (_mouseSelectionDragStart == null)
                {
                    _mouseSelectionDragStart = e.getPoint();
                    _mouseSelectionArea = new Rectangle(_mouseSelectionDragStart);
                }
                else
                {
                    Rectangle repaintArea = null;
                    int x = e.getX();
                    int y = e.getY();
                    x = x > 0 ? x : 0;
                    y = y > 0 ? y : 0;
                    
                    int left = x < _mouseSelectionDragStart.x ? x : _mouseSelectionDragStart.x;
                    int right = x < _mouseSelectionDragStart.x ? _mouseSelectionDragStart.x : x;
                    int top = y < _mouseSelectionDragStart.y ? y : _mouseSelectionDragStart.y;
                    int bottom = y < _mouseSelectionDragStart.y ? _mouseSelectionDragStart.y : y;
                    
                    int width = right - left;
                    int height = bottom - top;

                    Rectangle newMouseDragArea = new Rectangle(left, top, width, height);
                    repaintArea = newMouseDragArea.union(_mouseSelectionArea);
                    _mouseSelectionArea = newMouseDragArea;
                    
                    repaint(repaintArea);
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e)
            {
            }
        });
        addMouseListener(new MouseListener()
        {
            
            private GuiItem _shiftSelectionOldItem = null;
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                requestFocusInWindow();
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
                Rectangle repaintArea = new Rectangle();
                
                boolean extendSelection = e.isControlDown();
                if (!extendSelection)
                {
                    deselectItems(getSelectedItems());
                }
                if (null != _mouseSelectionArea)
                {
                    repaintArea.add(_mouseSelectionArea);
                    
                    for (GuiItem item : getGuiItemsInArea(_mouseSelectionArea))
                    {
                        if (item instanceof ListItemGuiItem)
                        {
                            ((ListItemGuiItem)item).isSelected = true;
                        }
                        repaintArea.add(item.area);
                    }
                }
                else
                {
                    GuiItem item = getGuiItemAtPoint(e.getPoint());
                    if (item != null)
                    {
                        if (item instanceof ListItemGuiItem)
                        {
                            onListItemClick((ListItemGuiItem) item, repaintArea, e);
                        }
                        else if (item instanceof ListItemGroupGuiItem)
                        {
                            onListItemGroupClick((ListItemGroupGuiItem) item, repaintArea, e);
                        }

                        if (null != _focusedGuiItem)
                        {
                            repaintArea.add(_focusedGuiItem.area);
                        }
                        setFocusedGuiItem(item);
                    }
                }
                
                _mouseSelectionDragStart = null;
                _mouseSelectionArea = null;
                
                repaint(repaintArea);
                
            }

            private void onListItemClick(ListItemGuiItem guiItem, Rectangle repaintArea, MouseEvent e)
            {
                List<ListItemGuiItem> affectedItems = new ArrayList<ListItemGuiItem>();
                if (e.isShiftDown())
                {
                    if (null == _shiftSelectionOldItem)
                    {
                        _shiftSelectionOldItem = _focusedGuiItem;
                    }
                    int newPos = _guiItemList.indexOf(guiItem);
                    int oldPos = _shiftSelectionOldItem != null ? _guiItemList.indexOf(_shiftSelectionOldItem) : 0;
                    int startPos = Math.min(newPos, oldPos);
                    int endPos = Math.max(newPos, oldPos);
                    for (int i = startPos; i <= endPos; i++)
                    {
                        GuiItem gI = _guiItemList.get(i);
                        if (gI instanceof ListItemGuiItem)
                        {
                            ((ListItemGuiItem)gI).isSelected = true;
                            repaintArea.add(gI.area);
                        }
                    }
                }
                else
                {
                    guiItem.isSelected = !guiItem.isSelected;
                    repaintArea.add(guiItem.area);
                    
                    _shiftSelectionOldItem = null;
                }
            }
            
            private void onListItemGroupClick(ListItemGroupGuiItem guiItem, Rectangle repaintArea, MouseEvent e)
            {
                guiItem.isExpanded = !guiItem.isExpanded;
                repaintArea.add(guiItem.area);
                relayout();
            }
        });
    }
    
    public List<ListItem> getSelectedItems()
    {
        List<ListItem> res = new ArrayList<ListItem>();
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem instanceof ListItemGuiItem)
            {
                ListItemGuiItem listItemGuiItem = (ListItemGuiItem)guiItem;
                if (listItemGuiItem.isSelected)
                {
                    res.add(listItemGuiItem.item);
                }
            }
        }
        return res;
    }
    
    public void deselectItems(List<ListItem> items)
    {
        setItemsSelectionStatus(items, false);
    }
    
    ListItemGroupGuiItem getGroupGuiItem(ImageGroup group)
    {
        for (ListItemGroupGuiItem guiItem : _guiItemMap.keySet())
        {
            if (guiItem.getGroup() == group)
            {
                return guiItem;
            }
        }
        return null;
    }
    
    GuiItem getGuiItemAtPoint(Point coord)
    {
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem.isVisible && guiItem.contains(coord))
            {
                return guiItem;
            }
        }
        return null;
    }

    List<GuiItem> getGuiItemsInArea(Rectangle area)
    {
        List<GuiItem> res = new ArrayList<GuiItem>();
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem.isVisible && guiItem.intersects(area))
            {
                res.add(guiItem);
            }
        }
        return res;
    }
    
    public List<ListItem> getItems()
    {
        return Collections.unmodifiableList(_items);
    }

    ListItem getListItemAtPoint(Point coord)
    {
        GuiItem guiItem = getGuiItemAtPoint(coord);
        if (guiItem instanceof ListItemGuiItem)
        {
            return ((ListItemGuiItem)guiItem).item;
        }
        return null;
    }

    ListItemGuiItem getListItemGuiItem(ListItem item)
    {
        for (Entry<ListItemGroupGuiItem, List<ListItemGuiItem>> entry : _guiItemMap.entrySet())
        {
            List<ListItemGuiItem> guiItems = entry.getValue();
            ListItemGuiItem listItem = getListItemGuiItem(item, guiItems);
            if (null != listItem)
            {
                return listItem;
            }
        }
        return null;
    }

    private ListItemGuiItem getListItemGuiItem(ListItem item, List<ListItemGuiItem> guiItems)
    {
        for (ListItemGuiItem guiItem : guiItems)
        {
            if (guiItem.item == item)
            {
                return guiItem;
            }
        }
        return null;
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
    
    private boolean isItemSelected(ListItem item)
    {
        ListItemGuiItem listItemGuiItem = getListItemGuiItem(item);
        if (listItemGuiItem != null)
        {
            return listItemGuiItem.isSelected;
        }
        return false;
    }
    
    @Override
    public Iterator<ListItem> iterator()
    {
        return _items.iterator();
    }

    private void paintComponent(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle originalClip = g.getClipBounds();
        
        paintGuiItems(g);
        
        paintSelectionRectangle(g);
        
        if (isFocusOwner())
        {
            paintFocusIndicator(g);
        }
        
        g.setClip(originalClip);
        
        revalidate();
    }

    private void paintFocusIndicator(Graphics2D g)
    {
        if (_focusedGuiItem != null)
        {
            BasicStroke stroke = new BasicStroke(0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[] {2,2}, 0);
            Rectangle focusRect = new Rectangle(_focusedGuiItem.area.x, _focusedGuiItem.area.y, _focusedGuiItem.area.width - 1, _focusedGuiItem.area.height - 1);
            System.err.println("paintFocusIndicator " + focusRect);
            g.draw(stroke.createStrokedShape(focusRect));
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g); // Paint background
        
        if (_firstPaint)
        {
            regroup(_grouper);
            relayout();
            _firstPaint = false;
        }
        
        int w = getWidth();
        if (_width != w)
        {
            // Component has been resized. Recalculate whatever needs to be recalculated.
            relayout();
            _width = w;
        }
        
        paintComponent((Graphics2D) g.create());
    }

    private void paintGuiItems(Graphics2D g)
    {
        System.err.println("Paint " + g.getClipBounds());
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem.isVisible)
            {
                guiItem.paint(g);
            }
        }
//        for (Entry<ListItemGroupGuiItem, List<ListItemGuiItem>> entry : _guiItemMap.entrySet())
//        {
//            entry.getKey().paint(g);
//            for (ListItemGuiItem item : entry.getValue())
//            {
//                item.paint(g);
//            }
//        }
    }

    private void paintSelectionRectangle(Graphics2D g)
    {
        Rectangle mouseDragArea = _mouseSelectionArea;
        if (mouseDragArea != null)
        {
            g.setColor(new Color(200, 200, 200, 150));
            g.fill(mouseDragArea);
            g.setColor(Color.BLACK);
            g.drawRect(mouseDragArea.x, mouseDragArea.y, mouseDragArea.width-1, mouseDragArea.height-1);
            System.err.println("Mouse selection: " + mouseDragArea);
        }
    }

    private void regroup(ImageGrouper grouper)
    {
        Map<ListItemGroupGuiItem, List<ListItemGuiItem>> newGuiItemMap = new LinkedHashMap<ListItemGroupGuiItem, List<ListItemGuiItem>>();
        List<GuiItem> newGuiItemList = new ArrayList<GuiItem>();
        
        for (ImageGroup grp : grouper.group(_items))
        {
            
            ListItemGroupGuiItem groupGuiItem = getGroupGuiItem(grp);
            if (groupGuiItem == null)
            {
                groupGuiItem = new ListItemGroupGuiItem(grp, new Rectangle());
            }
            
            ArrayList<ListItemGuiItem> itemList = new ArrayList<ListItemGuiItem>();
            newGuiItemMap.put(groupGuiItem, itemList);
            newGuiItemList.add(groupGuiItem);
            
            for (ListItem item : grp.getItems())
            {
                ListItemGuiItem imageGuiItem = getListItemGuiItem(item);
                if (imageGuiItem == null)
                {
                    imageGuiItem = new ListItemGuiItem(item, _painter, new Rectangle());
                }
                itemList.add(imageGuiItem);
                newGuiItemList.add(imageGuiItem);
            }
        }
        
        
        _guiItemMap = newGuiItemMap;
        
        relayout();

        Collections.sort(newGuiItemList, ITEM_SORTER);
        _guiItemList = newGuiItemList;
        
        repaint();
    }

    private void relayout()
    {
        Dimension componentSize = getSize();
        Dimension visibleSize = getVisibleRect().getSize();
        Insets insets = getInsets();
        
        int visibleWidth = visibleSize.width;
        int visibleHeight = visibleSize.height;
        int componentWidth = componentSize.width;
        int componentHeight = componentSize.height;
        
        long itemArea = (visibleHeight * visibleWidth * _zoom * _zoom) / (10000); //(long) (visibleHeight * visibleWidth * zoomPercent);
        
        if (itemArea == 0)
        {
            return;
        }
        double itemAspectRatio = _painter.getWidthToHeightRatio(itemArea);


        int accumulatedY = 0;

        double itemWidth = Math.sqrt(itemAspectRatio * itemArea);
        double itemHeight = Math.sqrt(itemArea * itemAspectRatio);
        int itemsWide = (int) (visibleWidth / itemWidth);
        itemWidth = visibleWidth / itemsWide;
        itemHeight = itemWidth / itemAspectRatio;
//        System.err.println("itemsWide=" + itemsWide + " itemWidth=" + itemWidth);

        for (Entry<ListItemGroupGuiItem, List<ListItemGuiItem>> entry : _guiItemMap
                .entrySet())
        {

            ListItemGroupGuiItem groupGuiItem = entry.getKey();
            groupGuiItem.area.x = 0;
            groupGuiItem.area.y = accumulatedY;
            groupGuiItem.area.width = visibleWidth - 1;
            groupGuiItem.area.height = ListItemGroupGuiItem.HEIGHT;

            accumulatedY += ListItemGroupGuiItem.HEIGHT;
            double x = 0;
            int accumulatedX = 0;
            int i = 0;
            int widthLeft = visibleWidth;

            if (entry.getValue().size() > 0)
            {
                for (ListItemGuiItem listItemGuiItem : entry.getValue())
                {
    
                    listItemGuiItem.isVisible = groupGuiItem.isExpanded;
                    if (groupGuiItem.isExpanded)
                    {
                        ListItem item = listItemGuiItem.item;
                        if (i == itemsWide)
                        {
                            i = 0;
                            x = 0;
                            accumulatedX = 0;
                            accumulatedY += itemHeight;
                        }
                        int left = (int) (i * itemWidth);
                        int right = (int) ((i + 1) * itemWidth);
                        int itemW = right - left;
                        x += itemHeight * itemAspectRatio;
    
                        GuiItem imageGuiItem = getListItemGuiItem(item);
                        imageGuiItem.area.x = accumulatedX;
                        imageGuiItem.area.y = accumulatedY;
                        imageGuiItem.area.width = itemW - 1;
                        imageGuiItem.area.height = (int) itemHeight - 1;
    
                        accumulatedX += itemW;
                        i++;
                    }
                }
                if (groupGuiItem.isExpanded)
                {
                    accumulatedY += itemHeight;
                }
            }
        }
        
        
        setPreferredSize(new Dimension(componentWidth, accumulatedY));
        System.out.println("Preferred size: " + getPreferredSize());
        
        repaint();
    }
    
    public GuiItem getRelatedGuiItem(GuiItem base, GuiItemRelationship relationship)
    {
        int basePos = _guiItemList.indexOf(base);
        if (basePos >= 0)
        {
            switch (relationship)
            {
            case DOWN:
            {
                GuiItem candidate = null;
                for (int i = basePos + 1; i < _guiItemList.size(); i++)
                {
                    GuiItem guiItem = _guiItemList.get(i);
                    if (guiItem.isVisible && guiItem.area.y > base.area.y)
                    {
                        if (null == candidate)
                        {
                            candidate = guiItem;
                        }
                        if (candidate.area.y < guiItem.area.y)
                        {
                            return candidate;
                        }
                        else if (guiItem.area.x >= base.area.x)
                        {
                            return guiItem;
                        }
                    }
                }
                return candidate;
            }
            case UP:
            {
                for (int i = basePos - 1; i >= 0; i--)
                {
                    GuiItem guiItem = _guiItemList.get(i);
                    if (guiItem.isVisible && guiItem.area.y < base.area.y && guiItem.area.x <= base.area.x)
                    {
                        return guiItem;
                    }
                }
                break;
            }
            case LEFT:
            case RIGHT:
                break;
            case NEXT:
            case PREVIOUS:
            {
                int step = relationship == GuiItemRelationship.NEXT ? 1 : -1;
                for (int i = basePos + step; i < _guiItemList.size() && i >= 0; i += step)
                {
                    GuiItem guiItem = _guiItemList.get(i);
                    if (guiItem.isVisible)
                    {
                        return guiItem;
                    }
                }
//                if (basePos > 0)
//                {
//                    return _guiItemList.get(basePos - 1);
//                }
                break;
            }
            case NEXT_GROUP:
            case PREVIOUS_GROUP:
            {
                int step = relationship == GuiItemRelationship.NEXT_GROUP ? 1 : -1;
                for (int i = basePos + step; i < _guiItemList.size() && i >= 0; i += step)
                {
                    GuiItem guiItem = _guiItemList.get(i);
                    if (guiItem.isVisible && guiItem instanceof ListItemGroupGuiItem)
                    {
                        return guiItem;
                    }
                }
            }
            }
        }
        return null;
    }
    
    public void setFocusedGuiItem(GuiItem focusedGuiItem)
    {
        System.err.println("setFocusedGuiItem(" + focusedGuiItem + ")");
        Rectangle repaintArea = new Rectangle(focusedGuiItem.area);
        if (null != _focusedGuiItem)
        {
            repaintArea.add(_focusedGuiItem.area);
        }
        
        _focusedGuiItem = focusedGuiItem;
        
        repaint(/*repaintArea*/);
        scrollRectToVisible(_focusedGuiItem.area);
//        scrollRectToVisible(repaintArea);
    }

    GuiItem getFocusedGuiItem()
    {
        return _focusedGuiItem;
    }

    public void selectItems(List<ListItem> items)
    {
        setItemsSelectionStatus(items, true);
    }

    public void setItems(Iterable<ListItem> items)
    {
        _items = new ArrayList<ListItem>();
        
        Iterator<ListItem> i = items.iterator();
        while (i.hasNext())
        {
            ListItem item = i.next();
            _items.add(item);
        }
        
        regroup(_grouper);
    }
    
    private void setItemsSelectionStatus(List<ListItem> items, boolean selected)
    {
        Rectangle repaintArea = new Rectangle();
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem instanceof ListItemGuiItem)
            {
                ListItemGuiItem listItemGuiItem = (ListItemGuiItem)guiItem;
                if (items.contains(listItemGuiItem.item))
                {
                    listItemGuiItem.isSelected = selected;
                    repaintArea.add(listItemGuiItem.area);
                }
            }
        }
        repaint(repaintArea);
    }
    
}
