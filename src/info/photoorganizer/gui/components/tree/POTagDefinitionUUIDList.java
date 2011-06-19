package info.photoorganizer.gui.components.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class POTagDefinitionUUIDList implements Transferable, Serializable
{
    public static final DataFlavor TAG_DEFINITION_UUID_LIST_DATAFLAVOR = new DataFlavor(POTagDefinitionUUIDList.class, "UUID List");
    
    private static final DataFlavor[] FLAVORS = { TAG_DEFINITION_UUID_LIST_DATAFLAVOR };
    
    private List<UUID> _UUIDs = null;

    public POTagDefinitionUUIDList(List<UUID> uuids)
    {
        _UUIDs = uuids;
        System.err.println("POTagDefinitionUUIDList with UUIDs " + _UUIDs.toString());
    }

    public List<UUID> getUUIDs()
    {
        return _UUIDs;
    }
    
//    public List<KeywordTagDefinition> getKeywords()
//    {
//        List<KeywordTagDefinition> res = new ArrayList<KeywordTagDefinition>();
//        for (TreePath path : _paths)
//        {
//            if (path.getLastPathComponent() instanceof KeywordTagDefinition)
//            {
//                res.add((KeywordTagDefinition) path.getLastPathComponent());
//            }
//        }
//        return res;
//    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (isDataFlavorSupported(flavor))
        {
            return this;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return FLAVORS;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        for (DataFlavor f : getTransferDataFlavors())
        {
            if (f.equals(flavor))
            {
                return true;
            }
        }
        return false;
    }

}
