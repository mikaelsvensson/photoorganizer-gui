package info.photoorganizer.gui.shared;

import java.util.EventObject;

public class POActionListenerEvent extends EventObject
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public POActionListenerEvent(Object source)
    {
        super(source);
    }

    @Override
    public POActionListener getSource()
    {
        return (POActionListener) super.getSource();
    }
    
}
