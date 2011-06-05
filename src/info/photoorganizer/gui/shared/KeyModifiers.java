package info.photoorganizer.gui.shared;

import java.awt.event.InputEvent;

public enum KeyModifiers
{
    NONE(0),
    
    CTRL(InputEvent.CTRL_DOWN_MASK),
    SHIFT(InputEvent.SHIFT_DOWN_MASK),
    ALT(InputEvent.ALT_DOWN_MASK),
    
    CTRL_SHIFT(InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK),
    CTRL_ALT(InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK),
    SHIFT_ALT(InputEvent.SHIFT_DOWN_MASK + InputEvent.ALT_DOWN_MASK)
    ;
    private int _value = 0;
    
    public int getValue()
    {
        return _value;
    }
    
    private KeyModifiers(int value)
    {
        _value = value;
    }
}
