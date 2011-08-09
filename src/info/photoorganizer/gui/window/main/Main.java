package info.photoorganizer.gui.window.main;


import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.PhotoOrganizer;
import info.photoorganizer.gui.components.frame.POFrame;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.thumblist.MetadataLoader;
import info.photoorganizer.gui.components.thumblist.POThumbList;
import info.photoorganizer.gui.components.tree.POFolderTree;
import info.photoorganizer.gui.components.tree.POKeywordTreePanel;
import info.photoorganizer.gui.components.tree.POTreeSelectionEvent;
import info.photoorganizer.gui.components.tree.POTreeSelectionListener;
import info.photoorganizer.gui.components.view.POViewPanel;
import info.photoorganizer.gui.components.view.Position;
import info.photoorganizer.gui.components.view.TreeException;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.StringUtils;

import java.awt.BorderLayout;

public class Main extends POFrame
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private POTagField<KeywordTagDefinition> _componentTagField = null;

    private MetadataLoader _metadataLoader = null;
    
    public synchronized MetadataLoader getMetadataLoader()
    {
        if (null == _metadataLoader)
        {
            _metadataLoader = new POMetadataLoader(getDatabase());
        }
        return _metadataLoader;
    }

    public synchronized POTagField<KeywordTagDefinition> getComponentTagField()
    {
        if (null == _componentTagField)
        {
            _componentTagField = GuiComponentFactory.createTagField(new KeywordTagDefinition[] {}, 20, getKeywordWordprovider());
        }
        return _componentTagField;
    }

    public Main(PhotoOrganizer application)
    {
        super("TITLE", 1000, 800, CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBorderLayoutPanel());
        
        PhotoSourcePanel photoSourcePanel = new PhotoSourcePanel(application);
        SearchResultPanel searchResultPanel = new SearchResultPanel(application);
        SelectionInfoPanel selectionInfoPanel = new SelectionInfoPanel(application);
        
        POViewPanel view = new POViewPanel();
        
        try
        {
            view.split(null, false);
            view.split(new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND }, true);
        }
        catch (TreeException e)
        {
            e.printStackTrace();
        }
        
        view.add(photoSourcePanel, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(selectionInfoPanel, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        view.add(searchResultPanel, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.LEFT_OR_TOP_OR_FIRST } );

        getContentPane().add(view, BorderLayout.CENTER);

    }
    
    public static void main(String[] args)
    {
        PhotoOrganizer.main(args);
    }

}
