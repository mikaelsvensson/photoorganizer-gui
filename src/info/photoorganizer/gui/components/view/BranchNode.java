package info.photoorganizer.gui.components.view;

import java.awt.Component;

public class BranchNode extends TreeNode
{
    private boolean _vertical = false;
    private TreeNode _first = null;
    private TreeNode _second = null;
    
    public BranchNode(boolean vertical, TreeNode first, TreeNode second)
    {
        super();
        _vertical = vertical;
        _first = first;
        _second = second;
    }

    public boolean isVertical()
    {
        return _vertical;
    }

    public void setVertical(boolean vertical)
    {
        _vertical = vertical;
    }

    public TreeNode getFirst()
    {
        return _first;
    }

    public void setFirst(TreeNode first)
    {
        _first = first;
    }

    public TreeNode getSecond()
    {
        return _second;
    }

    public void setSecond(TreeNode second)
    {
        _second = second;
    }

    public void split(boolean vertically, Component... secondBranchComponents)
    {
        
    }

    public void consolidate()
    {
    }

    @Override
    public int getComponentCount()
    {
        return (_first != null ? _first.getComponentCount() : 0) + (_second != null ? _second.getComponentCount() : 0);
    }
}
