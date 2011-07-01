package info.photoorganizer.gui.components.thumblist;

import java.util.List;

public class ImageGroup
{
    private boolean _expanded = false;
    private String _header = null;
    private List<ListItem> _items = null;

    public List<ListItem> getItems()
    {
        return _items;
    }

    public void setItems(List<ListItem> items)
    {
        _items = items;
    }

    public ImageGroup(boolean expanded, String header, List<ListItem> items)
    {
        super();
        _expanded = expanded;
        _header = header;
        _items = items;
    }

    public String getHeader()
    {
        return _header;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_header == null) ? 0 : _header.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageGroup other = (ImageGroup) obj;
        if (_header == null)
        {
            if (other._header != null)
                return false;
        }
        else if (!_header.equals(other._header))
            return false;
        return true;
    }

    public boolean isExpanded()
    {
        return _expanded;
    }

    public void setExpanded(boolean expanded)
    {
        _expanded = expanded;
    }

    public void setHeader(String header)
    {
        _header = header;
    }
}
