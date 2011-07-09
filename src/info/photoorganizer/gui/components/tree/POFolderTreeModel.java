package info.photoorganizer.gui.components.tree;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.tree.TreePath;

public class POFolderTreeModel extends POTreeModel
{
    
    private FilenameFilter _folderFilter = new FilenameFilter()
    {
        
        @Override
        public boolean accept(File dir, String name)
        {
            return (new File(dir, name)).isDirectory();
        }
    };

    private class Roots
    {

        @Override
        public String toString()
        {
            return "/";
        }
        
    }

    @Override
    public Object getChild(Object parent, int index)
    {
        if (parent instanceof File)
        {
            File folder = (File) parent;
            return new File(folder, folder.list(_folderFilter)[index]);
        }
        else
        {
            return File.listRoots()[index];
        }
    }

    @Override
    public int getChildCount(Object parent)
    {
        if (parent instanceof File)
        {
            File fileSystemItem = (File) parent;
            if (fileSystemItem.isDirectory())
            {
                String[] children = fileSystemItem.list(_folderFilter);
                return children != null ? children.length : 0;
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return File.listRoots().length;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        File[] haystack = null;
        File needle = (File) child;
        if (parent instanceof File)
        {
            File folder = (File) parent;
            haystack = folder.listFiles();
        }
        else
        {
            haystack = File.listRoots();
        }
        
        int i = 0;
        for (File file : haystack)
        {
            if (file.equals(needle))
            {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public Object getRoot()
    {
        return new Roots();
    }

    @Override
    public boolean isLeaf(Object node)
    {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        // TODO Auto-generated method stub

    }

}
