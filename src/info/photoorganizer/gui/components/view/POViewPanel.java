package info.photoorganizer.gui.components.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class POViewPanel extends JPanel
{

    private TreeNode _root = new TreeNode();
    
    private JLabel _empty = null;

    private Component _rootComponent = null;
    
    @Override
    public Component add(Component comp, int index)
    {
        return add(comp, comp.getName(), null);
    }

    @Override
    public void add(Component comp, Object constraints, int index)
    {
        add(comp, constraints);
    }

    @Override
    public void add(Component comp, Object constraints)
    {
        if (constraints == null || constraints instanceof Position[])
        {
            add(comp, comp.getName(), (Position[])constraints);
        }
    }

    @Override
    public Component add(Component comp)
    {
        return add(comp, comp.getName(), null);
    }

    @Override
    public Component add(String name, Component comp)
    {
        return add(comp, name, null);
    }

    public POViewPanel()
    {
        this(true);
    }

    public POViewPanel(boolean isDoubleBuffered)
    {
        this(new GridLayout(1, 1), isDoubleBuffered);
    }

    private POViewPanel(GridLayout layout, boolean isDoubleBuffered)
    {
        super(layout, isDoubleBuffered);
        //setSize(500, 500);
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        return _rootComponent.getPreferredSize();
    }

    @Override
    public void setPreferredSize(Dimension preferredSize)
    {
        super.setPreferredSize(preferredSize);
        _rootComponent.setPreferredSize(preferredSize);
    }

    public void split(Position[] path, boolean splitVertically) throws TreeException
    {
        TreeNode node = _root.getNode(path);
        node.split(splitVertically);
    }
    
    public Component add(POViewPaneInfo pane, Position[] path)
    {
        return add(pane.getComponent(), pane.getLabel(), path);
    }
    
    public Component add(Component component, String name, Position[] path)
    {
        try
        {
            TreeNode node = _root.getNode(path);
            node.addComponent(component, name);
            refreshView();
        }
        catch (TreeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return component;
    }
    
    private void refreshView()
    {
        Component root = refreshViewRegion(_root);
        if (root != _rootComponent)
        {
            if (null != _rootComponent)
            {
                remove(_rootComponent);
            }
            super.add(root);
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
                    tabs.addTab(compNode.getLabel(), compNode.getComponent());
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
            JSplitPane split = null;
            if (guiComponent == null)
            {
                split = new JSplitPane();
                node.setGuiComponent(split);
            }
            else
            {
                split = (JSplitPane) guiComponent;
            }
            
            split.setOrientation(node.isVertical() ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);

            Component firstGuiComponent = refreshViewRegion(node.getFirst());
            if (firstGuiComponent != null/* && firstGuiComponent instanceof JTabbedPane*/)
            {
                split.setTopComponent(firstGuiComponent);
            }
            
            Component secondGuiComponent = refreshViewRegion(node.getSecond());
            if (secondGuiComponent != null/* && secondGuiComponent instanceof JTabbedPane*/)
            {
                split.setBottomComponent(secondGuiComponent);
            }
            
            return split;
        }
    }
}
