package info.photoorganizer.gui.components.view;

import javax.swing.JLabel;

import org.junit.Test;

public class TreeNodeTest
{
    @Test
    public void manualTree() throws TreeException
    {
        TreeNode root = new TreeNode();
        JLabel labelAlbert = createLabel("Albert");
        JLabel labelBertha = createLabel("Bertha");
        JLabel labelCeasar = createLabel("Ceasar");
        JLabel labelDaniel = createLabel("Daniel");
        
        root.addComponent(labelAlbert, "A");
        root.addComponent(labelBertha, "B");
        
        root.split(true, labelCeasar, "C", Position.RIGHT_OR_BOTTOM_OR_SECOND);

        System.out.println(root);
        
        root.getSecond().split(false, labelDaniel, "D", Position.LEFT_OR_TOP_OR_FIRST);
        
        System.out.println(root);
        
        root.getSecond().getSecond().removeComponent(labelCeasar);
        
        System.out.println(root);
        
        root.getSecond().removeComponent(labelDaniel);
        
        System.out.println(root);
        
        root.removeComponent(labelAlbert);
        
        System.out.println(root);
    }
    
    private JLabel createLabel(String text)
    {
        return new JLabel(text);
    }
}
