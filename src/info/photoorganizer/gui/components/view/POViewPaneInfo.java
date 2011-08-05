package info.photoorganizer.gui.components.view;

import java.awt.Component;

public class POViewPaneInfo implements POViewPane
{
    private Component _component = null;
    private String _label = null;

    public POViewPaneInfo(Component component, String label)
    {
        super();
        _component = component;
        _label = label;
    }

    @Override
    public Component getComponent()
    {
        return _component;
    }

    @Override
    public String getLabel()
    {
        return _label;
    }

    public void setComponent(Component component)
    {
        _component = component;
    }

    public void setLabel(String label)
    {
        _label = label;
    }

    @Override
    public String toString()
    {
        return _label;
    }
}
