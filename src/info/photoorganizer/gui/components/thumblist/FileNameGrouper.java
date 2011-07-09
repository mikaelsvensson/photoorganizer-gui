package info.photoorganizer.gui.components.thumblist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class FileNameGrouper implements ImageGrouper
{

    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getHeaders()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListItem[] getItems(String header)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ImageGroup> group(Iterable<ListItem> items)
    {
        SortedMap<String, List<ListItem>> groups = new TreeMap<String, List<ListItem>>();
        
        if (items != null)
        {
            for (ListItem item : items)
            {
                String groupName = String.valueOf(item.getFile().getName().toUpperCase().charAt(0));
                List<ListItem> groupItems = null;
                if (groups.containsKey(groupName))
                {
                    groupItems = groups.get(groupName);
                }
                else
                {
                    groupItems = new ArrayList<ListItem>();
                    groups.put(groupName, groupItems);
                }
                groupItems.add(item);
            }
            
        }
        List<ImageGroup> res = new ArrayList<ImageGroup>();
        for (Entry<String, List<ListItem>> entry : groups.entrySet())
        {
            res.add(new ImageGroup(true, entry.getKey(), entry.getValue()));
        }
        return res;
    }

}
