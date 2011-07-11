package info.photoorganizer.gui.components.view;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.thumblist.ListItem;
import info.photoorganizer.gui.components.thumblist.POThumbList;
import info.photoorganizer.gui.components.tree.POFolderTree;
import info.photoorganizer.gui.components.tree.POKeywordTree;
import info.photoorganizer.gui.components.tree.POTreeSelectionEvent;
import info.photoorganizer.gui.components.tree.POTreeSelectionListener;
import info.photoorganizer.gui.search.EntireDatabaseImageProducer;
import info.photoorganizer.gui.search.FolderContentsImageProducer;
import info.photoorganizer.gui.search.Indexer;
import info.photoorganizer.gui.search.IndexerEvent;
import info.photoorganizer.gui.search.IndexerEventListener;
import info.photoorganizer.gui.search.PhotoIndexer;
import info.photoorganizer.gui.search.PhotoListItem;
import info.photoorganizer.gui.search.ResultItem;
import info.photoorganizer.gui.search.ResultItemProducer;
import info.photoorganizer.gui.search.Search;
import info.photoorganizer.gui.search.SearchResultEvent;
import info.photoorganizer.gui.search.SearchResultListener;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.metadata.TagDefinition;
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
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class POViewPanelTest2 extends PODialog
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Search _currentSearch = null;
    
    private Indexer _indexerWorker = null;
    private PhotoIndexer _indexer = null;

    private final POThumbList thumbList = new POThumbList();
    
    public POViewPanelTest2() throws TreeException
    {
        super("TITLE", 800, 800, CloseOperation.DISPOSE_ON_CLOSE, createBorderLayoutPanel());
        
        _indexerWorker = new Indexer(getDatabase());
        _indexerWorker.addIndexerEventListener(new IndexerEventListener()
        {
            @Override
            public void fileIndexed(IndexerEvent event)
            {
                for (File indexedFile : event.getIndexedFiles())
                {
                    thumbList.repaint(indexedFile);
                }
            }
        });
        //_indexerWorker.execute();
        
        _indexer = new PhotoIndexer(getDatabase());
        (new Thread(_indexer)).start();

        POFolderTree folderTree = new POFolderTree();
        folderTree.addSelectionListener(new POTreeSelectionListener<File>()
        {
            
            @Override
            public void selectionChanged(POTreeSelectionEvent<File> event)
            {
                ResultItemProducer[] prods = new ResultItemProducer[/*1 + */event.getSelection().size()];
                int i=0;
                
//                prods[i++] = new EntireDatabaseImageProducer(getDatabase());
                
                for (File folder : event.getSelection())
                {
                    prods[i++] = new FolderContentsImageProducer(folder, false);
                }
                System.out.println(prods);
                
                if (null != _currentSearch)
                {
                    _currentSearch.cancel();
                }
                _currentSearch = new Search(null, prods);
                _currentSearch.addSearchResultListener(new SearchResultListener()
                {
                    
                    @Override
                    public void itemsAdded(SearchResultEvent event)
                    {
                        List<ListItem> items = new ArrayList<ListItem>();
                        for (ResultItem resItem : _currentSearch.getResult())
                        {
                            //items.add(new PhotoListItem(resItem, _indexerWorker));
                            items.add(new PhotoListItem(resItem, _indexer, getDatabase()));
                        }
                        thumbList.setItems(items);
                    }
                });
                
                Thread searchThread = new Thread(_currentSearch);
                searchThread.start();
                
                //thumbList.setItems(POThumbList.getFileList(event.getSelection()));
            }
        });
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
                    _currentSearch.cancel();
                }
                if (_indexer != null)
                {
                    _indexer.cancel();
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
