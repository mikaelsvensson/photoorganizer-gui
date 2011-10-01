package info.photoorganizer.gui.appconfig.autoindexing;

import info.photoorganizer.database.autoindexing.IndexingConfigurationInterface;
import info.photoorganizer.database.autoindexing.IndexingConfigurationList;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.appconfig.POConfigPanel;
import info.photoorganizer.gui.components.frame.POCloseReason;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.metadata.PhotoFileMetadataTag;
import info.photoorganizer.util.config.ConfigurationProperty;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class AutoIndexingConfigPanel extends POConfigPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JList indexingConfigurationList = null;
    private DefaultListModel listModel = new DefaultListModel();
    
    public AutoIndexingConfigPanel()
    {
        for (IndexingConfigurationInterface cfg : ConfigurationProperty.indexingConfigurationList.get())
        {
            listModel.addElement(cfg);
        }
        
        indexingConfigurationList = new JList(listModel);
        indexingConfigurationList.addMouseListener(new MouseListener()
        {
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
            }
            
            @Override
            public void mousePressed(MouseEvent e)
            {
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
            }
            
            @Override
            public void mouseEntered(MouseEvent e)
            {
            }
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    try
                    {
                        int index = indexingConfigurationList.getSelectedIndex();
                        EditIndexingConfigurationDialog dialog = new EditIndexingConfigurationDialog(getOwner(), (IndexingConfigurationInterface)indexingConfigurationList.getSelectedValue());
                        if (dialog.showModal() == POCloseReason.OK)
                        {
                            listModel.set(index, dialog.getConfiguration());
                        }
                    }
                    catch (CloneNotSupportedException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        
        Component[] components = {
                GuiComponentFactory.createLabel(getI18nText("INDEXING_CONFIGURATIONS")),
                indexingConfigurationList
        };
        GuiComponentFactory.initUserOptionsPanel(this, components);
    }

    @Override
    public void load()
    {
        //databasePathField.setText(ConfigurationProperty.dbPath.get().toString());
    }

    @Override
    public void save() throws Exception
    {
        Enumeration<IndexingConfigurationInterface> elements = (Enumeration<IndexingConfigurationInterface>) listModel.elements();
        IndexingConfigurationList list = new IndexingConfigurationList();
        while (elements.hasMoreElements())
        {
            list.add(elements.nextElement());
        }
        ConfigurationProperty.indexingConfigurationList.set(list);
        //ConfigurationProperty.dbPath.set(new URL(databasePathField.getText()));
        
    }
}
