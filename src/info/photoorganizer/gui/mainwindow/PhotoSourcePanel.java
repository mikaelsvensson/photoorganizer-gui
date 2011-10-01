package info.photoorganizer.gui.mainwindow;

import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.PhotoOrganizer;
import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;
import info.photoorganizer.gui.components.tree.POFolderTree;
import info.photoorganizer.gui.components.tree.POKeywordTree;
import info.photoorganizer.gui.components.tree.POTreeSelectionEvent;
import info.photoorganizer.gui.components.tree.POTreeSelectionListener;
import info.photoorganizer.gui.search.FolderContentMatchProvider;
import info.photoorganizer.gui.search.MatchProvider;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.StringUtils;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

public class PhotoSourcePanel extends DockablePanel<JScrollPane>
{
    
    private static final Logger L = info.photoorganizer.util.Log.getLogger(PhotoSourcePanel.class);

    public PhotoSourcePanel(PhotoOrganizer application)
    {
        super(application);
    }
    
//    private JPanel p = new JPanel(new BorderLayout());
    
    private POFolderTree folderTree = null;
    private JRadioButton allPhotos = null;
    private JRadioButton folderPhotos = null;
    private POKeywordTree keywordTree = null;

    @Override
    protected JScrollPane createComponent()
    {
        folderTree = new POFolderTree();
        folderTree.addSelectionListener(new POTreeSelectionListener<File>()
        {
            
            @Override
            public void selectionChanged(POTreeSelectionEvent<File> event)
            {
                List<MatchProvider> prods = new ArrayList<MatchProvider>();
                
                folderPhotos.setSelected(true);
                
                for (File folder : event.getSelection())
                {
                    prods.add(new FolderContentMatchProvider(folder, getDatabase()));
                }
                
                L.info("Match providers: " + prods);
                
                _application.startPhotoSearch(prods);
            }
        });
        //folderTree.setPreferredSize(new Dimension(200, 500));
        
        keywordTree = new POKeywordTree(getDatabase());
//        keywordTree.setScrollsOnExpand(false);
//        keywordTree.setPreferredSize(new Dimension(100, 100));
//        keywordTree.setLargeModel(true);
        keywordTree.addSelectionListener(new POTreeSelectionListener<KeywordTagDefinition>()
                {
                    @Override
                    public void selectionChanged(POTreeSelectionEvent<KeywordTagDefinition> event)
                    {
                        L.fine("User has selected these keywords: " + event.getSelection().toString());
                        for (KeywordTagDefinition keyword : event.getSelection())
                        {
                            L.fine("Synonyms for " + keyword.getName() + ": " + StringUtils.join(keyword.getSynonyms(), String.valueOf(KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR), true, KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK));
                        }
                    }
                });
        
        allPhotos = new JRadioButton(getText("ENTIRE_DATABASE_LABEL"));
        folderPhotos = new JRadioButton(getText("FOLDERS_LABEL"));
        
        ButtonGroup group = new ButtonGroup();
        group.add(allPhotos);
        group.add(folderPhotos);
        
        JPanel sourcesPanel = GuiComponentFactory.createBoxLayoutPanel(
                false, 
                GuiComponentFactory.createLabel(getText("PHOTO_SOURCES_HEADER")), 
                allPhotos, 
                folderPhotos,
                folderTree,
                GuiComponentFactory.createLabel(getText("KEYWORDS_HEADER")),
                keywordTree);
        
//        p.add(sourcesPanel, BorderLayout.NORTH);
//        p.add(new JScrollPane(folderTree), BorderLayout.CENTER);
//        p.setPreferredSize(new Dimension(300, 200));
        return new JScrollPane(sourcesPanel/*, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER*/);
    }

}
