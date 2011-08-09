package info.photoorganizer.gui.components.tree;

import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Mikael
 *
 * @param <N> the type of object used to represent tree nodes
 */
class POTree<T extends TreeModel, N> extends JTree implements TreeSelectionListener
{
    @Override
    public T getModel()
    {
        return (T) super.getModel();
    }

    private Event<POTreeSelectionListener<N>, POTreeSelectionEvent<N>> _selectionEvent = new Event<POTreeSelectionListener<N>, POTreeSelectionEvent<N>>(
            new EventExecuter<POTreeSelectionListener<N>, POTreeSelectionEvent<N>>()
            {
                @Override
                public void fire(POTreeSelectionListener<N> listener, POTreeSelectionEvent<N> event)
                {
                    listener.selectionChanged(event);
                }
            });
    
    public void addSelectionListener(POTreeSelectionListener<N> listener)
    {
        _selectionEvent.addListener(listener);
    }
    
    public void removeSelectionListener(POTreeSelectionListener<N> listener)
    {
        _selectionEvent.removeListener(listener);
    }
    
    private static final long serialVersionUID = 1L;
    
//    private ArrayList<T> _selection = new ArrayList<T>();

    public POTree(TreeModel newModel)
    {
        super(newModel);
        addTreeSelectionListener(this);
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        if (value instanceof File)
        {
            File fileSystemItem = (File)value;
            return fileSystemItem.getName().length() > 0 ? fileSystemItem.getName() : fileSystemItem.getPath();
        }
        else
        {
            return value.toString();
        }
    }
    
    public List<N> getSelection()
    {
        ArrayList<N> _selection = new ArrayList<N>();
        TreePath[] selectionPaths = getSelectionPaths();
        if (null != selectionPaths)
        {
            for (TreePath path : selectionPaths)
            {
                try
                {
                    _selection.add((N) path.getLastPathComponent());
                }
                catch (ClassCastException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
        }
        return _selection;
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
//        for (TreePath path : e.getPaths())
//        {
//            try
//            {
//                T keyword = (T) path.getLastPathComponent();
//                if (e.isAddedPath(path))
//                {
//                    _selection.add(keyword);
//                }
//                else
//                {
//                    _selection.remove(keyword);
//                }
//            }
//            catch (ClassCastException ex)
//            {
//                // TODO Auto-generated catch block
//                ex.printStackTrace();
//            }
//        }
//        System.err.println("Selection: " + _selection.toString());
        _selectionEvent.fire(new POTreeSelectionEvent<N>(this, getSelection()));
    }
}