package info.photoorganizer.gui.shared;

import javax.swing.JComponent;

public class KeyboardShortcutDefinition
{
    private POActionListener _action = null;
    private String _actionParameter = null;
    private Keys _key = null;
    private KeyModifiers _modifiers = null;
    private JComponent _owner = null;
    
    public KeyboardShortcutDefinition(POActionListener action,
                                      String actionParameter,
                                      Keys key,
                                      KeyModifiers modifiers,
                                      JComponent owner)
    {
        super();
        _action = action;
        _actionParameter = actionParameter;
        _key = key;
        _modifiers = modifiers;
        _owner = owner;
    }
    public POActionListener getAction()
    {
        return _action;
    }
    public String getActionParameter()
    {
        return _actionParameter;
    }
    public Keys getKey()
    {
        return _key;
    }
    public KeyModifiers getModifiers()
    {
        return _modifiers;
    }
    public JComponent getOwner()
    {
        return _owner;
    }
}
