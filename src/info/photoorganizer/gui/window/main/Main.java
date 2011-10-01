package info.photoorganizer.gui.window.main;


import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.POAction;
import info.photoorganizer.gui.PhotoOrganizer;
import info.photoorganizer.gui.components.frame.POFrame;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.thumblist.MetadataLoader;
import info.photoorganizer.gui.components.view.POViewPanel;
import info.photoorganizer.gui.components.view.Position;
import info.photoorganizer.gui.components.view.TreeException;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.window.config.Config;
import info.photoorganizer.gui.window.config.ConfigPanel;
import info.photoorganizer.metadata.KeywordTagDefinition;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Main extends POFrame
{

    private static final String ACTIONNAME_SHOW_OPTIONS = "SHOW_OPTIONS";

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
        
        initActions();
        
        initMenu();
        
        initComponents(application);
    }

    private void initComponents(PhotoOrganizer application)
    {
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

    private void initActions()
    {
        getActionMap().put(ACTIONNAME_SHOW_OPTIONS, new POAction(getI18nText(ACTIONNAME_SHOW_OPTIONS))
        {
            
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                GuiComponentFactory.show(new Config(Main.this));
            }
        });
    }
    
    private void initMenu()
    {
        JMenuBar menu = new JMenuBar();
        
        JMenu fileMenu = new JMenu(getI18nText("MENU_FILE"));
        fileMenu.add(new JMenuItem(getActionMap().get(ACTIONNAME_SHOW_OPTIONS)));
        menu.add(fileMenu);
        
        setJMenuBar(menu);
    }

    public static void main(String[] args)
    {
        PhotoOrganizer.main(args);
    }

}
