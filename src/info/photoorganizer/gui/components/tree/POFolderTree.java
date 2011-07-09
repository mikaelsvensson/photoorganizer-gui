package info.photoorganizer.gui.components.tree;

import java.awt.Component;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class POFolderTree extends POTreePanel<POFolderTreeModel, File>
{
    
    private class FolderIconRenderer extends DefaultTreeCellRenderer
    {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultTreeCellRenderer nonLeafRenderer = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
            renderer.setIcon(nonLeafRenderer.getIcon());
            
            return renderer;
        }
        
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public POFolderTree()
    {
        this(new POFolderTreeModel());
    }

    public POFolderTree(POFolderTreeModel treeModel)
    {
        super(treeModel);
        _tree.setCellRenderer(new FolderIconRenderer());
    }

}
