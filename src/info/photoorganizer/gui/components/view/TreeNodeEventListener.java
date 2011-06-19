package info.photoorganizer.gui.components.view;

import java.util.EventListener;

public interface TreeNodeEventListener extends EventListener
{
    void nodeChanged(TreeNodeEvent event);
}
