package info.photoorganizer.gui.shared;

import javax.swing.AbstractAction;

import com.sun.corba.se.impl.logging.POASystemException;

public abstract class POAction extends AbstractAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public POAction()
    {
        super();
    }
    
    public POAction(String name)
    {
        setName(name);
    }

    public void setName(String name)
    {
        putValue(NAME, name);
    }
    
    public void setDescription(String description)
    {
        putValue(SHORT_DESCRIPTION, description);
    }
    
    public void setHelpText(String helpText)
    {
        putValue(LONG_DESCRIPTION, helpText);
    }
    
    public String getHelpText()
    {
        return (String) getValue(LONG_DESCRIPTION);
    }
    
    public String getName()
    {
        return (String) getValue(NAME);
    }

    public String getDescription()
    {
        return (String) getValue(SHORT_DESCRIPTION);
    }
}
