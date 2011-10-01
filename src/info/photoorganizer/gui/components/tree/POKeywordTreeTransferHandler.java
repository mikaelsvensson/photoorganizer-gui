package info.photoorganizer.gui.components.tree;

import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;
import info.photoorganizer.metadata.KeywordTagDefinition;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 * @author Mikael
 * @see http://www.javaprogrammingforums.com/java-code-snippets-tutorials/3141-drag-drop-jtrees.html
 */
public class POKeywordTreeTransferHandler extends TransferHandler
{
    private static final Logger L = info.photoorganizer.util.Log.getLogger(POKeywordTree.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private POKeywordTreeModel _tree = null;

    public POKeywordTreeTransferHandler(POTreePanel<POKeywordTreeModel, KeywordTagDefinition> poTree)
    {
        super();
        _tree = poTree.getModel();
    }
    
    public POKeywordTreeTransferHandler(POTree<POKeywordTreeModel, KeywordTagDefinition> poTree)
    {
        super();
        _tree = poTree.getModel();
    }

    @Override
    public boolean canImport(TransferSupport support)
    {
        support.setShowDropLocation(true);
        if (support.isDataFlavorSupported(POTransferableUUIDList.TAG_DEFINITION_UUID_LIST_DATAFLAVOR))
        {
            TreePath dropPath = ((JTree.DropLocation)support.getDropLocation()).getPath();
            if (dropPath != null && dropPath.getLastPathComponent() instanceof KeywordTagDefinition)
            {
                KeywordTagDefinition target = (KeywordTagDefinition) dropPath.getLastPathComponent();
                try
                {
                    List<UUID> uuids = ((POTransferableUUIDList)support.getTransferable().getTransferData(POTransferableUUIDList.TAG_DEFINITION_UUID_LIST_DATAFLAVOR)).getUUIDs();
                    for (UUID uuid : uuids)
                    {
                    }
                    
                    return true;
                }
                catch (UnsupportedFlavorException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else
            {
                // Convert to top-level keyword
                return true;
            }
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c)
    {
        if (c instanceof POTree)
        {
            List<KeywordTagDefinition> selection = ((POTree)c).getSelection();
            L.finer("createTransferable using this selection: " + selection.toString());
            return new POTransferableUUIDList(selection);
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action)
    {
        if (action == TransferHandler.MOVE)
        {
            try
            {
                List<UUID> uuids = ((POTransferableUUIDList)data.getTransferData(POTransferableUUIDList.TAG_DEFINITION_UUID_LIST_DATAFLAVOR)).getUUIDs();
                for (UUID uuid : uuids)
                {
                    // Remove keyword (path)
                    //_tree.
                }
            }
            catch (UnsupportedFlavorException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return TransferHandler.MOVE;
    }

    @Override
    public boolean importData(TransferSupport support)
    {
        if (canImport(support))
        {
            try
            {
                Transferable transferable = support.getTransferable();
                List<UUID> uuids = ((POTransferableUUIDList)transferable.getTransferData(POTransferableUUIDList.TAG_DEFINITION_UUID_LIST_DATAFLAVOR)).getUUIDs();
                TreePath dropPath = ((JTree.DropLocation)support.getDropLocation()).getPath();
                KeywordTagDefinition target = null;
                if (null != dropPath && dropPath.getLastPathComponent() instanceof KeywordTagDefinition)
                {
                    target = (KeywordTagDefinition) dropPath.getLastPathComponent();
                }
                else
                {
                    target = _tree.getRoot();
                }
                for (UUID uuid : uuids)
                {
                    KeywordTagDefinition keyword = _tree.getRoot().getChildById(uuid, true);
                    keyword.moveTo(target);
                }
                target.store();
            }
            catch (UnsupportedFlavorException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (DatabaseStorageException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

}
