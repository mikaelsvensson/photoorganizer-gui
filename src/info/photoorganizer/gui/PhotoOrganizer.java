package info.photoorganizer.gui;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseManager;
import info.photoorganizer.database.autoindexing.DefaultIndexingConfiguration;
import info.photoorganizer.database.autoindexing.IndexingConfigurationList;
import info.photoorganizer.database.autoindexing.MetadataMappingConfiguration;
import info.photoorganizer.database.autoindexing.MetadataMappingConfigurationInterface;
import info.photoorganizer.gui.search.MatchProvider;
import info.photoorganizer.gui.search.Search;
import info.photoorganizer.gui.search.SearchListener;
import info.photoorganizer.gui.window.main.Main;
import info.photoorganizer.metadata.AutoIndexTagDefinition;
import info.photoorganizer.metadata.DefaultTagDefinition;
import info.photoorganizer.metadata.Photo;
import info.photoorganizer.metadata.PhotoFileMetadataTag;
import info.photoorganizer.util.Event;
import info.photoorganizer.util.Event.EventExecuter;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.config.ConfigurationProperty;
import info.photoorganizer.util.transform.ReplaceTransformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoOrganizer
{
    private static I18n I18N = I18n.getInstance();
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
//        GuiComponentFactory.initDefaultLookAndFeel();
        PhotoOrganizer application = new PhotoOrganizer();
        
        Database database = application.getDatabase();
        IndexingConfigurationList indexingCfgList = ConfigurationProperty.indexingConfigurationList.get();
        if (indexingCfgList.size() == 0 && null != database)
        {
            DefaultIndexingConfiguration configuration = new DefaultIndexingConfiguration();
            configuration.setFileFilter(new RejectedFileExtensionFileFilter());

            {
                MetadataMappingConfiguration mappingCfg = new MetadataMappingConfiguration(/*database*/);
                mappingCfg.setSource(PhotoFileMetadataTag.IPTC_SUPPLEMENTAL_CATEGORIES);
                mappingCfg.getSourceTextTransformers().add(new ReplaceTransformer(".", " "));
                mappingCfg.setTarget(database.getTagDefinition(DefaultTagDefinition.ROOT_KEYWORD.getId()));
                configuration.getMetadataMappers().add(mappingCfg);
            }
            
            for (AutoIndexTagDefinition aitd : AutoIndexTagDefinition.values())
            {
                configuration.getMetadataMappers().add(
                        new MetadataMappingConfiguration(
                                aitd.getFileTag(), 
                                database.getTagDefinition(aitd.getTargetTagDefinitionId())/*,
                                database*/));
            }
            indexingCfgList.add(configuration);
            
            ConfigurationProperty.indexingConfigurationList.set(indexingCfgList);

        }
        GuiComponentFactory.show(new Main(application), false);
    }
    
    private Search _currentSearch = null;
    
    private ExecutorService _executor = Executors.newCachedThreadPool();
    
    public void submitBackgroundTask(Runnable task)
    {
        _executor.submit(task);
    }

    private Database _database = null;
    
    private Event<PhotoSelectionEventListener, PhotoSelectionEvent> _photoSelectionEvent = new Event<PhotoSelectionEventListener, PhotoSelectionEvent>(
            new EventExecuter<PhotoSelectionEventListener, PhotoSelectionEvent>()
            {
                @Override
                public void fire(PhotoSelectionEventListener listener, PhotoSelectionEvent event)
                {
                    listener.photoSelectionChanged(event);
                }
            });
    
    private List<Photo> _selectedPhotos = new ArrayList<Photo>();
    
    public void addPhotoToSelection(Photo photo)
    {
        synchronized (_selectedPhotos)
        {
            _selectedPhotos.add(photo);
            _photoSelectionEvent.fire(new PhotoSelectionEvent(this, true, photo));
        }
    }
    
    public Database getDatabase()
    {
        if (null == _database)
        {
            _database = DatabaseManager.getInstance().openDatabase(ConfigurationProperty.dbPath.get());
        }
        return _database;
    }
    
    public List<Photo> getSelectedPhotos()
    {
        synchronized (_selectedPhotos)
        {
            return Collections.unmodifiableList(new ArrayList<Photo>(_selectedPhotos));
        }
    }

    public String getText(Class<?> bundle, String key, Object... parameters)
    {
        return I18N.getString(bundle, key, parameters);
    }
    
    public void removePhotoFromSelection(Photo photo)
    {
        synchronized (_selectedPhotos)
        {
            if (_selectedPhotos.remove(photo))
            {
                _photoSelectionEvent.fire(new PhotoSelectionEvent(this, false, photo));
            }
        }
    }
    
    public void setSelectedPhotos(List<Photo> newSelection)
    {
        synchronized (_selectedPhotos)
        {
            List<Photo> oldSelection = _selectedPhotos;
            
            List<Photo> added = new ArrayList<Photo>(newSelection);
            List<Photo> removed = new ArrayList<Photo>(oldSelection);
            added.removeAll(oldSelection);// Added photos: all photos that exist in newSelection and not in oldSelection
            removed.removeAll(newSelection); // Removed photos: all photos that exist in oldSelecton and not in newSelection.
            
            _selectedPhotos.clear();
            _selectedPhotos.addAll(newSelection);
            
            _photoSelectionEvent.fire(new PhotoSelectionEvent(this, added, removed));
        }
    }
    
    public void addPhotoSelectionListener(PhotoSelectionEventListener listener)
    {
        _photoSelectionEvent.addListener(listener);
    }

    public void removePhotoSelectionListener(PhotoSelectionEventListener listener)
    {
        _photoSelectionEvent.removeListener(listener);
    }

    private List<SearchListener> _pendingSearchListeners = new ArrayList<SearchListener>();
    
    public synchronized void startPhotoSearch(List<MatchProvider> prods)
    {
            Search newSearch = new Search(prods, null);
            if (null != _currentSearch)
            {
                for (SearchListener listener : _currentSearch.getListeners())
                {
                    newSearch.addSearchResultListener(listener);
                }
                
                _currentSearch.removeSearchResultListeners();
                _currentSearch.cancel(true);
            }
            else
            {
                for (SearchListener listener : _pendingSearchListeners)
                {
                    newSearch.addSearchResultListener(listener);
                }
                _pendingSearchListeners.clear();
            }
            
            _currentSearch = newSearch;
            
//            Thread searchThread = new Thread(_currentSearch);
//            searchThread.start();
            //_currentSearch.execute();
            submitBackgroundTask(_currentSearch);
    }
    
    public synchronized void addPhotoSearchResultListener(SearchListener listener)
    {
        if (null == _currentSearch)
        {
            _pendingSearchListeners.add(listener);
        }
        else
        {
            _currentSearch.addSearchResultListener(listener);
        }
    }

}
