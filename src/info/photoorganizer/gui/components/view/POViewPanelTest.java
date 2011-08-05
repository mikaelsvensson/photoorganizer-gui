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

        POViewPane labelAlbert = new POViewPaneInfo(GuiComponentFactory.createLabel("Albert"), "A");
        POViewPane labelBertha = new POViewPaneInfo(GuiComponentFactory.createLabel("Bertha"), "B");
        POViewPane labelCeasar = new POViewPaneInfo(GuiComponentFactory.createLabel("Ceasar"), "C");
        POViewPane labelDaniel = new POViewPaneInfo(GuiComponentFactory.createLabel("Daniel"), "D");
        
        POViewPanel view = new POViewPanel();
        
        view.split(null, false);
        view.split(new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND }, true);
        
        view.add(labelAlbert.getComponent(), labelAlbert.getLabel(), new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(labelBertha.getComponent(), labelBertha.getLabel(), new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(labelCeasar.getComponent(), labelCeasar.getLabel(), new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(labelDaniel.getComponent(), labelDaniel.getLabel(), new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        
        getContentPane().add(view, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) throws TreeException
    {
        GuiComponentFactory.show(new POViewPanelTest());
    }
}
