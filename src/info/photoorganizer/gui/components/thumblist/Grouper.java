package info.photoorganizer.gui.components.thumblist;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Grouper
{
    private ImageGrouper _grouper = null;

    public Grouper(ImageGrouper grouper)
    {
        super();
        _grouper = grouper;
    }

    public List<ImageGroup> group(Iterable<DefaultListItem> items)
    {
        SortedMap<String, List<ListItem>> groups = new TreeMap<String, List<ListItem>>();
        
        if (items != null)
        {
            for (ListItem item : items)
            {
                String groupName = _grouper.getGroupName(item);
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
