package info.photoorganizer.gui;

import info.photoorganizer.database.autoindexing.POFileFilter;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;

public class RejectedFileExtensionFileFilter implements POFileFilter, Serializable
{
    private static final long serialVersionUID = 1L;
    
    private HashSet<String> rejectedFileTypes = null;

    public HashSet<String> getRejectedFileTypes()
    {
        if (null == rejectedFileTypes)
        {
            rejectedFileTypes = new HashSet<String>();
//            rejectedFileTypes.addAll(Arrays.asList("info", "thumbs", "db", "txt"));
        }
        return rejectedFileTypes;
    }

    public void setRejectedFileTypes(String... rejectedFileTypes)
    {
        HashSet<String> set = getRejectedFileTypes();
        set.clear();
        for (String ext : rejectedFileTypes)
        {
            set.add(ext);
        }
    }

    @Override
    public boolean accept(File f)
    {
        String fileType = f.getName().substring(f.getName().lastIndexOf('.')+1);
        return !getRejectedFileTypes().contains(fileType);
    }

    @Override
    public RejectedFileExtensionFileFilter cloneDeep()
    {
        RejectedFileExtensionFileFilter clone = new RejectedFileExtensionFileFilter();
        if (null != rejectedFileTypes)
        {
            clone.rejectedFileTypes = new HashSet<String>(rejectedFileTypes);
        }
        return clone;
    }

    @Override
    public String toString()
    {
        return I18n.getInstance().getString(POFileFilter.class, "REJECTED_FILE_EXTENSION_TOSTRING_PATTERN", StringUtils.join(getRejectedFileTypes().iterator(), true));
    }
}