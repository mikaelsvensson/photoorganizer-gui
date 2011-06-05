package info.photoorganizer.gui.shared;

import java.awt.FlowLayout;

public enum FlowLayoutAlignment
{
    LEFT(FlowLayout.LEFT), 
    RIGHT(FlowLayout.RIGHT), 
    CENTER(FlowLayout.CENTER), 
    LEADING(FlowLayout.LEADING), 
    TRAILING(FlowLayout.TRAILING);
    
    private FlowLayoutAlignment(int value)
    {
        _value = value;
    }

    private int _value = 0;

    public int getValue()
    {
        return _value;
    }
}
