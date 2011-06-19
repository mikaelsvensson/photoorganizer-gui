package info.photoorganizer.gui.components.view;

import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;

public class ViewLayoutTest extends PODialog
{
    public ViewLayoutTest()
    {
        super("test", CloseOperation.DISPOSE_ON_CLOSE, createViewLayoutPanel());
        //getContentPane().add(createLabel("Hej"));
        getContentPane().add(createLabel("Hej"), new Object());
    }
    
    public static void main(String[] args)
    {
        show(new ViewLayoutTest());
    }
}
