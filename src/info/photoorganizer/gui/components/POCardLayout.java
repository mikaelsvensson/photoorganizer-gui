package info.photoorganizer.gui.components;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * @see http://tips4java.wordpress.com/2008/10/31/card-layout-focus/
 */
public class POCardLayout extends CardLayout implements AncestorListener
{
    
    public JComponent getCurrentCard()
    {
        return _currentCard;
    }

    private static final long serialVersionUID = 1L;
    
    private JComponent _currentCard = null;

    @Override
    public void addLayoutComponent(Component comp, Object constraints)
    {
        super.addLayoutComponent(comp, constraints);
        if (comp instanceof JComponent)
        {
            JComponent jComp = (JComponent) comp;
            jComp.addAncestorListener(this);
        }
        else
        {
            throw new IllegalArgumentException("Cannot add component of type " + comp.getClass().getSimpleName() + ". " + getClass().getSimpleName() + " only supports components extending JComponent.");
        }
    }

    @Override
    public void removeLayoutComponent(Component comp)
    {
        super.removeLayoutComponent(comp);
        if (comp instanceof JComponent)
        {
            JComponent jComp = (JComponent) comp;
            jComp.removeAncestorListener(this);
        }
    }

    @Override
    public void ancestorAdded(AncestorEvent event)
    {
        _currentCard = event.getComponent();
    }

    @Override
    public void ancestorMoved(AncestorEvent event)
    {
    }

    @Override
    public void ancestorRemoved(AncestorEvent event)
    {
    }

}
