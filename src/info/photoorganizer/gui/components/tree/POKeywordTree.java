package info.photoorganizer.gui.components.tree;

import info.photoorganizer.database.Database;
import info.photoorganizer.metadata.KeywordTagDefinition;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class POKeywordTree extends POTreePanel<POKeywordTreeModel, KeywordTagDefinition>
{
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
            System.out.println(e.getActionCommand());
        }
    };

    public POKeywordTree(Database database)
    {
        super(new POKeywordTreeModel(database));
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
                    getPopupMenu().show(POKeywordTree.this, e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    getPopupMenu().show(POKeywordTree.this, e.getX(), e.getY());
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
