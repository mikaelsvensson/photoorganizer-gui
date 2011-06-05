package info.photoorganizer.gui.components.tree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

public class POTree extends JTree
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public POTreeModel getModel()
    {
        return (POTreeModel) super.getModel();
    }

    @Override
    public void setModel(TreeModel newModel)
    {
        super.setModel(newModel);
    }
    
}
