package info.photoorganizer.gui.components.view;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.frame.POFrame;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.thumblist.MetadataLoader;
import info.photoorganizer.gui.components.thumblist.POThumbList;
import info.photoorganizer.gui.components.tree.POFolderTree;
import info.photoorganizer.gui.components.tree.POKeywordTreePanel;
import info.photoorganizer.gui.components.tree.POTreeSelectionEvent;
import info.photoorganizer.gui.components.tree.POTreeSelectionListener;
import info.photoorganizer.gui.search.FolderContentMatchProvider;
import info.photoorganizer.gui.search.Match;
import info.photoorganizer.gui.search.MatchProvider;
import info.photoorganizer.gui.search.SearchListener;
import info.photoorganizer.gui.search.SearchResultEvent;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.metadata.DefaultTagDefinition;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.metadata.Photo;
import info.photoorganizer.metadata.Tag;
import info.photoorganizer.metadata.TagDefinition;
import info.photoorganizer.metadata.ValueTag;
import info.photoorganizer.util.StringUtils;
import info.photoorganizer.util.WordInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class POViewPanelTest2 extends POFrame
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private info.photoorganizer.gui.search.Search _currentSearch = null;
    
    private final POThumbList thumbList = new POThumbList();
    
    public POViewPanelTest2(Database database) throws TreeException
    {
        super("TITLE", 800, 800, CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBorderLayoutPanel(), database);
        
        setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        
        thumbList.setMetadataLoader(new MetadataLoader()
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
        });
        
        POFolderTree folderTree = new POFolderTree();
        folderTree.addSelectionListener(new POTreeSelectionListener<File>()
        {
            
            @Override
            public void selectionChanged(POTreeSelectionEvent<File> event)
            {
                List<MatchProvider> prods = new ArrayList<MatchProvider>();
                
                for (File folder : event.getSelection())
                {
                    prods.add(new FolderContentMatchProvider(folder, getDatabase()));
                }
                System.out.println(prods);
//                thumbList.clearItems();
                if (null != _currentSearch)
                {
                    _currentSearch.cancel(true);
//                    System.err.println("--------------------------------------------------------------Cancelling " + _currentSearch);
                }
                _currentSearch = new info.photoorganizer.gui.search.Search(prods, null);
                _currentSearch.addSearchResultListener(new SearchListener()
                {
                    
                    @Override
                    public void searchResultFound(SearchResultEvent event)
                    {
                        for (Match match : event.getNewMatches())
                        {
                            System.out.println("Found " + match.getPhoto().getFile().getName());
                            thumbList.addItem(match.getPhoto().getFile());
                        }
                    }

                    @Override
                    public void searchStarted(SearchResultEvent event)
                    {
                        thumbList.clearItems();
                    }
                });
                
                Thread searchThread = new Thread(_currentSearch);
                searchThread.start();
            }
        });
//        folderTree.addSelectionListener(new POTreeSelectionListener<File>()
//                {
//            
//            @Override
//            public void selectionChanged(POTreeSelectionEvent<File> event)
//            {
//                ResultItemProducer[] prods = new ResultItemProducer[/*1 + */event.getSelection().size()];
//                int i=0;
//                
//                for (File folder : event.getSelection())
//                {
//                    prods[i++] = new FolderContentsImageProducer(folder, false);
//                }
//                System.out.println(prods);
//                
//                if (null != _currentSearch)
//                {
//                    _currentSearch.cancel();
//                }
//                _currentSearch = new Search(null, prods);
//                _currentSearch.addSearchResultListener(new SearchResultListener()
//                {
//                    
//                    @Override
//                    public void itemsAdded(SearchResultEvent event)
//                    {
//                        List<ListItem> items = new ArrayList<ListItem>();
//                        for (ResultItem resItem : _currentSearch.getResult())
//                        {
//                            items.add(new PhotoListItem(resItem, _indexer, getDatabase()));
//                        }
//                        thumbList.setItems(items);
//                    }
//                });
//                
//                Thread searchThread = new Thread(_currentSearch);
//                searchThread.start();
//            }
//                });
        folderTree.setPreferredSize(new Dimension(200, 100));
        
        
        POKeywordTreePanel keywordTree = new POKeywordTreePanel(getDatabase(), true);
        keywordTree.addSelectionListener(new POTreeSelectionListener<KeywordTagDefinition>()
        {
            @Override
            public void selectionChanged(POTreeSelectionEvent<KeywordTagDefinition> event)
            {
                System.out.println("User has selected these keywords: " + event.getSelection().toString());
                for (KeywordTagDefinition keyword : event.getSelection())
                {
                    System.out.println("Synonyms for " + keyword.getName() + ": " + StringUtils.join(keyword.getSynonyms(), String.valueOf(KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR), true, KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK));
                }
            }
        });
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(thumbList), BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(300, 200));
        final POTagField<KeywordTagDefinition> tagField = GuiComponentFactory.createTagField(new KeywordTagDefinition[] {}, 20, getKeywordWordprovider());
        tagField.addFocusListener(new FocusListener()
        {
            
            @Override
            public void focusLost(FocusEvent e)
            {
                if (tagField.isFocusLost(e))
                {
                    Database database = getDatabase();
                    for (WordInfo info : tagField.getWords())
                    {
                        String word = info.getWord();
                        TagDefinition definition = database.getTagDefinition(word);
                        if (null == definition)
                        {
                            KeywordTagDefinition newKeywordTag = database.addRootKeyword(word);
                            try
                            {
                                newKeywordTag.store();
                            }
                            catch (DatabaseStorageException e1)
                            {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
                // TODO Auto-generated method stub
                
            }
        });
        
        POViewPanel view = new POViewPanel();
        
        view.split(null, false);
        view.split(new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND }, true);
        
        view.add(folderTree, "Folders", new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
//        view.add(labelAlbert, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(tagField, "Keyword", new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.LEFT_OR_TOP_OR_FIRST } );
        
        view.add(p, "Images", new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        view.add(keywordTree, "Keywords", new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        
        getContentPane().add(view, BorderLayout.CENTER);
        
        addWindowListener(new WindowListener()
        {
            
            @Override
            public void windowOpened(WindowEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowIconified(WindowEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowDeiconified(WindowEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowDeactivated(WindowEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (_currentSearch != null)
                {
                    _currentSearch.cancel(true);
                }
            }
            
            @Override
            public void windowClosed(WindowEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowActivated(WindowEvent e)
            {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
//    public static void main(String[] args) throws TreeException
//    {
////        imx.loggui.LogMaster.startLogGui();
//        GuiComponentFactory.initDefaultLookAndFeel();
//        GuiComponentFactory.show(new POViewPanelTest2());
//    }
}
