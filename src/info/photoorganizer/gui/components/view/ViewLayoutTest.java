package info.photoorganizer.gui.components.view;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;

public class ViewLayoutTest extends PODialog
{
    public ViewLayoutTest(Database database)
    {
        super("test", CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createViewLayoutPanel(), database);
        //getContentPane().add(createLabel("Hej"));
        getContentPane().add(GuiComponentFactory.createLabel("Hej"), new Object());
    }
    
//    public static void main(String[] args)
//    {
//        GuiComponentFactory.show(new ViewLayoutTest());
//    }
}
