package info.photoorganizer.gui.components;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

public class PORadioButton extends JRadioButton
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Object _value = null;

    public PORadioButton()
    {
        super();
    }

    public PORadioButton(Action arg0)
    {
        super(arg0);
    }

    public PORadioButton(Icon arg0)
    {
        super(arg0);
    }

    public PORadioButton(Icon arg0, boolean arg1)
    {
        super(arg0, arg1);
    }

    public PORadioButton(String arg0)
    {
        super(arg0);
    }

    public PORadioButton(String arg0, boolean arg1)
    {
        super(arg0, arg1);
    }

    public PORadioButton(String arg0, Icon arg1)
    {
        super(arg0, arg1);
    }
    
    public PORadioButton(String arg0, Icon arg1, boolean arg2)
    {
        super(arg0, arg1, arg2);
    }

    public Object getValue()
    {
        return _value;
    }

    public void setValue(Object value)
    {
        _value = value;
    }
}
