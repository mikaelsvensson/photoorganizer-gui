package info.photoorganizer.gui.shared;

import info.photoorganizer.util.I18n;

import javax.swing.JPanel;

public class POPanel extends JPanel
{
    protected String getI18nText(Class<?> bundle, String key, Object... parameters)
    {
        return I18n.getInstance().getString(bundle, key, parameters);
    }
    
    protected String getI18nText(String key, Object... parameters)
    {
        return I18n.getInstance().getString(getClass(), key, parameters);
    }

}
