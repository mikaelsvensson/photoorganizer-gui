package info.photoorganizer.gui.shared;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public abstract class POSimpleDocumentListener implements DocumentListener
{

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        update(e);
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        update(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        update(e);
    }
    
    private void update(DocumentEvent e)
    {
        try
        {
            update(e, e.getDocument().getText(0, e.getDocument().getLength()));
        }
        catch (BadLocationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    
    public abstract void update(DocumentEvent e, String text);

}
