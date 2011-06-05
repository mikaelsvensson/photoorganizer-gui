package info.photoorganizer.gui.shared;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public enum CloseOperation
{
    DO_NOTHING_ON_CLOSE(WindowConstants.DO_NOTHING_ON_CLOSE),// (defined in WindowConstants): Don't do anything; require the program to handle the operation in the windowClosing method of a registered WindowListener object.
    HIDE_ON_CLOSE(WindowConstants.HIDE_ON_CLOSE),// (defined in WindowConstants): Automatically hide the frame after invoking any registered WindowListener objects.
    DISPOSE_ON_CLOSE(WindowConstants.DISPOSE_ON_CLOSE),// (defined in WindowConstants): Automatically hide and dispose the frame after invoking any registered WindowListener objects.
    EXIT_ON_CLOSE(JFrame.EXIT_ON_CLOSE);// (defined in JFrame): Exit the application using the System exit method. Use this only in applications.
    
    private int _value = 0;

    public int getValue()
    {
        return _value;
    }

    private CloseOperation(int value)
    {
        _value = value;
    }
    
}
