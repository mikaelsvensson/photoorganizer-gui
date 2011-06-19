package info.photoorganizer.gui.components.tree;

import info.photoorganizer.metadata.KeywordTagDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class POTree extends JTree implements TreeSelectionListener
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private ArrayList<UUID> _selectedUUIDs = null;

    public List<UUID> getSelection()
    {
        return (List<UUID>) _selectedUUIDs.clone();
    }

    public POTree(POTreeModel treeModel)
    {
        super();
        _selectedUUIDs = new ArrayList<UUID>();
        addTreeSelectionListener(this);
        setModel(treeModel);
        setDragEnabled(true);
        setRootVisible(false);
        setTransferHandler(new POTreeTransferHandler(this));
    }

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

    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
        for (TreePath path : e.getPaths())
        {
            if (path.getLastPathComponent() instanceof KeywordTagDefinition)
            {
                KeywordTagDefinition keyword = (KeywordTagDefinition) path.getLastPathComponent();
                if (e.isAddedPath(path))
                {
                    _selectedUUIDs.add(keyword.getId());
                }
                else
                {
                    _selectedUUIDs.remove(keyword.getId());
                }
            }
        }
        System.err.println("Selected UUIDs: " + _selectedUUIDs.toString());
    }
    
}
