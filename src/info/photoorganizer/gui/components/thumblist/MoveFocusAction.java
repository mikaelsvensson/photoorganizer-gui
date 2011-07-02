package info.photoorganizer.gui.components.thumblist;


import java.awt.event.ActionEvent;

public class MoveFocusAction extends POThumbListAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private GuiItemRelationship _direction = null;

    public MoveFocusAction(POThumbList component, GuiItemRelationship direction)
    {
        super(component);
        _direction = direction;
    }

    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        GuiItem guiItem = _component.getRelatedGuiItem(_component.getFocusedGuiItem(), _direction);
        if (null != guiItem)
        {
            _component.setFocusedGuiItem(guiItem);
        }
    }

}
