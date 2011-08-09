package info.photoorganizer.gui.window.main;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.PhotoOrganizer;
import info.photoorganizer.gui.components.view.POViewPane;
import info.photoorganizer.metadata.Photo;

import java.awt.Component;
import java.util.List;

public abstract class DockablePanel<T extends Component> implements POViewPane
{
    public String getText(Class<?> bundle, String key, Object... parameters)
    {
        return _application.getText(bundle, key, parameters);
    }
    
    public String getText(String key, Object... parameters)
    {
        return _application.getText(getClass(), key, parameters);
    }

    protected PhotoOrganizer _application = null;
    private T _component = null;
    
    protected abstract T createComponent();

    public DockablePanel(PhotoOrganizer application)
    {
        super();
        _application = application;
    }

    public List<Photo> getSelectedPhotos()
    {
        return _application.getSelectedPhotos();
    }

    public Database getDatabase()
    {
        return _application.getDatabase();
    }

    @Override
    public T getComponent()
    {
        if (null == _component)
        {
            _component = createComponent();
        }
        return _component;
    }

    @Override
    public String getLabel()
    {
        return _application.getText(getClass(), "DOCKABLE_PANEL_TITLE");
    }

}
