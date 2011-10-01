package info.photoorganizer.gui.mainwindow;

import info.photoorganizer.gui.PhotoOrganizer;
import info.photoorganizer.gui.PhotoSelectionEvent;
import info.photoorganizer.gui.PhotoSelectionEventListener;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.metadata.Photo;
import info.photoorganizer.metadata.Tag;
import info.photoorganizer.metadata.TagDefinition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;

public class SelectionInfoPanel extends DockablePanel<JLabel>
{

    public SelectionInfoPanel(PhotoOrganizer application)
    {
        super(application);
    }
    
    private JLabel label = null;

    @Override
    protected JLabel createComponent()
    {
        label = new JLabel();
        _application.addPhotoSelectionListener(new PhotoSelectionEventListener()
        {
            
            @Override
            public void photoSelectionChanged(PhotoSelectionEvent event)
            {
                Map<KeywordTagDefinition, Integer> counters = new HashMap<KeywordTagDefinition, Integer>();
                String text = _application.getSelectedPhotos().size() + " photos selected. ";
                for (Photo photo : _application.getSelectedPhotos())
                {
                    Iterator<Tag<? extends TagDefinition>> i = photo.getTags();
                    while (i.hasNext())
                    {
                        TagDefinition tagDef = i.next().getDefinition();
                        if (tagDef instanceof KeywordTagDefinition)
                        {
                            KeywordTagDefinition keywordTagDef = (KeywordTagDefinition)tagDef;
                            Integer old = counters.get(keywordTagDef);
                            counters.put(keywordTagDef, old != null ? old+1 : 1);
                        }
                    }
                }
                for (Entry<KeywordTagDefinition, Integer> entry : counters.entrySet())
                {
                    text += "<br>" + entry.getKey().getName() + ": " + entry.getValue() + " photo(s). ";
                }
                label.setText("<html>" + text + "</html>");
            }
        });
        return label;
    }

}
