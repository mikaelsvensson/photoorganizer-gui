package info.photoorganizer.gui.components.view;

import info.photoorganizer.metadata.KeywordTagDefinitionEvent;
import info.photoorganizer.metadata.KeywordTagDefinitionEventListener;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.StringUtils;
import info.photoorganizer.util.Event.EventExecuter;

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class TreeNode
{
    private List<LeafNodeComponent> _components = null;
    private TreeNode _first = null;
    private TreeNode _parent = null;
    private TreeNode _second = null;
    private boolean _vertical = false;
    private Component _guiComponent = null;
    
    public Component getGuiComponent()
    {
        return _guiComponent;
    }

    public void setGuiComponent(Component guiComponent)
    {
        _guiComponent = guiComponent;
    }

    private Event<TreeNodeEventListener, TreeNodeEvent> _changedEvent = new Event<TreeNodeEventListener, TreeNodeEvent>(
            new EventExecuter<TreeNodeEventListener, TreeNodeEvent>()
            {
                public void fire(TreeNodeEventListener listener, TreeNodeEvent event)
                {
                    listener.nodeChanged(event);
                }
            });
    
    public TreeNode()
    {
        this(null, null);
    }

    public TreeNode(boolean vertical, TreeNode first, TreeNode second)
    {
        super();
        _vertical = vertical;
        _first = first;
        _second = second;
    }

    public TreeNode(Component initialComponent, String label)
    {
        super();
        if (null != initialComponent && null != label && label.length() > 0)
        {
            getComponents().add(new LeafNodeComponent(initialComponent, label));
        }
    }
    
    public void addListener(TreeNodeEventListener listener)
    {
        _changedEvent.addListener(listener);
    }
    
    public void removeListener(TreeNodeEventListener listener)
    {
        _changedEvent.removeListener(listener);
    }
    
    private void fireChangedEvent(TreeNode source)
    {
        _changedEvent.fire(new TreeNodeEvent(source));
    }
    
    private TreeNode getRoot()
    {
        TreeNode root = this;
        while (root.getParent() != null)
        {
            root = root.getParent();
        }
        return root;
    }

    @Override
    public String toString()
    {
        if (isLeaf())
        {
            return StringUtils.join(getComponents().iterator(), ',');
        }
        else
        {
            return "(" + _first + ")(" + _second + ")";
        }
    }

    private void consolidate()
    {
        TreeNode existingBranch = !_first.isEmpty() ? _first : !_second.isEmpty() ? _second : null;
        if (existingBranch != null)
        {
            _components = existingBranch.getComponents();
            _vertical = existingBranch.isVertical();
            _first = null;
            _second = null;
        }
    }

    public int getComponentCount()
    {
        if (isLeaf())
        {
            return _components != null ? _components.size() : 0;
        }
        else
        {
            return (_first != null ? _first.getComponentCount() : 0) + (_second != null ? _second.getComponentCount() : 0);
        }
    }

    public List<LeafNodeComponent> getComponents()
    {
        if (null == _components)
        {
            _components = new ArrayList<LeafNodeComponent>();
        }
        return _components;
    }

    public TreeNode getFirst()
    {
        return _first;
    }

    private LeafNodeComponent getNodeComponent(Component component)
    {
        for (LeafNodeComponent c : getComponents())
        {
            if (c.getComponent() == component)
            {
                return c;
            }
        }
        return null;
    }

    public TreeNode getParent()
    {
        return _parent;
    }

    public TreeNode getSecond()
    {
        return _second;
    }
    
    public boolean isEmpty()
    {
        return getComponentCount() == 0;
    }

    public boolean isLeaf()
    {
        return _first == null && _second == null;
    }
    
    public boolean isVertical()
    {
        return _vertical;
    }
    
    public void removeComponent(Component component) 
    {
        getComponents().remove(getNodeComponent(component));
        if (isEmpty())
        {
            getParent().consolidate();
        }
        getRoot().fireChangedEvent(this);
    }
    
    public TreeNode getNode(Position[] path) throws TreeException
    {
        TreeNode node = this;
        if (null != path)
        {
            for (Position p : path)
            {
                node = (p == Position.LEFT_OR_TOP_OR_FIRST ? node.getFirst() : node.getSecond());
                if (node == null)
                {
                    throw new TreeException("Invalid path");
                }
            }
        }
        return node;
    }
    
    public void setFirst(TreeNode first)
    {
        _first = first;
    }
    
    public void setParent(TreeNode parent)
    {
        _parent = parent;
    }

    public void setSecond(TreeNode second)
    {
        _second = second;
    }

    public void setVertical(boolean vertical)
    {
        _vertical = vertical;
    }
    
    public void split(boolean vertical) throws TreeException
    {
        split(vertical, null, null, null);
    }
    
    public void split(boolean vertical, Component addedComponent, String addedComponentName, Position addedComponentPosition) throws TreeException
    {
        if (!isLeaf())
        {
            throw new TreeException("Cannot split a branch node."); 
        }
        
        TreeNode thisLeaf = new TreeNode();
        thisLeaf._components = _components;
        thisLeaf.setParent(this);
        
        TreeNode newLeaf = new TreeNode(addedComponent, addedComponentName);
        newLeaf.setParent(this);

        _vertical = vertical;
        _first = addedComponentPosition == Position.LEFT_OR_TOP_OR_FIRST ? newLeaf : thisLeaf;
        _second = addedComponentPosition == Position.LEFT_OR_TOP_OR_FIRST ? thisLeaf : newLeaf;
        _components = null;
    }
    
    public void addComponent(Component component, String label)
    {
        getComponents().add(new LeafNodeComponent(component, label));
        getRoot().fireChangedEvent(this);
    }
    
    public Position[] getPath()
    {
        List<Position> path = new LinkedList<Position>();
        getPath(path, this);
        return path.toArray(new Position[0]);
    }
    
    private void getPath(List<Position> path, TreeNode ref)
    {
        if (this != ref)
        {
            path.add(_first == ref ? Position.LEFT_OR_TOP_OR_FIRST : Position.RIGHT_OR_BOTTOM_OR_SECOND);
        }
        if (getParent() != null)
        {
            getParent().getPath(path, this);
        }
    }
}
