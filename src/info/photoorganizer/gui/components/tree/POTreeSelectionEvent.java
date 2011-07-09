package info.photoorganizer.gui.components.tree;

import java.util.EventObject;
import java.util.List;

/**
 * @author Mikael
 *
 * @param <T> the type of object used to represent tree nodes
 */
public class POTreeSelectionEvent<T> extends EventObject
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private List<T> _selection = null;

    public POTreeSelectionEvent(Object source)
    {
        super(source);
    }

    public POTreeSelectionEvent(Object source, List<T> selection)
    {
        super(source);
        _selection = selection;
    }

    public List<T> getSelection()
    {
        return _selection;
    }

}
