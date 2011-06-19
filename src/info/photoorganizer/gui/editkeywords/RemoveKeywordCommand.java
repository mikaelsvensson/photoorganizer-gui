package info.photoorganizer.gui.editkeywords;

import info.photoorganizer.database.DatabaseStorageException;
import info.photoorganizer.gui.shared.POCommand;
import info.photoorganizer.metadata.KeywordTagDefinition;

public class RemoveKeywordCommand extends POCommand
{
    
    private KeywordTagDefinition _keyword = null;
    private KeywordTagDefinition _parentBeforeRemoval = null;
    
    public KeywordTagDefinition getKeyword()
    {
        return _keyword;
    }

    public KeywordTagDefinition getParentBeforeRemoval()
    {
        return _parentBeforeRemoval;
    }

    protected RemoveKeywordCommand(KeywordTagDefinition keyword)
    {
        super();
        _keyword = keyword;
        _parentBeforeRemoval = _keyword.getParent(); // Cache for usage by undoAction().
    }

    @Override
    public void doAction()
    {
        if (null != _parentBeforeRemoval)
        {
            try
            {
                _keyword.remove();
            }
            catch (DatabaseStorageException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void redoAction()
    {
        doAction();
    }
    
    @Override
    public void undoAction()
    {
        if (null != _parentBeforeRemoval)
        {
            _parentBeforeRemoval.addChild(_keyword, true);
        }
    }
    
}
