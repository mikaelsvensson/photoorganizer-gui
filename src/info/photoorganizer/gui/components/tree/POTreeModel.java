package info.photoorganizer.gui.components.tree;

import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

public abstract class POTreeModel implements TreeModel
{
    private Event<TreeModelListener, TreeModelEvent> _treeNodesChangedEvent = new Event<TreeModelListener, TreeModelEvent>(
            new EventExecuter<TreeModelListener, TreeModelEvent>()
            {
                @Override
                public void fire(TreeModelListener listener, TreeModelEvent event)
                {
                    listener.treeNodesChanged(event);
                }
            });
    private Event<TreeModelListener, TreeModelEvent> _treeNodesInsertedEvent = new Event<TreeModelListener, TreeModelEvent>(
            new EventExecuter<TreeModelListener, TreeModelEvent>()
            {
                @Override
                public void fire(TreeModelListener listener, TreeModelEvent event)
                {
                    listener.treeNodesInserted(event);
                }
            });
    private Event<TreeModelListener, TreeModelEvent> _treeNodesRemovedEvent = new Event<TreeModelListener, TreeModelEvent>(
            new EventExecuter<TreeModelListener, TreeModelEvent>()
            {
                @Override
                public void fire(TreeModelListener listener, TreeModelEvent event)
                {
                    listener.treeNodesRemoved(event);
                }
            });
    private Event<TreeModelListener, TreeModelEvent> _treeStructureChangedEvent = new Event<TreeModelListener, TreeModelEvent>(
            new EventExecuter<TreeModelListener, TreeModelEvent>()
            {
                @Override
                public void fire(TreeModelListener listener, TreeModelEvent event)
                {
                    listener.treeStructureChanged(event);
                }
            });
    
    @Override
    public void addTreeModelListener(TreeModelListener l)
    {
        _treeNodesChangedEvent.addListener(l);
        _treeNodesInsertedEvent.addListener(l);
        _treeNodesRemovedEvent.addListener(l);
        _treeStructureChangedEvent.addListener(l);
    }
    
    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {
        _treeNodesChangedEvent.removeListener(l);
        _treeNodesInsertedEvent.removeListener(l);
        _treeNodesRemovedEvent.removeListener(l);
        _treeStructureChangedEvent.removeListener(l);
    }
    
    protected void fireTreeNodesRemovedEvent(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        _treeNodesRemovedEvent.fire(new TreeModelEvent(source, path, childIndices, children));
    }

    protected void fireTreeNodesInsertedEvent(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        _treeNodesInsertedEvent.fire(new TreeModelEvent(source, path, childIndices, children));
    }

    protected void fireTreeStructureChangedEvent(Object source, Object[] path)
    {
        _treeStructureChangedEvent.fire(new TreeModelEvent(source, path));
    }
}
