package info.photoorganizer.gui.components.view;

import java.util.EventObject;

public class TreeNodeEvent extends EventObject
{

    private static final long serialVersionUID = -2679683136706976707L;

    public TreeNodeEvent(Object source)
    {
        super(source);
    }

    @Override
    public TreeNode getSource()
    {
        if (source instanceof TreeNode)
        {
            return (TreeNode) source;
        }
        return null;
    }
}
