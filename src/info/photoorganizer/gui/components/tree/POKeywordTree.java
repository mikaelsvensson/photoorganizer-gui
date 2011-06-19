package info.photoorganizer.gui.components.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import info.photoorganizer.database.Database;

import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class POKeywordTree extends JScrollPane
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private POTree _keywordsTree = null;
    
    private JPopupMenu _popupMenu = null;
    
    private ActionListener _popupMenuItemListener = new ActionListener()
    {
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println(e.getActionCommand());
        }
    };

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

    Database _database = null;
    
    public POKeywordTree(Database database)
    {
        super();
        _database = database;
        setViewportView(getKeywordsTree());
    }

    public POKeywordTree(Database database, int vsbPolicy, int hsbPolicy)
    {
        super(vsbPolicy, hsbPolicy);
        _database = database;
        setViewportView(getKeywordsTree());
    }

    private POTree getKeywordsTree()
    {
        if (null == _keywordsTree)
        {
            initKeywordsTree();
        }
        return _keywordsTree;
    }

    private void initKeywordsTree()
    {
        _keywordsTree = new POTree(new POTreeModel(_database.getRootKeyword()));
        _keywordsTree.addMouseListener(new MouseListener()
        {
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    getPopupMenu().show(POKeywordTree.this, e.getX(), e.getY());
                }
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
            public void mouseExited(MouseEvent e)
            {
            }
            
            @Override
            public void mouseEntered(MouseEvent e)
            {
            }
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
            }
        });
        _keywordsTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                onKeywordsTree_TreeSelection_valueChanged(e);
            }
        });
    }
    
    private void onKeywordsTree_TreeSelection_valueChanged(TreeSelectionEvent e)
    {
        TreePath selectedPath = getKeywordsTree().getSelectionPath();
        boolean isSelected = null != selectedPath;
        
        if (isSelected)
        {
//            System.out.println("Synonyms: " + StringUtils.join(((KeywordTagDefinition)selectedPath.getLastPathComponent()).getSynonyms(), String.valueOf(KEYWORD_SEPARATION_CHARACTER), true, KEYWORD_QUOTATION_CHARACTER));
        }
        
    }}
