package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.KeyModifiers;
import info.photoorganizer.gui.shared.Keys;
import info.photoorganizer.metadata.Orientation;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;

public class POThumbList extends JPanel implements Scrollable
{
    private static final Logger L = info.photoorganizer.util.Log.getLogger(POThumbList.class);
    
    private static final Integer TASK_PRIORITY_HIGH = 1;
    private static final Integer TASK_PRIORITY_LOW = 0;
    
    private PrioritizedSwingWorker<Void, DefaultListItem, Integer> _metadataWorker = null;
    private MetadataLoader _metadataLoader = null;
    public synchronized ImageLoader getImageLoader()
    {
        return _imageLoader;
    }

    public synchronized void setImageLoader(ImageLoader imageLoader)
    {
        _imageLoader = imageLoader;
    }

    private ExecutorService _executor = Executors.newCachedThreadPool();
    
    private ImageLoader _imageLoader = null;
    private Event<SelectionEventListener, SelectionEvent> _selectionEvent = new Event<SelectionEventListener, SelectionEvent>(
            new EventExecuter<SelectionEventListener, SelectionEvent>()
            {
                @Override
                public void fire(SelectionEventListener listener, SelectionEvent event)
                {
                    listener.selectionChanged(event);
                }
            });
    
    public synchronized MetadataLoader getMetadataLoader()
    {
        return _metadataLoader;
    }

    public void addSelectionListener(SelectionEventListener listener)
    {
        _selectionEvent.addListener(listener);
    }

    public void removeSelectionListener(SelectionEventListener listener)
    {
        _selectionEvent.removeListener(listener);
    }

    public synchronized void setMetadataLoader(MetadataLoader metadataLoader)
    {
        _metadataLoader = metadataLoader;
    }

    private class MetadataLoaderCallable implements Callable<DefaultListItem>
    {

        private DefaultListItem _item = null;
        
        public MetadataLoaderCallable(DefaultListItem item)
        {
            _item = item;
        }
        
        @Override
        public DefaultListItem call()/* throws Exception*/
        {
            if (_item.getMetadata() == null)
            {
                try
                {
                    _item.setMetadata(_metadataLoader.getMetadata(_item.getFile()));
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//                if (_item.getOrientation() != null)
//                {
//                    repaint(_item.getFile());
//                }
            }
            return _item;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj != null && obj instanceof MetadataLoaderCallable)
            {
                return ((MetadataLoaderCallable)obj)._item.getFile().equals(_item.getFile());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return _item.getFile().hashCode();
        }
        
        @Override
        public String toString()
        {
            return "Get metadata for " + _item.getFile().getAbsolutePath();
        }
        
    }
    
    private class ImageLoaderCallable implements Callable<DefaultListItem>
    {
        
        private DefaultListItem _item = null;
        private Dimension _preferredSize = null;
        
        public ImageLoaderCallable(DefaultListItem item, Dimension preferredSize)
        {
            _item = item;
            _preferredSize = preferredSize;
        }
        
        @Override
        public DefaultListItem call()/* throws Exception*/
        {
//            Image image = _item.getImage(_preferredSize);
//            if (image == null)
//            {
//            if (_item.getOrientation() != null)
//            {
                try
                {
                    Image image = _imageLoader.getImage(_item.getFile(), _preferredSize/*, _item.getOrientation()*/);
                    if (null == image)
                    {
                        image = new BufferedImage(_preferredSize.width, _preferredSize.height, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D graphics = (Graphics2D) image.getGraphics();
                        graphics.setColor(Color.RED);
                        graphics.fill(new Rectangle(_preferredSize));
                        graphics.setColor(Color.BLACK);
                        graphics.drawString("No image", 5, 25);
                        graphics.dispose();
                    }
                    _item.setImage(image, _preferredSize);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//            }
//            else
//            {
//                BufferedImage errorImg = new BufferedImage(_preferredSize.width, _preferredSize.height, BufferedImage.TYPE_INT_ARGB);
//                _item.setImage(errorImg, _preferredSize);
//            }
//            }
            return _item;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (obj != null && obj instanceof ImageLoaderCallable)
            {
                return ((ImageLoaderCallable)obj)._item.getFile().equals(_item.getFile());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return _item.getFile().hashCode();
        }

        @Override
        public String toString()
        {
            return "Get image " + _item.getFile().getAbsolutePath() + " " + _preferredSize.width + "x" + _preferredSize.height;
        }
        
    }
    
    synchronized void addMetadataTask(DefaultListItem item, boolean highPriority)
    {
        getWorker().addTask(new MetadataLoaderCallable(item), highPriority ? TASK_PRIORITY_HIGH : TASK_PRIORITY_LOW, false);
    }
    
    synchronized void addImageTask(DefaultListItem item, Dimension preferredSize, boolean highPriority)
    {
        getWorker().addTask(new ImageLoaderCallable(item, preferredSize), highPriority ? TASK_PRIORITY_HIGH : TASK_PRIORITY_LOW, true);
    }

    private synchronized PrioritizedSwingWorker<Void, DefaultListItem, Integer> getWorker()
    {
        if (null == _metadataWorker || _metadataWorker.isDone())
        {
            _metadataWorker = new PrioritizedSwingWorker<Void, DefaultListItem, Integer>()
            {
                
                @Override
                protected void process(List<DefaultListItem> chunks)
                {
                    Rectangle area = new Rectangle();
                    for (ListItem chunk : chunks)
                    {
                        if (isCancelled())
                        {
                            break;
                        }
                        ListItemGuiItem guiItem = getListItemGuiItem(chunk);
                        if (null != guiItem)
                        {
                            area.add(guiItem.area);
                        }
                        else
                        {
                            L.fine("Could not locate " + chunk + " on screen! Perhaps we are trying to process a task!?");
                        }
                    }
                    repaint(area);
                }
                
            };
            _executor.submit(_metadataWorker);
//            _metadataWorker.execute();
        }
        return _metadataWorker;
    }
    
    private static class POThumbListTestDialog extends PODialog
    {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        protected POThumbListTestDialog(Container root, Database database)
        {
            super("TITLE", CloseOperation.DISPOSE_ON_CLOSE, root, database);
        }
        
    }
    public final static String ACTIONNAME_FOCUS_DOWN = "focusDown";
    public final static String ACTIONNAME_FOCUS_NEXT = "focusNext";
    public final static String ACTIONNAME_FOCUS_NEXT_GROUP = "focusNextGroup";
    public final static String ACTIONNAME_FOCUS_PREVIOUS = "focusPrevious";

    public final static String ACTIONNAME_FOCUS_PREVIOUS_GROUP = "focusPreviousGroup";
    
    public final static String ACTIONNAME_FOCUS_UP = "focusUp";
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

//    public static void main(String[] args)
//    {
//        POThumbList thumbList = new POThumbList();
//        
//        thumbList.setItems(new File("F:\\Fotografier\\Personer\\Sonja"));
//        
//        JPanel p = new JPanel();
//        JScrollPane scrollPane = new JScrollPane(thumbList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setPreferredSize(new Dimension(500, 500));
//        p.add(scrollPane);
//        GuiComponentFactory.show(new POThumbListTestDialog(scrollPane));
//    }
    
    boolean _regroupPending = true;
    
    boolean _relayoutPending = true;
    
    private GuiItem _focusedGuiItem = null;
    
    private Grouper _grouper = new Grouper(new FileNameGrouper());
    
    private List<GuiItem> _guiItemList = new ArrayList<GuiItem>();
    
    private Map<ListItemGroupGuiItem, List<ListItemGuiItem>> _guiItemMap = new HashMap<ListItemGroupGuiItem, List<ListItemGuiItem>>();
    
    private ArrayList<DefaultListItem> _items = null;
    
    private Rectangle _mouseSelectionArea = null;
    
    private Point _mouseSelectionDragStart = null;
    
    private ListItemPainter _painter = new SquarePainter();
    
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
            public void focusGained(FocusEvent e)
            {
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e)
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
                    _zoom -= Math.signum(e.getWheelRotation())*10;
                    if (_zoom < 1)
                    {
                        _zoom = 10;
                    }
                    else if (_zoom > 100)
                    {
                        _zoom = 100;
                    }
                    L.info("Zoom level: " + _zoom);
                    scheduleRelayout();
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
                resetSelectionEventDirty();
                
                Rectangle repaintArea = new Rectangle();
                
                boolean extendSelection = e.isShiftDown() || e.isControlDown();
                if (!extendSelection)
                {
                    deselectItems(getSelectedItems());
                }
                GuiItem currentItem = getGuiItemAtPoint(e.getPoint());
                if (null != _mouseSelectionArea)
                {
                    repaintArea.add(_mouseSelectionArea);
                    
                    for (GuiItem guiItem : _guiItemList)
                    {
                        if (guiItem instanceof ListItemGuiItem)
                        {
                            ListItemGuiItem listItemGuiItem = (ListItemGuiItem)guiItem;
                            if (listItemGuiItem.isVisible)
                            {
                                if (listItemGuiItem.area.intersects(_mouseSelectionArea))
                                {
                                    boolean selectionState = e.isControlDown() ? !listItemGuiItem.isSelected() : true;
                                    setItemSelectionStatus(listItemGuiItem, selectionState);
                                }
                                else
                                {
                                    if (!extendSelection)
                                    {
                                        if (setItemSelectionStatus(listItemGuiItem, false))
                                        {
                                            repaintArea.add(listItemGuiItem.area);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    for (GuiItem item : getGuiItemsInArea(_mouseSelectionArea))
                    {
                        if (item instanceof ListItemGuiItem)
                        {
                            ListItemGuiItem listItemGuiItem = (ListItemGuiItem)item;
                            if (e.isControlDown())
                            {
                                toggleItemSelectionStatus(listItemGuiItem);
                            }
                            else
                            {
                                setItemSelectionStatus(listItemGuiItem, true);
                            }
                        }
                        repaintArea.add(item.area);
                    }
                    if (currentItem != null)
                    {
                        setFocusedGuiItem(currentItem);
                    }
                }
                else
                {
                    if (currentItem != null)
                    {
                        if (currentItem instanceof ListItemGuiItem)
                        {
                            onListItemClick((ListItemGuiItem) currentItem, repaintArea, e);
                        }
                        else if (currentItem instanceof ListItemGroupGuiItem)
                        {
                            onListItemGroupClick((ListItemGroupGuiItem) currentItem, repaintArea, e);
                        }

                        if (null != _focusedGuiItem)
                        {
                            repaintArea.add(_focusedGuiItem.area);
                        }
                        setFocusedGuiItem(currentItem);
                    }
                }
                
                _mouseSelectionDragStart = null;
                _mouseSelectionArea = null;
                
                fireSelectionEventIfDirty();
                
                repaint(repaintArea);
                
            }

            private void onListItemClick(ListItemGuiItem guiItem, Rectangle repaintArea, MouseEvent e)
            {
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
                            if (setItemSelectionStatus((ListItemGuiItem)gI, true))
                            {
                                repaintArea.add(gI.area);
                            }
                        }
                    }
                }
                else
                {
                    toggleItemSelectionStatus(guiItem);
                    repaintArea.add(guiItem.area);
                    
                    _shiftSelectionOldItem = null;
                }
            }
            
            private void onListItemGroupClick(ListItemGroupGuiItem guiItem, Rectangle repaintArea, MouseEvent e)
            {
                guiItem.isExpanded = !guiItem.isExpanded;
                scheduleRelayout();
            }
        });
    }
    
    protected boolean setItemSelectionStatus(ListItemGuiItem listItemGuiItem, boolean selected)
    {
        return listItemGuiItem.setSelected(selected);
    }
    
    protected void toggleItemSelectionStatus(ListItemGuiItem listItemGuiItem)
    {
        listItemGuiItem.setSelected(!listItemGuiItem.isSelected());
    }

    public synchronized void deselectItems(List<File> items)
    {
        setItemsSelectionStatus(items, false);
    }
    
    GuiItem getFocusedGuiItem()
    {
        return _focusedGuiItem;
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

    ListItem getListItemAtPoint(Point coord)
    {
        GuiItem guiItem = getGuiItemAtPoint(coord);
        if (guiItem instanceof ListItemGuiItem)
        {
            return ((ListItemGuiItem)guiItem).getItem();
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
            if (guiItem.getItem() == item)
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

    public List<File> getSelectedItems()
    {
        List<File> res = new ArrayList<File>();
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem instanceof ListItemGuiItem)
            {
                ListItemGuiItem listItemGuiItem = (ListItemGuiItem)guiItem;
                if (listItemGuiItem.isSelected())
                {
                    res.add(listItemGuiItem.getItem().getFile());
                }
            }
        }
        return res;
    }

//    @Override
//    public Iterator<ListItem> iterator()
//    {
//        return _items.iterator();
//    }
    
    private void scheduleRelayout()
    {
        if (getWidth() > 0 && getHeight() > 0)
        {
            _relayoutPending = true;
            repaint();
        }
        else
        {
            relayout();
        }
    }
    
    private void scheduleRegroup()
    {
        if (getWidth() > 0 && getHeight() > 0)
        {
            _regroupPending = true;
            repaint();
        }
        else
        {
            regroup();
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g); // Paint background
        
//        System.err.println("------------------ Painting"
//                + " affects " + g.getClipBounds() 
//                + " width=" + getWidth()
//                + " preferred width=" + getPreferredSize().width);
        
        if (_regroupPending)
        {
            regroup();
            _regroupPending = false;
        }
        else if (_relayoutPending)
        {
            relayout();
            _relayoutPending = false;
        }
        
        if (getWidth() != getPreferredSize().width)
        {
            // Component does not have its intended width, and this usually
            // happens when calls to relayout() causes the vertical scrollbar to
            // become visible, effectively decreasing the component's width. It
            // is therefore necessary to call relayout() once more to account
            // for the width change.
            
            relayout();
        }
        
        paintComponent((Graphics2D) g.create());
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
            g.draw(stroke.createStrokedShape(focusRect));
        }
    }
    
    private void paintGuiItems(Graphics2D g)
    {
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem.isVisible && guiItem.area.intersects(g.getClipBounds()))
            {
                guiItem.paint(g);
            }
        }
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
            L.fine("Mouse selection: " + mouseDragArea);
        }
    }

    private synchronized void regroup(/*ImageGrouper grouper*/)
    {
        Map<ListItemGroupGuiItem, List<ListItemGuiItem>> newGuiItemMap = new LinkedHashMap<ListItemGroupGuiItem, List<ListItemGuiItem>>();
        List<GuiItem> newGuiItemList = new ArrayList<GuiItem>();
        
        for (ImageGroup grp : _grouper.group(_items))
        {
            
            ListItemGroupGuiItem groupGuiItem = getGroupGuiItem(grp);
            if (groupGuiItem == null)
            {
                groupGuiItem = new ListItemGroupGuiItem(grp, new Rectangle(), this);
            }
            
            ArrayList<ListItemGuiItem> itemList = new ArrayList<ListItemGuiItem>();
            newGuiItemMap.put(groupGuiItem, itemList);
            newGuiItemList.add(groupGuiItem);
            
            for (ListItem item : grp.getItems())
            {
                ListItemGuiItem imageGuiItem = getListItemGuiItem(item);
                if (imageGuiItem == null)
                {
                    imageGuiItem = new ListItemGuiItem(item, _painter, new Rectangle(), this);
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

    private synchronized void relayout()
    {
        L.fine("relayout");
        
        Dimension visibleSize = null;
        Container parent = getParent();
        if (parent instanceof JViewport)
        {
            visibleSize = ((JViewport)parent).getExtentSize();
        }
        else
        {
            visibleSize = getSize();
        }
        Insets insets = getInsets();
        
        int visibleWidth = visibleSize.width - insets.left - insets.right;
        int visibleHeight = visibleSize.height - insets.top - insets.bottom;
        
        long itemArea = (1l * visibleHeight * visibleWidth * _zoom * _zoom) / (10000); //(long) (visibleHeight * visibleWidth * zoomPercent);
        
        if (itemArea == 0)
        {
            return;
        }
        
        double itemAspectRatio = _painter.getWidthToHeightRatio(itemArea);

        int accumulatedY = insets.top;

        double itemWidth = Math.sqrt(itemAspectRatio * itemArea);
        double itemHeight = Math.sqrt(itemArea * itemAspectRatio);
        int itemsWide = (int) Math.max(1, visibleWidth / itemWidth);
        try
        {
            itemWidth = visibleWidth / itemsWide;
        }
        catch (ArithmeticException e)
        {
            L.fine("Could not do this: " + visibleWidth +"/" + itemsWide);
            e.printStackTrace();
        }
        itemHeight = itemWidth / itemAspectRatio;

        for (Entry<ListItemGroupGuiItem, List<ListItemGuiItem>> entry : _guiItemMap.entrySet())
        {

            ListItemGroupGuiItem groupGuiItem = entry.getKey();
            groupGuiItem.area.x = insets.left;
            groupGuiItem.area.y = accumulatedY;
            groupGuiItem.area.width = visibleWidth - 1;
            groupGuiItem.area.height = ListItemGroupGuiItem.HEIGHT;

            accumulatedY += ListItemGroupGuiItem.HEIGHT;
            double x = 0;
            int accumulatedX = 0;
            int i = 0;

            if (entry.getValue().size() > 0)
            {
                for (ListItemGuiItem listItemGuiItem : entry.getValue())
                {
    
                    listItemGuiItem.isVisible = groupGuiItem.isExpanded;
                    if (groupGuiItem.isExpanded)
                    {
                        ListItem item = listItemGuiItem.getItem();
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
        
        
        setPreferredSize(new Dimension(getWidth(), accumulatedY));
        //System.err.println("Preferred size: " + getPreferredSize() + " Size: " + getSize());
        
        revalidate();
        
        repaint();
    }

    public synchronized void selectItems(List<File> items)
    {
        setItemsSelectionStatus(items, true);
    }
    
    public void setFocusedGuiItem(GuiItem focusedGuiItem)
    {
        L.fine("setFocusedGuiItem(" + focusedGuiItem + ")");
        Rectangle repaintArea = new Rectangle(focusedGuiItem.area);
        if (null != _focusedGuiItem)
        {
            repaintArea.add(_focusedGuiItem.area);
        }
        
        _focusedGuiItem = focusedGuiItem;
        
        repaint(/*repaintArea*/);
        scrollRectToVisible(_focusedGuiItem.area);
    }
    
    public synchronized void setItems(File folder)
    {
        setItems(Arrays.asList(folder.listFiles()));
        scheduleRegroup();
    }
    
    public synchronized void addItem(File file)
    {
        if (_items == null)
        {
            clearItems();
        }
        _items.add(new DefaultListItem(file, this));
        scheduleRegroup();
    }
    
    public synchronized void clearItems()
    {
        getWorker().clearTasks();
        
        _items = new ArrayList<DefaultListItem>();
        scheduleRegroup();
    }

    public synchronized void setItems(Iterable<File> items)
    {
        
        clearItems();
        
        Iterator<File> i = items.iterator();
        while (i.hasNext())
        {
            File f = i.next();
            _items.add(new DefaultListItem(f, this));
        }
        
        scheduleRegroup();
    }
    
    private synchronized void setItemsSelectionStatus(List<File> items, boolean selected)
    {
        boolean fire = false;
        Rectangle repaintArea = new Rectangle();
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem instanceof ListItemGuiItem)
            {
                ListItemGuiItem listItemGuiItem = (ListItemGuiItem)guiItem;
                if (items.contains(listItemGuiItem.getItem().getFile()))
                {
                    setItemSelectionStatus(listItemGuiItem, selected);
                    fire = true;
                    repaintArea.add(listItemGuiItem.area);
                }
            }
        }
        if (fire)
        {
            fireSelectionEvent();
        }
        repaint(repaintArea);
    }
    
    private void fireSelectionEvent()
    {
        _selectionEvent.fire(new SelectionEvent(this, getSelectedItems()));
    }
    
    private boolean _selectionEventDirty = false;
    
    void selectionHasChanged()
    {
        _selectionEventDirty = true;
    }
    
    void resetSelectionEventDirty()
    {
        _selectionEventDirty = false;
    }
    
    private void fireSelectionEventIfDirty()
    {
        if (_selectionEventDirty)
        {
            fireSelectionEvent();
        }
    }

    public void repaint(File indexedFile)
    {
        for (GuiItem guiItem : _guiItemList)
        {
            if (guiItem instanceof ListItemGuiItem)
            {
                ListItemGuiItem listItemGuiItem = (ListItemGuiItem)guiItem;
                if (listItemGuiItem.getItem().getFile().equals(indexedFile))
                {
                    repaint(listItemGuiItem.area);
                    return;
                }
            }
        }
    }

}
