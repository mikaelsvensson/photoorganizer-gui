package info.photoorganizer.gui.components.view;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.shared.CloseOperation;

public class POViewPanelTest extends PODialog
{
    public POViewPanelTest() throws TreeException
    {
        super("test", 500, 500, CloseOperation.DISPOSE_ON_CLOSE, createBorderLayoutPanel());

        POViewPaneInfo labelAlbert = new POViewPaneInfo(createLabel("Albert"), "A");
        POViewPaneInfo labelBertha = new POViewPaneInfo(createLabel("Bertha"), "B");
        POViewPaneInfo labelCeasar = new POViewPaneInfo(createLabel("Ceasar"), "C");
        POViewPaneInfo labelDaniel = new POViewPaneInfo(createLabel("Daniel"), "D");
        
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
        show(new POViewPanelTest());
    }
}
