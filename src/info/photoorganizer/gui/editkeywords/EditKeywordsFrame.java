package info.photoorganizer.gui.editkeywords;

import info.photoorganizer.database.Database;
import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.GuiComponentFactory;
import info.photoorganizer.gui.components.frame.PODialog;
import info.photoorganizer.gui.components.tagfield.POTagField;
import info.photoorganizer.gui.components.tree.POKeywordTreeModel;
import info.photoorganizer.gui.components.tree.POTreePanel;
import info.photoorganizer.gui.editkeyword.EditKeywordFrame;
import info.photoorganizer.gui.shared.CloseOperation;
import info.photoorganizer.gui.shared.FlowLayoutAlignment;
import info.photoorganizer.gui.shared.KeyModifiers;
import info.photoorganizer.gui.shared.KeyboardShortcutDefinition;
import info.photoorganizer.gui.shared.Keys;
import info.photoorganizer.gui.shared.POActionListener;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.util.I18n;
import info.photoorganizer.util.StringUtils;
import info.photoorganizer.util.WordInfo;
import info.photoorganizer.util.command.CommandManager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class EditKeywordsFrame extends PODialog
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final char KEYWORD_SEPARATION_CHARACTER = ' ';

    private static final char KEYWORD_QUOTATION_CHARACTER = '"';

    public static void main(String[] args)
    {
        GuiComponentFactory.initDefaultLookAndFeel();
        GuiComponentFactory.show(new EditKeywordsFrame());
    }
    private JButton _addButton = null;
    
    private POTagField<KeywordTagDefinition> _testKeywordsField = null;
    
    public POTagField<KeywordTagDefinition> getTestKeywordsField()
    {
        if (_testKeywordsField == null)
        {
            _testKeywordsField = GuiComponentFactory.createTagField(new KeywordTagDefinition[] {}, 20, getKeywordWordprovider());
            _testKeywordsField.getDocument().addDocumentListener(new DocumentListener()
            {
                
                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    updateStatus();
                }
                
                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    updateStatus();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    updateStatus();
                }
                
                private void updateStatus()
                {
                    List<WordInfo> words = _testKeywordsField.getWords(false);
                    System.out.print("Keywords entered: ");
                    for (WordInfo word : words)
                    {
                        KeywordTagDefinition keyword = getDatabase().getRootKeyword().getChildByName(word.getWord(), true);
                        if (null != keyword)
                        {
                            System.out.print(keyword);
                        }
                        else
                        {
                            System.out.print(word.getWord() + "(new)");
                        }
                        System.out.print(' ');
                    }
                    System.out.println();
                }
            });
        }
        return _testKeywordsField;
    }
    private POActionListener _addButtonActionListener = new POActionListener(false)
    {
        @Override
        public void actionPerformedImpl(ActionEvent e)
        {
            onAddButton_actionPerformed(e);
        }
    };
    
    private JScrollPane _centerPanel = null;
    private JButton _editButton = null;
    private POActionListener _editButtonActionListener = new POActionListener(false)
    {
        
        @Override
        public void actionPerformedImpl(ActionEvent event)
        {
            onEditButton_actionPerformed(event);
        }

    };
    
    private POTreePanel _keywordsTree = null;
    
    private JButton _okButton = null;
    private POActionListener _okButtonActionListener = new POActionListener()
    {
        @Override
        public void actionPerformedImpl(ActionEvent arg0)
        {
            onOkButton_actionPerformed(arg0);
        }
        
    };
    
    private POActionListener _cancelButtonActionListener = new POActionListener()
    {
        @Override
        public void actionPerformedImpl(ActionEvent arg0)
        {
            onCancelButton_actionPerformed(arg0);
        }
    };
    
    private void onCancelButton_actionPerformed(ActionEvent arg0)
    {
        dispose();
    }
    
    private JPanel _pageEndPanel = null;
    
    private JButton _removeButton = null;
    
    private POActionListener _removeButtonActionListener = new POActionListener(false)
    {        
        @Override
        public void actionPerformedImpl(ActionEvent e)
        {
            onRemoveButton_actionPerformed(e);
        }   
    };

    private JButton _cancelButton = null;
    
    protected EditKeywordsFrame()
    {
        super("TITLE", 600, 600, CloseOperation.DISPOSE_ON_CLOSE, GuiComponentFactory.createBorderLayoutPanel());
        
        initComponents();
        
        initKeyboardShortcuts();
    }
    public JButton getAddButton()
    {
        if (null == _addButton)
        {
            _addButton = GuiComponentFactory.createButton(getI18nText("ADD_KEYWORD_BUTTON_TEXT"), _addButtonActionListener);
        }
        return _addButton;
    }
    public JScrollPane getCenterPanel()
    {
        if (null == _centerPanel)
        {
            _centerPanel = new JScrollPane(getKeywordsTree());
        }
        return _centerPanel;
    }
    public JButton getEditButton()
    {
        if (null == _editButton)
        {
            _editButton = GuiComponentFactory.createButton(getI18nText("EDIT_KEYWORD_BUTTON_TEXT"), _editButtonActionListener);
        }
        return _editButton;
    }
    public JButton getRemoveButton()
    {
        if (null == _removeButton)
        {
            _removeButton = GuiComponentFactory.createButton(getI18nText("REMOVE_KEYWORD_BUTTON_TEXT"), _removeButtonActionListener);
        }
        return _removeButton;
    }
    
    private POTreePanel getKeywordsTree()
    {
        if (null == _keywordsTree)
        {
            Database database = getDatabase();
            
            _keywordsTree = new POTreePanel(new POKeywordTreeModel(database.getRootKeyword()));
            _keywordsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            //_keywordsTree.setRootVisible(false);
            _keywordsTree.setEditable(true);
//            _keywordsTree.addTreeSelectionListener(new TreeSelectionListener()
//            {
//                @Override
//                public void valueChanged(TreeSelectionEvent e)
//                {
//                    onKeywordsTree_TreeSelection_valueChanged(e);
//                }
//            });
            
        }
        return _keywordsTree;
    }

    private JButton getOkButton()
    {
        if (null == _okButton)
        {
            _okButton = GuiComponentFactory.createButton(getI18nText("OK_BUTTON_TEXT"), _okButtonActionListener);
        }
        return _okButton;
    }
    
    private JButton getCancelButton()
    {
        if (null == _cancelButton)
        {
            _cancelButton = GuiComponentFactory.createButton(getI18nText("CANCEL_BUTTON_TEXT"), _cancelButtonActionListener);
        }
        return _cancelButton;
    }
    
    private JPanel getPageEndPanel()
    {
        if (null == _pageEndPanel)
        {
            _pageEndPanel = GuiComponentFactory.createFlowLayoutPanel(
                    FlowLayoutAlignment.RIGHT, 
                    getTestKeywordsField(),
                    getAddButton(), 
                    getRemoveButton(),
                    getEditButton(),
                    getOkButton(),
                    getCancelButton());
        }
        return _pageEndPanel;
    }
    
    private KeywordTagDefinition getSelectedKeyword()
    {
        return (KeywordTagDefinition) getKeywordsTree().getLastSelectedPathComponent();
    }
    
    private void initKeyboardShortcuts()
    {
        addKeyboardShortcuts(
                new KeyboardShortcutDefinition(_removeButtonActionListener, null, Keys.DELETE, KeyModifiers.NONE, getKeywordsTree()),
                new KeyboardShortcutDefinition(_addButtonActionListener, null, Keys.INSERT, KeyModifiers.NONE, getKeywordsTree()),
                new KeyboardShortcutDefinition(_editButtonActionListener, null, Keys.ENTER, KeyModifiers.NONE, getKeywordsTree())
                );
    }
    
    private void onEditButton_actionPerformed(ActionEvent event)
    {
        KeywordTagDefinition keyword = getSelectedKeyword();
        GuiComponentFactory.show(new EditKeywordFrame(this, keyword));
    }
    
    private void onKeywordsTree_TreeSelection_valueChanged()
    {
        TreePath selectedPath = getKeywordsTree().getSelectionPath();
        boolean isSelected = null != selectedPath;
        _addButtonActionListener.setEnabled(isSelected);
        _removeButtonActionListener.setEnabled(isSelected);
        _editButtonActionListener.setEnabled(isSelected);
        
        if (isSelected)
        {
            System.out.println("Synonyms: " + StringUtils.join(((KeywordTagDefinition)selectedPath.getLastPathComponent()).getSynonyms(), String.valueOf(KEYWORD_SEPARATION_CHARACTER), true, KEYWORD_QUOTATION_CHARACTER));
        }
        
    }
    
    private void onOkButton_actionPerformed(ActionEvent arg0)
    {
        try
        {
            getDatabase().close();
            dispose();
        }
        catch (DatabaseStorageException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void onRemoveButton_actionPerformed(ActionEvent e)
    {
        KeywordTagDefinition keyword = getSelectedKeyword();
        
        int childCount = keyword.getChildCount();
        String question = I18n.getInstance().getString(getClass(), childCount > 0 ? "REMOVE_KEYWORD_CONFIRM_WITH_DESCENDANTS_TEXT" : "REMOVE_KEYWORD_CONFIRM_TEXT", keyword.getName(), childCount);
        
        if (JOptionPane.showConfirmDialog(this, question, I18n.getInstance().getString(getClass(), "REMOVE_KEYWORD_CONFIRM_HEADER"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            RemoveKeywordCommand cmd = new RemoveKeywordCommand(keyword);
            CommandManager.getInstance().doAction(cmd);
            
            POTreePanel keywordsTree = getKeywordsTree();
            if (cmd.getParentBeforeRemoval() != null && cmd.getParentBeforeRemoval() != keywordsTree.getTreeModel().getRoot())
            {
                keywordsTree.setSelectionPath(new TreePath(cmd.getParentBeforeRemoval().getPath()));
            }
            else
            {
                keywordsTree.clearSelection();
            }
        }
    }

    protected void initComponents()
    {
        Container contentPane = getContentPane();
        contentPane.add(getCenterPanel(), BorderLayout.CENTER);
        contentPane.add(getPageEndPanel(), BorderLayout.PAGE_END);
        
        setDefaultButton(getOkButton());
        setCancelButton(getCancelButton());
    }

    protected void onAddButton_actionPerformed(ActionEvent e)
    {
        POTreePanel keywordsTree = getKeywordsTree();
        KeywordTagDefinition keyword = getSelectedKeyword();
        
        String newName = JOptionPane.showInputDialog(this, I18n.getInstance().getString(getClass(), "ADD_KEYWORD_NEW_NAME_TEXT"), I18n.getInstance().getString(getClass(), "ADD_KEYWORD_NEW_NAME_HEADER"), JOptionPane.QUESTION_MESSAGE);
        if (null != newName && newName.length() > 0)
        {
            AddKeywordCommand cmd = new AddKeywordCommand(keyword, newName);
            CommandManager.getInstance().doAction(cmd);
            
            TreePath newChildPath = new TreePath(cmd.getNewKeyword().getPath());
            keywordsTree.makeVisible(newChildPath);
            keywordsTree.setSelectionPath(newChildPath);
        }
    }
}
