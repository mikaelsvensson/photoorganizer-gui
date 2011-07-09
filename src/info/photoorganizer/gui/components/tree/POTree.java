package info.photoorganizer.gui.components.tree;

import info.photoorganizer.metadata.KeywordTagDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

final class POTree<T> extends JTree implements TreeSelectionListener
{
    private static final long serialVersionUID = 1L;
    
    private ArrayList<T> _selection = new ArrayList<T>();

    public POTree(TreeModel newModel)
    {
        super(newModel);
        addTreeSelectionListener(this);
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        if (value instanceof File)
        {
            File fileSystemItem = (File)value;
            return fileSystemItem.getName().length() > 0 ? fileSystemItem.getName() : fileSystemItem.getPath();
        }
        else
        {
            return value.toString();
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
        for (TreePath path : e.getPaths())
        {
            if (path.getLastPathComponent() instanceof KeywordTagDefinition)
            {
                T keyword = (T) path.getLastPathComponent();
                if (e.isAddedPath(path))
                {
                    _selection.add(keyword);
                }
                else
                {
                    _selection.remove(keyword);
                }
            }
        }
        System.err.println("Selection: " + _selection.toString());
    }

    public List<T> getSelection()
    {
        return _selection;
    }
}