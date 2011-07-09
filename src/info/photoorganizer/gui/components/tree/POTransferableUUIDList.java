package info.photoorganizer.gui.components.tree;

import info.photoorganizer.metadata.KeywordTagDefinition;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class POTransferableUUIDList implements Transferable, Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final DataFlavor TAG_DEFINITION_UUID_LIST_DATAFLAVOR = new DataFlavor(POTransferableUUIDList.class, "UUID List");
    
    private static final DataFlavor[] FLAVORS = { TAG_DEFINITION_UUID_LIST_DATAFLAVOR };
    
    private List<UUID> _UUIDs = null;

    public POTransferableUUIDList(List<KeywordTagDefinition> selection)
    {
        List<UUID> uuids = new ArrayList<UUID>();
        for (KeywordTagDefinition keyword : selection)
        {
            uuids.add(keyword.getId());
        }
        _UUIDs = uuids;
    }

    public List<UUID> getUUIDs()
    {
        return _UUIDs;
    }

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
