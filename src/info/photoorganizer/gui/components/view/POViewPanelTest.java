package info.photoorganizer.gui.components.view;

import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;

import java.awt.BorderLayout;

public class POViewPanelTest extends PODialog
{
    public POViewPanelTest() throws TreeException
    {
        super("test", 500, 500, CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBorderLayoutPanel());

        POViewPaneInfo labelAlbert = new POViewPaneInfo(GuiComponentFactory.createLabel("Albert"), "A");
        POViewPaneInfo labelBertha = new POViewPaneInfo(GuiComponentFactory.createLabel("Bertha"), "B");
        POViewPaneInfo labelCeasar = new POViewPaneInfo(GuiComponentFactory.createLabel("Ceasar"), "C");
        POViewPaneInfo labelDaniel = new POViewPaneInfo(GuiComponentFactory.createLabel("Daniel"), "D");
        
        POViewPanel view = new POViewPanel();
        
        view.split(null, false);
        view.split(new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND }, true);
        
        view.add(labelAlbert, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(labelBertha, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(labelCeasar, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(labelDaniel, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        
        getContentPane().add(view, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) throws TreeException
    {
        GuiComponentFactory.show(new POViewPanelTest());
    }
}
