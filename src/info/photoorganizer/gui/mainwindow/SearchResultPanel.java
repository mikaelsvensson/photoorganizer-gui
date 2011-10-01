package info.photoorganizer.gui.mainwindow;

import info.photoorganizer.gui.PhotoOrganizer;
import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;
import info.photoorganizer.gui.components.thumblist.POThumbList;
import info.photoorganizer.gui.components.thumblist.SelectionEvent;
import info.photoorganizer.gui.components.thumblist.SelectionEventListener;
import info.photoorganizer.gui.search.Match;
import info.photoorganizer.gui.search.SearchListener;
import info.photoorganizer.gui.search.SearchResultEvent;
import info.photoorganizer.metadata.Photo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SearchResultPanel extends DockablePanel<JPanel>
{

    private static final Logger L = info.photoorganizer.util.Log.getLogger(SearchResultPanel.class);
    
    public SearchResultPanel(PhotoOrganizer application)
    {
        super(application);
    }
    
    private JPanel p = new JPanel(new BorderLayout());
    
    private POThumbList thumbList = new POThumbList();

    @Override
    protected JPanel createComponent()
    {
        thumbList.setMetadataLoader(new POMetadataLoader(getDatabase())/* new MetadataLoader()
        {
            @Override
            public Map<Object, Object> getMetadata(File file)
            {
                Map<Object, Object> res = new HashMap<Object, Object>();
                TagDefinition fNumber = getDatabase().getTagDefinition(DefaultTagDefinition.F_NUMBER.getId());
                TagDefinition dateTaken = getDatabase().getTagDefinition(DefaultTagDefinition.DATE_TAKEN.getId());
                Photo photo = getDatabase().getPhoto(file);
                if (null != photo)
                {
                    Iterator<Tag<? extends TagDefinition>> tags = photo.getTags();
                    while (tags.hasNext())
                    {
                        Tag<? extends TagDefinition> tag = tags.next();
                        if (tag instanceof ValueTag)
                        {
                            if (tag.getDefinition().equals(fNumber) || tag.getDefinition().equals(dateTaken))
                            {
                                res.put(tag.getDefinition(), ((ValueTag)tag).getValue());
                            }
                        }
                    }
                }
                return res;
            }
        }*/);
        
        thumbList.setImageLoader(new POImageLoader(getDatabase()));
        
        thumbList.addSelectionListener(new SelectionEventListener()
        {
            
            @Override
            public void selectionChanged(SelectionEvent event)
            {
                POThumbList source = event.getSource();
                List<Photo> _selectedPhotos = new ArrayList<Photo>();
                for (File f : event.getSource().getSelectedItems())
                {
                    _selectedPhotos.add(getDatabase().getPhoto(f));
                }
                _application.setSelectedPhotos(_selectedPhotos);
            }
        });
        
        _application.addPhotoSearchResultListener(new SearchListener()
        {

            @Override
            public void searchResultFound(SearchResultEvent event)
            {
                for (Match match : event.getNewMatches())
                {
                    L.info("Found " + match.getPhoto().getFile().getName());
                    thumbList.addItem(match.getPhoto().getFile());
                }
            }

            @Override
            public void searchStarted(SearchResultEvent event)
            {
                thumbList.clearItems();
            }
        });
        
        p.add(new JScrollPane(thumbList), BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(300, 200));
        return p;
    }
}
