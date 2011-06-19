package info.photoorganizer.gui.components.view;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.tree.POKeywordTree;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.metadata.TagDefinition;
import info.photoorganizer.util.WordInfo;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class POViewPanelTest2 extends PODialog
{
    public POViewPanelTest2() throws TreeException
    {
        super("TITLE", 500, 500, CloseOperation.DISPOSE_ON_CLOSE, createBorderLayoutPanel());

        POViewPaneInfo labelAlbert = new POViewPaneInfo(createLabel("Albert"), "A");
        POViewPaneInfo labelBertha = new POViewPaneInfo(createLabel("Bertha"), "B");
        POViewPaneInfo labelCeasar = new POViewPaneInfo(createLabel("Ceasar"), "C");
        POViewPaneInfo treeKeywords = new POViewPaneInfo(new POKeywordTree(getDatabase()), "Keywords");
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
        
        view.add(labelAlbert, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(labelBertha, new Position[] { Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(keywordsField, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.LEFT_OR_TOP_OR_FIRST } );
        view.add(treeKeywords, new Position[] { Position.RIGHT_OR_BOTTOM_OR_SECOND, Position.RIGHT_OR_BOTTOM_OR_SECOND } );
        
        getContentPane().add(view, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) throws TreeException
    {
        initDefaultLookAndFeel();
        show(new POViewPanelTest2());
    }
}
