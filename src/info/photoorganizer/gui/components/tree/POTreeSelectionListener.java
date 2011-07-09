package info.photoorganizer.gui.components.tree;

import java.util.EventListener;

/**
 * @author Mikael
 *
 * @param <T> the type of object used to represent tree nodes
 */
public interface POTreeSelectionListener<T> extends EventListener
{
    void selectionChanged(POTreeSelectionEvent<T> event);
}
