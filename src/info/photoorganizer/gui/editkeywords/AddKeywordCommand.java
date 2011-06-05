package info.photoorganizer.gui.editkeywords;

import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.shared.POCommand;
import info.photoorganizer.metadata.KeywordTagDefinition;


public class AddKeywordCommand extends POCommand
{
    private KeywordTagDefinition _parent = null;
    private KeywordTagDefinition _newKeyword = null;
    public KeywordTagDefinition getNewKeyword()
    {
        return _newKeyword;
    }

    private String _newName = null;
    
    protected AddKeywordCommand(KeywordTagDefinition parent, String newName)
    {
        super();
        _parent = parent;
        _newName = newName;
    }

    @Override
    public void doAction()
    {
        try
        {
            _newKeyword = _parent.createChild(_newName);
            _newKeyword.store();
        }
        catch (DatabaseStorageException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public void redoAction()
    {
        try
        {
            _parent.addChild(_newKeyword);
            _parent.store();
        }
        catch (DatabaseStorageException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public void undoAction()
    {
        try
        {
            _parent.removeChild(_newKeyword);
            _parent.store();
        }
        catch (DatabaseStorageException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
