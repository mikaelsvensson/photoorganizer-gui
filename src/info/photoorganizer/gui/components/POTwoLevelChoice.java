package info.photoorganizer.gui.components;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class POTwoLevelChoice<T extends Object, E extends JComponent> extends JPanel
{
    private LinkedHashMap<T, E> _options = null;

    public POTwoLevelChoice(LinkedHashMap<T, E> options)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        _options = options;
        
        initComponents();
    }
    
    private JComboBox _primaryOption = null;
    private JPanel _secondaryOptionsPanel = null;
    private CardLayout _secondaryOptionsPanelLayout = null;
    
    private void initComponents()
    {
        DefaultComboBoxModel primaryOptionsModel = new DefaultComboBoxModel();
        
        _primaryOption = new JComboBox(primaryOptionsModel);
        _secondaryOptionsPanelLayout = new CardLayout();
        _secondaryOptionsPanel = new JPanel(_secondaryOptionsPanelLayout);
        
        add(_primaryOption);
        add(_secondaryOptionsPanel);
        
        _primaryOption.addItemListener(new ItemListener()
        {

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                String label = e.getItem().toString();
                _secondaryOptionsPanelLayout.show(_secondaryOptionsPanel, label);
            }
            
        });
        
        for (Entry<T, E> entry : _options.entrySet())
        {
            primaryOptionsModel.addElement(entry.getKey());
            _secondaryOptionsPanel.add(entry.getValue(), entry.getKey().toString());
        }
        setSelected(_options.keySet().iterator().next());
    }

    public void setSelected(T choiceName)
    {
        _primaryOption.setSelectedItem(choiceName);
    }
    
    public T getSelectedChoiceName()
    {
        return (T) _primaryOption.getSelectedItem();
    }
    
    public E getSelectedChoiceValue()
    {
        return _options.get(_primaryOption.getSelectedItem());
    }
}
