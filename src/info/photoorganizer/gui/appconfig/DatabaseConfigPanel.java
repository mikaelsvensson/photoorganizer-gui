package info.photoorganizer.gui.appconfig;

import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.util.config.ConfigurationProperty;

import java.awt.Component;
import java.net.URL;

import javax.swing.JTextField;


public class DatabaseConfigPanel extends POConfigPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JTextField databasePathField = GuiComponentFactory.createTextField(ConfigurationProperty.dbPath.get().toString());
    
    public DatabaseConfigPanel()
    {
        Component[] components = {
                GuiComponentFactory.createLabel(getI18nText("DATABASE_PATH")),
                databasePathField
        };
        GuiComponentFactory.initUserOptionsPanel(this, components);
    }

    @Override
    public void load()
    {
        databasePathField.setText(ConfigurationProperty.dbPath.get().toString());
    }

    @Override
    public void save() throws Exception
    {
        ConfigurationProperty.dbPath.set(new URL(databasePathField.getText()));
    }
}
