package info.photoorganizer.gui.components.tree;

import info.photoorganizer.util.I18n;

import java.awt.BorderLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Mikael
 *
 * @param <T>
 * @param <N> the type of object used to represent tree nodes
 */
public class POTreePanel<T extends TreeModel, N> extends JPanel /*implements TreeSelectionListener*/
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected POTree<T, N> _tree = null;

    public void addSelectionListener(POTreeSelectionListener<N> listener)
    {
        _tree.addSelectionListener(listener);
    }

    public void removeSelectionListener(POTreeSelectionListener<N> listener)
    {
        _tree.removeSelectionListener(listener);
    }

    public POTreePanel(T treeModel, boolean scrollable)
    {
        super();
        //_selectedUUIDs = new ArrayList<UUID>();
        
        _tree = new POTree<T, N>(treeModel);
        
        //_tree.addTreeSelectionListener(this);
        _tree.setModel(treeModel);
        _tree.setDragEnabled(true);
        _tree.setRootVisible(false);
        
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        if (scrollable)
        {
            add(new JScrollPane(_tree)/*, BorderLayout.CENTER*/);
        }
        else
        {
            add(_tree/*, BorderLayout.CENTER*/);
        }
    }

    public void addFocusListener(FocusListener l)
    {
        _tree.addFocusListener(l);
    }

    public void addKeyListener(KeyListener l)
    {
        _tree.addKeyListener(l);
    }

    public void addMouseListener(MouseListener l)
    {
        _tree.addMouseListener(l);
    }

    public void addMouseMotionListener(MouseMotionListener l)
    {
        _tree.addMouseMotionListener(l);
    }

    public void addMouseWheelListener(MouseWheelListener l)
    {
        _tree.addMouseWheelListener(l);
    }

    public void clearSelection()
    {
        _tree.clearSelection();
    }

    public Object getLastSelectedPathComponent()
    {
        return _tree.getLastSelectedPathComponent();
    }

    public List<N> getSelection()
    {
        return _tree.getSelection();
    }

    public TreeSelectionModel getSelectionModel()
    {
        return _tree.getSelectionModel();
    }

    public TreePath getSelectionPath()
    {
        return _tree.getSelectionPath();
    }

    public TreePath[] getSelectionPaths()
    {
        return _tree.getSelectionPaths();
    }

    public T getModel()
    {
        return (T) _tree.getModel();
    }

    public void makeVisible(TreePath path)
    {
        _tree.makeVisible(path);
    }

    public void removeFocusListener(FocusListener l)
    {
        _tree.removeFocusListener(l);
    }

    public void removeKeyListener(KeyListener l)
    {
        _tree.removeKeyListener(l);
    }
    public void removeMouseListener(MouseListener l)
    {
        _tree.removeMouseListener(l);
    }

    public void removeMouseMotionListener(MouseMotionListener l)
    {
        _tree.removeMouseMotionListener(l);
    }
    
//    public List<UUID> getSelection()
//    {
//        return (List<UUID>) _selectedUUIDs.clone();
//    }

    public void removeMouseWheelListener(MouseWheelListener l)
    {
        _tree.removeMouseWheelListener(l);
    }

    public void setDragEnabled(boolean arg0)
    {
        _tree.setDragEnabled(arg0);
    }

//    @Override
//    public void setModel(T newModel)
//    {
//        super.setModel(newModel);
//    }

    public final void setDropMode(DropMode arg0)
    {
        _tree.setDropMode(arg0);
    }

    public void setDropTarget(DropTarget dt)
    {
        _tree.setDropTarget(dt);
    }

    public void setEditable(boolean flag)
    {
        _tree.setEditable(flag);
    }

    public void setSelectionPath(TreePath path)
    {
        _tree.setSelectionPath(path);
    }

    public void setSelectionPaths(TreePath[] paths)
    {
        _tree.setSelectionPaths(paths);
    }

    public void setTransferHandler(TransferHandler newHandler)
    {
        _tree.setTransferHandler(newHandler);
    }

    protected String getI18nText(String key, Object... parameters)
    {
        return I18n.getInstance().getString(getClass(), key, parameters);
    }

//    @Override
//    public void valueChanged(TreeSelectionEvent e)
//    {
//        for (TreePath path : e.getPaths())
//        {
//            if (path.getLastPathComponent() instanceof KeywordTagDefinition)
//            {
//                KeywordTagDefinition keyword = (KeywordTagDefinition) path.getLastPathComponent();
//                if (e.isAddedPath(path))
//                {
//                    _selectedUUIDs.add(keyword.getId());
//                }
//                else
//                {
//                    _selectedUUIDs.remove(keyword.getId());
//                }
//            }
//        }
//        System.err.println("Selected UUIDs: " + _selectedUUIDs.toString());
//    }
    
}
