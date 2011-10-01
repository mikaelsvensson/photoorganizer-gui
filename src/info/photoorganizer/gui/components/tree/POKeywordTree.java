package info.photoorganizer.gui.components.tree;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.components.frame.POCloseReason;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.frame.POFrame;
import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;
import info.photoorganizer.gui.editkeyword.EditKeywordDialog;
import info.photoorganizer.gui.shared.POAction;
import info.photoorganizer.metadata.KeywordTagDefinition;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreeModel;

public class POKeywordTree extends POTree<POKeywordTreeModel, KeywordTagDefinition>
{
    private static final String ACTIONNAME_SETSYNONYMS = "ACTIONNAME_SETSYNONYMS";
    private static final String ACTIONNAME_EDIT = "ACTIONNAME_EDIT";

    private static final Logger L = info.photoorganizer.util.Log.getLogger(POKeywordTree.class);
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

//    private POTree<POKeywordTreeModel> _keywordsTree = null;
    
    private JPopupMenu _singleItemPopupMenu = null;
    private JPopupMenu _multipleItemsPopupMenu = null;
    
    protected Database _database;
    
    private ActionListener _popupMenuItemListener = new ActionListener()
    {
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            L.info(e.getActionCommand());
        }
    };

    public POKeywordTree(Database database)
    {
        super(new POKeywordTreeModel(database));
        init(database);
    }

//    @Override
//    public POKeywordTreeModel getModel()
//    {
//        return (POKeywordTreeModel) super.getModel();
//    }

    public JPopupMenu getSingleItemPopupMenu()
    {
        if (null == _singleItemPopupMenu)
        {
            _singleItemPopupMenu = new JPopupMenu();
            _singleItemPopupMenu.add(new JMenuItem(getActionMap().get(ACTIONNAME_EDIT)));
        }
        return _singleItemPopupMenu;
    }
    public JPopupMenu getMultipleItemsPopupMenu()
    {
        if (null == _multipleItemsPopupMenu)
        {
            _multipleItemsPopupMenu = new JPopupMenu();
            _multipleItemsPopupMenu.add(new JMenuItem(getActionMap().get(ACTIONNAME_SETSYNONYMS)));
        }
        return _multipleItemsPopupMenu;
    }

    private void init(Database database)
    {
        _database = database;
        
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
                //mouseReleased(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    if (getSelection().size() == 1)
                    {
                        getSingleItemPopupMenu().show(POKeywordTree.this, e.getX(), e.getY());
                    }
                    else if (getSelection().size() > 1)
                    {
                        getMultipleItemsPopupMenu().show(POKeywordTree.this, e.getX(), e.getY());
                    }
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
        setDragEnabled(true);
        setRootVisible(false);
        
        initActions();
    }

    private void initActions()
    {
        ActionMap actionMap = getActionMap();
        actionMap.put(ACTIONNAME_SETSYNONYMS, new POAction(getI18nText("SET_AS_SYNONYMS"))
        {
            
            @Override
            public void actionPerformed(ActionEvent event)
            {
                List<KeywordTagDefinition> selection = getSelection();
                if (selection.size() > 1)
                {
                    for (int i=1; i < selection.size(); i++)
                    {
                        try
                        {
                            KeywordTagDefinition.addSynonym(selection.get(0), selection.get(i), true);
                        }
                        catch (DatabaseStorageException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        actionMap.put(ACTIONNAME_EDIT, new POAction(getI18nText("EDIT_KEYWORD"))
        {
            
            @Override
            public void actionPerformed(ActionEvent event)
            {
                List<KeywordTagDefinition> selection = getSelection();
                if (selection.size() == 1)
                {
                    POFrame frame = (POFrame) ((JComponent)getSingleItemPopupMenu().getInvoker()).getTopLevelAncestor();
                    EditKeywordDialog editKeywordFrame = new EditKeywordDialog(frame, selection.get(0), _database);
                    if (editKeywordFrame.showModal() == POCloseReason.OK)
                    {
                        
                    }
                }
            }
        });
    }
    
//    private void onKeywordsTree_TreeSelection_valueChanged(TreeSelectionEvent e)
//    {
//        for (KeywordTagDefinition keyword : getSelection())
//        {
//            System.out.println("Synonyms for " + keyword.getName() + ": " + StringUtils.join(keyword.getSynonyms(), String.valueOf(KeywordTagDefinition.DEFAULT_KEYWORD_SEPARATOR), true, KeywordTagDefinition.DEFAULT_KEYWORD_QUOTATION_MARK));
//        }
//    }
}
