package info.photoorganizer.gui.components.tree;

import info.photoorganizer.database.Database;
import info.photoorganizer.metadata.KeywordTagDefinition;
import info.photoorganizer.metadata.KeywordTagDefinitionEvent;
import info.photoorganizer.metadata.KeywordTagDefinitionEventListener;
import info.photoorganizer.metadata.TagDefinitionEvent;

import javax.swing.tree.TreePath;

public class POKeywordTreeModel extends POTreeModel implements KeywordTagDefinitionEventListener
{
    private KeywordTagDefinition root = null;
    
    public POKeywordTreeModel(Database database)
    {
        super();
        this.root = database.getRootKeyword();
        this.root.addKeywordEventListener(this);
    }
    
    public POKeywordTreeModel(KeywordTagDefinition root)
    {
        super();
        this.root = root;
        this.root.addKeywordEventListener(this);
    }

    @Override
    public Object getChild(Object parent, int index)
    {
        return ((KeywordTagDefinition)parent).getChild(index);
    }
    
    @Override
    public int getChildCount(Object parent)
    {
        return ((KeywordTagDefinition)parent).getChildCount();
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        return ((KeywordTagDefinition)parent).getIndexOfChild((KeywordTagDefinition) child);
    }
    
    @Override
    public KeywordTagDefinition getRoot()
    {
        return root;
    }
    
    @Override
    public boolean isLeaf(Object node)
    {
        return ((KeywordTagDefinition)node).getChildCount() == 0;
    }
    
    @Override
    public void keywordInserted(KeywordTagDefinitionEvent keywordEvent)
    {
        fireTreeNodesInsertedEvent(
                keywordEvent.getSource(), 
                keywordEvent.getSource().getPath(), 
                keywordEvent.getTargetIndices(), 
                keywordEvent.getTargets()
                );
    }
    
    @Override
    public void keywordStructureChanged(KeywordTagDefinitionEvent keywordEvent)
    {
        fireTreeStructureChangedEvent(
                keywordEvent.getSource(), 
                keywordEvent.getSource().getPath()
                );
    }
    
    @Override
    public void tagChanged(TagDefinitionEvent event)
    {
        if (event instanceof KeywordTagDefinitionEvent)
        {
            KeywordTagDefinitionEvent keywordEvent = (KeywordTagDefinitionEvent)event;
            fireTreeNodesRemovedEvent(
                    keywordEvent.getSource(), 
                    keywordEvent.getSource().getPath(), 
                    keywordEvent.getTargetIndices(), 
                    keywordEvent.getTargets()
                    );
        }
    }
    
    @Override
    public void tagDeleted(TagDefinitionEvent event)
    {
        if (event instanceof KeywordTagDefinitionEvent)
        {
            KeywordTagDefinitionEvent keywordEvent = (KeywordTagDefinitionEvent)event;
            fireTreeNodesRemovedEvent(
                    keywordEvent.getSource(), 
                    keywordEvent.getSource().getPath(), 
                    keywordEvent.getTargetIndices(), 
                    keywordEvent.getTargets()
                    );
        }
    }
    
    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        ((KeywordTagDefinition)path.getLastPathComponent()).setName(newValue.toString());
    }
}
