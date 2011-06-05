package info.photoorganizer.gui.shared;

import java.awt.event.KeyEvent;

public enum Keys
{
    DELETE(KeyEvent.VK_DELETE),
    INSERT(KeyEvent.VK_INSERT), 
    ESCAPE(KeyEvent.VK_ESCAPE), 
    UP(KeyEvent.VK_UP), 
    DOWN(KeyEvent.VK_DOWN), 
    ENTER(KeyEvent.VK_ENTER);
    
    private Keys(int keyCode)
    {
        _keyCode = keyCode;
    }

    private int _keyCode = 0;

    public int getKeyCode()
    {
        return _keyCode;
    }
}
