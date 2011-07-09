package info.photoorganizer.gui.components.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

final class POTree<T> extends JTree /*implements TreeSelectionListener*/
{
    private static final long serialVersionUID = 1L;
    
//    private ArrayList<T> _selection = new ArrayList<T>();

    public POTree(TreeModel newModel)
    {
        super(newModel);
//        addTreeSelectionListener(this);
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
    
    public List<T> getSelection()
    {
        ArrayList<T> _selection = new ArrayList<T>();
        for (TreePath path : getSelectionPaths())
        {
            try
            {
                _selection.add((T) path.getLastPathComponent());
            }
            catch (ClassCastException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
        return _selection;
    }
    
    /*
    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
        for (TreePath path : e.getPaths())
        {
            try
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
            catch (ClassCastException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
        System.err.println("Selection: " + _selection.toString());
    }

    public List<T> getSelection()
    {
        return _selection;
    }
    */
}