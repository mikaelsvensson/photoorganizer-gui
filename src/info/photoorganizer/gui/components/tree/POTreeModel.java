package info.photoorganizer.gui.components.tree;

import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.metadata.KeywordTagDefinitionEvent;
import info.photoorganizer.metadata.KeywordTagDefinitionEventListener;
import info.photoorganizer.metadata.TagDefinitionEvent;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class POTreeModel implements TreeModel, KeywordTagDefinitionEventListener
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
    private KeywordTagDefinition root = null;
    
    public POTreeModel(KeywordTagDefinition root)
    {
        super();
        this.root = root;
        this.root.addKeywordEventListener(this);
    }
    
    @Override
    public void addTreeModelListener(TreeModelListener l)
    {
        _treeNodesChangedEvent.addListener(l);
        _treeNodesInsertedEvent.addListener(l);
        _treeNodesRemovedEvent.addListener(l);
        _treeStructureChangedEvent.addListener(l);
    }
    
    public void fireTreeNodesChangedEvent(TreeModelEvent event)
    {
        _treeNodesChangedEvent.fire(event);
    }
    
    @Override
    public Object getChild(Object parent, int index)
    {
        return ((KeywordTagDefinition)parent).getChild(index);
    }
    
    @Override
    public int getChildCount(Object parent)
    {
        return ((KeywordTagDefinition)parent).getChildCount();
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        return ((KeywordTagDefinition)parent).getIndexOfChild((KeywordTagDefinition) child);
    }
    
    @Override
    public Object getRoot()
    {
        return root;
    }
    
    @Override
    public boolean isLeaf(Object node)
    {
        return ((KeywordTagDefinition)node).getChildCount() == 0;
    }
    
    @Override
    public void tagChanged(TagDefinitionEvent event)
    {
        if (event instanceof KeywordTagDefinitionEvent)
        {
            KeywordTagDefinitionEvent keywordEvent = (KeywordTagDefinitionEvent)event;
            _treeNodesRemovedEvent.fire(new TreeModelEvent(
                    keywordEvent.getSource(), 
                    keywordEvent.getSource().getPath(), 
                    keywordEvent.getTargetIndices(), 
                    keywordEvent.getTargets()
            ));
        }
    }
    
    @Override
    public void tagDeleted(TagDefinitionEvent event)
    {
        if (event instanceof KeywordTagDefinitionEvent)
        {
            KeywordTagDefinitionEvent keywordEvent = (KeywordTagDefinitionEvent)event;
            _treeNodesRemovedEvent.fire(new TreeModelEvent(
                    keywordEvent.getSource(), 
                    keywordEvent.getSource().getPath(), 
                    keywordEvent.getTargetIndices(), 
                    keywordEvent.getTargets()
            ));
        }
    }
    
    @Override
    public void keywordInserted(KeywordTagDefinitionEvent keywordEvent)
    {
        _treeNodesInsertedEvent.fire(new TreeModelEvent(
                keywordEvent.getSource(), 
                keywordEvent.getSource().getPath(), 
                keywordEvent.getTargetIndices(), 
                keywordEvent.getTargets()
                ));
    }
    
    @Override
    public void keywordStructureChanged(KeywordTagDefinitionEvent keywordEvent)
    {
        _treeStructureChangedEvent.fire(new TreeModelEvent(
                keywordEvent.getSource(), 
                keywordEvent.getSource().getPath()
                ));
    }
    
    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {
        _treeNodesChangedEvent.removeListener(l);
        _treeNodesInsertedEvent.removeListener(l);
        _treeNodesRemovedEvent.removeListener(l);
        _treeStructureChangedEvent.removeListener(l);
    }
    
    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        ((KeywordTagDefinition)path.getLastPathComponent()).setName(newValue.toString());
    }
}
