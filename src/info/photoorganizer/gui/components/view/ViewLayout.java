package info.photoorganizer.gui.components.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class ViewLayout extends GridLayout
{
    private TreeNode _root = null;
    
    private JLabel _empty = null;

    private Component _rootComponent = null;
    
    public ViewLayout()
    {
        super(1, 1);
        
        initLayout();
    }

    private void initLayout()
    {
        _empty = new JLabel("Empty");
        //addLayoutComponent(null, _empty);
    }
    
    public void add(Component component, Position[] path) throws TreeException
    {
        TreeNode node = _root.getNode(path);
        node.addComponent(component, component.getName());
        refreshView();
        
    }
    
    private void refreshView()
    {
        Component root = refreshViewRegion(_root);
        if (root != _rootComponent)
        {
            removeLayoutComponent(_rootComponent);
            addLayoutComponent(null, root);
            _rootComponent = root;
        }
    }
    
    private Component refreshViewRegion(TreeNode node)
    {
        Component guiComponent = node.getGuiComponent();
        if (node.isLeaf())
        {
            if (guiComponent == null)
            {
                guiComponent = new JTabbedPane();
                node.setGuiComponent(guiComponent);
            }
            JTabbedPane tabs = (JTabbedPane) guiComponent;
            for (LeafNodeComponent compNode : node.getComponents())
            {
                boolean exists = false;
                for (Component comp : tabs.getComponents())
                {
                    if (comp == compNode.getComponent())
                    {
                        exists = true;
                    }
                }
                if (!exists)
                {
                    tabs.add(compNode.getComponent());
                }
            }
            for (Component comp : tabs.getComponents())
            {
                boolean removed = true;
                for (LeafNodeComponent compNode : node.getComponents())
                {
                    if (comp == compNode.getComponent())
                    {
                        removed = false;
                    }
                }
                if (removed)
                {
                    tabs.remove(comp);
                }
            }
            return guiComponent;
        }
        else
        {
            if (guiComponent == null)
            {
                guiComponent = new JSplitPane();
                node.setGuiComponent(guiComponent);
            }
            JSplitPane split = (JSplitPane) guiComponent;
            
            split.setOrientation(node.isVertical() ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);

            Component firstGuiComponent = refreshViewRegion(node.getFirst());
            if (firstGuiComponent != null && firstGuiComponent instanceof JTabbedPane)
            {
                split.setTopComponent(firstGuiComponent);
            }
            
            Component secondGuiComponent = refreshViewRegion(node.getSecond());
            if (secondGuiComponent != null && secondGuiComponent instanceof JTabbedPane)
            {
                split.setBottomComponent(secondGuiComponent);
            }
            
            return guiComponent;
        }
    }
    
    @Override
    public void addLayoutComponent(String name, Component component)
    {
        super.addLayoutComponent(name, component);
    }

}
