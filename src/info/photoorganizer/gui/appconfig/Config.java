package info.photoorganizer.gui.appconfig;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.appconfig.autoindexing.AutoIndexingConfigPanel;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.frame.POFrame;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.POActionListener;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Config extends PODialog
{
    POConfigPanel[] _panels = null;
    
    public Config(POFrame owner, Database database)
    {
        this(owner, database, ConfigPanel.values());
    }

    public Config(POFrame owner, Database database, ConfigPanel... panels)
    {
        super(owner, "TITLE", CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBorderLayoutPanel(), database);
        
        initComponents(panels);
    }

    private void initComponents(ConfigPanel[] panels)
    {
        final CardLayout cardLayout = new CardLayout();
        final JPanel cards = new JPanel(cardLayout);
        
        POConfigPanel initialPanel = null;
        if (panels.length > 1)
        {
            _panels = new POConfigPanel[panels.length];
            int i=0;
            for (ConfigPanel configPanel : panels)
            {
                _panels[i] = createConfigPanel(panels[i++]);
            }
            
            initialPanel = _panels[0];
            
            final JList panelChoices = new JList(_panels);
            panelChoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            panelChoices.setSelectedIndex(0);
            panelChoices.addListSelectionListener(new ListSelectionListener()
            {
                
                @Override
                public void valueChanged(ListSelectionEvent e)
                {
                    if (!e.getValueIsAdjusting())
                    {
                        Object object = ((JList)e.getSource()).getModel().getElementAt(panelChoices.getSelectedIndex());
                        POConfigPanel panel = (POConfigPanel) object;
                        
                        cardLayout.show(cards, panel.toString());
                    }
                }
            });
            getContentPane().add(new JScrollPane(panelChoices), BorderLayout.WEST);
        }
        else if (panels.length == 1)
        {
            _panels = new POConfigPanel[] { createConfigPanel(panels[0]) };
            
            initialPanel = _panels[0];
        }
        
        for (POConfigPanel card : _panels)
        {
            cards.add(new JScrollPane(card), card.toString());
        }
        cardLayout.first(cards);
        
        getContentPane().add(cards, BorderLayout.CENTER);
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonsPanel()
    {
        return GuiComponentFactory.createFlowLayoutPanel(FlowLayoutAlignment.RIGHT, 
                createOkButton(),
                createCancelButton()
                );
    }

    private JButton createCancelButton()
    {
        return GuiComponentFactory.createButton(getI18nText("CANCEL"), new POActionListener()
        {
            @Override
            public void actionPerformedImpl(ActionEvent event)
            {
                dispose();
            }
        });
    }

    private JButton createOkButton()
    {
        return GuiComponentFactory.createButton(getI18nText("OK"), new POActionListener()
        {
            @Override
            public void actionPerformedImpl(ActionEvent event)
            {
                boolean close = true;
                for (POConfigPanel configPanel : _panels)
                {
                    try
                    {
                        configPanel.save();
                    }
                    catch (Exception e)
                    {
                        int response = JOptionPane.showConfirmDialog(Config.this, getI18nText("COULD_NOT_SAVE"), getI18nText("COULD_NOT_SAVE"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (response == JOptionPane.CANCEL_OPTION)
                        {
                            return;
                        }
                    }
                }
                if (close)
                {
                    dispose();
                }
            }
        });
    }
    
    private POConfigPanel createConfigPanel(ConfigPanel p)
    {
        switch (p)
        {
        case AUTOMATIC_INDEXING:
            return new AutoIndexingConfigPanel(getDatabase());
        case DATABASE:
            return new DatabaseConfigPanel(); 
        }
        return null;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
