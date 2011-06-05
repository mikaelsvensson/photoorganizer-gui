package info.photoorganizer.gui.shared;

import java.util.EventListener;

public interface POActionListenerListener extends EventListener
{
    void actionListenerEnabledChange(POActionListenerEvent event);
}
