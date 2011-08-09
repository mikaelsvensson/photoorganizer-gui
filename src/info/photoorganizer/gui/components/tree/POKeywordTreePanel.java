package info.photoorganizer.gui.components.tree;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;
import info.photoorganizer.gui.shared.Logging;
import info.photoorganizer.metadata.KeywordTagDefinition;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class POKeywordTreePanel extends POTreePanel<POKeywordTreeModel, KeywordTagDefinition>
{
    private static final Logger L = Logging.getLogger(POKeywordTree.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

//    private POTree<POKeywordTreeModel> _keywordsTree = null;
    
    private JPopupMenu _popupMenu = null;
    
    private ActionListener _popupMenuItemListener = new ActionListener()
    {
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            L.info(e.getActionCommand());
        }
    };

    public POKeywordTreePanel(Database database, boolean scrollable)
    {
        super(new POKeywordTreeModel(database), scrollable);
        init();
    }

    public JPopupMenu getPopupMenu()
    {
        if (null == _popupMenu)
        {
            _popupMenu = new JPopupMenu();
            JMenuItem item = new JMenuItem("hej");
            item.addActionListener(_popupMenuItemListener);
            _popupMenu.add(item);
        }
        return _popupMenu;
    }

    private void init()
    {
        addMouseListener(new MouseListener()
        {
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
            }
            
            @Override
            public void mouseEntered(MouseEvent e)
            {
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
            }
            
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    getPopupMenu().show(POKeywordTreePanel.this, e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    getPopupMenu().show(POKeywordTreePanel.this, e.getX(), e.getY());
                }
            }
        });
//        addTreeSelectionListener(new TreeSelectionListener()
//        {
//            @Override
//            public void valueChanged(TreeSelectionEvent e)
//            {
//                onKeywordsTree_TreeSelection_valueChanged(e);
//            }
//        });
        setTransferHandler(new POKeywordTreeTransferHandler(this));
    }
    
//    private void onKeywordsTree_TreeSelection_valueChanged(TreeSelectionEvent e)
//    {
//        for (KeywordTagDefinition keyword : getSelection())
//        {
//            System.out.println("Synonyms for " + keyword.getName() + ": " + StringUtils.join(keyword.getSynonyms(), String.valueOf(KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR), true, KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK));
//        }
//    }
}
