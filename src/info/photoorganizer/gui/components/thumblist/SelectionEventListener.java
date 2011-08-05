package info.photoorganizer.gui.components.thumblist;

import java.util.EventListener;

public interface SelectionEventListener extends EventListener
{
    void selectionChanged(SelectionEvent event);
}
