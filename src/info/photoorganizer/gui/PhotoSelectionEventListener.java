package info.photoorganizer.gui;

import java.util.EventListener;

public interface PhotoSelectionEventListener extends EventListener
{
    void photoSelectionChanged(PhotoSelectionEvent event);
}
