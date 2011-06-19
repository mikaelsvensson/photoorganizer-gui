package info.photoorganizer.gui.components.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class LeafNode extends TreeNode
{
    private List<LeafNodeComponent> _components = new ArrayList<LeafNodeComponent>();
    
    private JTabbedPane _tabs = null;

    public LeafNode()
    {
        this(null, null);
    }
    
    public LeafNode(Component initialComponent, String label)
    {
        super();
        if (null != initialComponent && null != label && label.length() > 0)
        {
            _components.add(new LeafNodeComponent(initialComponent, label));
        }
    }

//    public Iterator<LeafNodeComponent> getComponents()
//    {
//        return _components.iterator();
//    }
    
    public boolean isEmpty()
    {
        return getComponentCount() == 0;
    }
    
    public void addComponent(Component component) 
    {
        
    }
    
//    public void removeComponent(Component component) 
//    {
//        _components.remove(getNodeComponent(component));
//        getParent().consolidate();
//    }
    
    private LeafNodeComponent getNodeComponent(Component component)
    {
        for (LeafNodeComponent c : _components)
        {
            if (c.getComponent() == component)
            {
                return c;
            }
        }
        return null;
    }

    @Override
    public int getComponentCount()
    {
        return _components.size();
    }
    
    public BranchNode split(boolean vertical, Component addedComponent, Position addedComponentPosition)
    {
        LeafNode newLeaf = new LeafNode(addedComponent, "test");
        
        LeafNode first = addedComponentPosition == Position.LEFT_OR_TOP_OR_FIRST ? newLeaf : this;
        LeafNode second = addedComponentPosition == Position.LEFT_OR_TOP_OR_FIRST ? this : newLeaf;
        BranchNode branch = new BranchNode(vertical, first, second);
        
        return branch;
    }
}
