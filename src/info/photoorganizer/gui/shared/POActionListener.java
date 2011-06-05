package info.photoorganizer.gui.shared;

import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class POActionListener implements ActionListener
{

    private Event<POActionListenerListener, POActionListenerEvent> _enabledEvent = new Event<POActionListenerListener, POActionListenerEvent>(new EventExecuter<POActionListenerListener, POActionListenerEvent>()
    {
        @Override
        public void fire(POActionListenerListener listener, POActionListenerEvent event)
        {
            listener.actionListenerEnabledChange(event);
        }
    });

    private boolean enabled = true;

    public POActionListener()
    {
        this(true);
    }
    
    public void addListener(POActionListenerListener listener)
    {
        _enabledEvent.addListener(listener);
    }
    
    public void removeListener(POActionListenerListener listener)
    {
        _enabledEvent.removeListener(listener);
    }
    
    public POActionListener(boolean enabled)
    {
        super();
        this.enabled = enabled;
    }
    
    @Override
    public final void actionPerformed(ActionEvent arg0)
    {
        if (isEnabled())
        {
            actionPerformedImpl(arg0);
        }
    }

    public abstract void actionPerformedImpl(ActionEvent event);

    public boolean isEnabled()
    {
        return enabled;
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        _enabledEvent.fire(new POActionListenerEvent(this));
    }
    
}
