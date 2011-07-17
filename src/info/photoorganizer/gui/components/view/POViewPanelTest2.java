package info.photoorganizer.gui.components.view;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.thumblist.MetadataLoader;
import info.photoorganizer.gui.components.thumblist.POThumbList;
import info.photoorganizer.gui.components.tree.POFolderTree;
import info.photoorganizer.gui.components.tree.POKeywordTree;
import info.photoorganizer.gui.components.tree.POTreeSelectionEvent;
import info.photoorganizer.gui.components.tree.POTreeSelectionListener;
import info.photoorganizer.gui.search.FolderContentMatchProvider;
import info.photoorganizer.gui.search.Match;
import info.photoorganizer.gui.search.MatchProvider;
import info.photoorganizer.gui.search.SearchResultEvent;
import info.photoorganizer.gui.search.SearchResultListener;
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

public class POViewPanelTest2 extends PODialog
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private info.photoorganizer.gui.search.Search _currentSearch = null;
    
    private final POThumbList thumbList = new POThumbList();
    
    public POViewPanelTest2() throws TreeException
    {
        super("TITLE", 800, 800, CloseOperation.DISPOSE_ON_CLOSE, createBorderLayoutPanel());
        
        thumbList.setMetadataLoader(new MetadataLoader()
        {
            @Override
            public Map<Object, Object> getMetadata(File file)
            {
                Map<Object, Object> res = new HashMap<Object, Object>();
                TagDefinition fNumber = getDatabase().getTagDefinition(DefaultTagDefinition.F_NUMBER.getId());
                Photo photo = getDatabase().getPhoto(file);
                if (null != photo)
                {
                    Iterator<Tag<? extends TagDefinition>> tags = photo.getTags();
                    while (tags.hasNext())
                    {
                        Tag<? extends TagDefinition> tag = tags.next();
                        if (tag instanceof ValueTag && tag.getDefinition().equals(fNumber))
                        {
                            res.put(fNumber, ((ValueTag)tag).getValue());
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
                int i=0;
                
                for (File folder : event.getSelection())
                {
                    prods.add(new FolderContentMatchProvider(folder, getDatabase()));
                }
                System.out.println(prods);
                thumbList.clearItems();
                if (null != _currentSearch)
                {
                    _currentSearch.cancel(true);
//                    System.err.println("--------------------------------------------------------------Cancelling " + _currentSearch);
                }
                _currentSearch = new info.photoorganizer.gui.search.Search(prods, null);
                _currentSearch.addSearchResultListener(new SearchResultListener()
                {
                    
                    @Override
                    public void itemsAdded(SearchResultEvent event)
                    {
                        for (Match match : event.getNewMatches())
                        {
                            System.out.println("Found " + match.getPhoto().getFile().getName());
                            thumbList.addItem(match.getPhoto().getFile());
                        }
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
        
//        POViewPaneInfo labelAlbert = new POViewPaneInfo(createLabel("Albert"), "A");
        POViewPaneInfo treeFolders = new POViewPaneInfo(folderTree, "Folders");
//        POViewPaneInfo labelCeasar = new POViewPaneInfo(createLabel("Ceasar"), "C");
        
        POKeywordTree keywordTree = new POKeywordTree(getDatabase());
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
        
        POViewPaneInfo treeKeywords = new POViewPaneInfo(keywordTree, "Keywords");
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(thumbList), BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(300, 200));
        POViewPaneInfo images = new POViewPaneInfo(p, "Images");
        final POTagField<KeywordTagDefinition> tagField = createTagField(new KeywordTagDefinition[] {}, 20);
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
        POViewPaneInfo keywordsField = new POViewPaneInfo(tagField, "Keyword");
        
        POViewPanel view = new POViewPanel();
        
        view.split(null, false);
        view.split(new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND }, true);
        
        view.add(treeFolders, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
//        view.add(labelAlbert, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(keywordsField, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.LEFT_OR_TOP_OR_FIRST } );
        
        view.add(images, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        view.add(treeKeywords, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        
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
    
    public static void main(String[] args) throws TreeException
    {
        initDefaultLookAndFeel();
        show(new POViewPanelTest2());
    }
}
