package info.photoorganizer.gui.components.view;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.thumblist.POThumbList;
import info.photoorganizer.gui.components.tree.POFolderTree;
import info.photoorganizer.gui.components.tree.POKeywordTree;
import info.photoorganizer.gui.components.tree.POTreeSelectionEvent;
import info.photoorganizer.gui.components.tree.POTreeSelectionListener;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.metadata.TagDefinition;
import info.photoorganizer.util.StringUtils;
import info.photoorganizer.util.WordInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class POViewPanelTest2 extends PODialog
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public POViewPanelTest2() throws TreeException
    {
        super("TITLE", 500, 500, CloseOperation.DISPOSE_ON_CLOSE, createBorderLayoutPanel());

        POFolderTree folderTree = new POFolderTree();
        final POThumbList thumbList = new POThumbList();
        folderTree.addSelectionListener(new POTreeSelectionListener<File>()
        {
            
            @Override
            public void selectionChanged(POTreeSelectionEvent<File> event)
            {
                thumbList.setItems(POThumbList.getFileList(event.getSelection()));
            }
        });
        
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
        view.add(treeKeywords, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        view.add(images, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        
        getContentPane().add(view, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) throws TreeException
    {
        initDefaultLookAndFeel();
        show(new POViewPanelTest2());
    }
}
