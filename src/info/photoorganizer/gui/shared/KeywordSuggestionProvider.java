/**
 * 
 */
package info.photoorganizer.gui.shared;

import info.photoorganizer.gui.components.tagfield.POTagFieldSuggestionProvider;
import info.photoorganizer.metadata.KeywordTagDefinition;

import java.util.LinkedList;
import java.util.List;

public class KeywordSuggestionProvider implements POTagFieldSuggestionProvider<KeywordTagDefinition>
{
    private KeywordTagDefinition _root = null;
    
    public KeywordSuggestionProvider(KeywordTagDefinition root)
    {
        super();
        _root = root;
    }

    @Override
    public List<KeywordTagDefinition> getSuggestions(String condition)
    {
        LinkedList<KeywordTagDefinition> res = new LinkedList<KeywordTagDefinition>();
        find(_root, condition.toLowerCase(), res);
        return res;
    }
    
    @Override
    public List<KeywordTagDefinition> getSynonyms(KeywordTagDefinition word)
    {
        LinkedList<KeywordTagDefinition> res = new LinkedList<KeywordTagDefinition>();
        for (KeywordTagDefinition synonym : word.getSynonyms())
        {
            res.add(synonym);
        }
        return res;
    }
    
    @Override
    public String toString(KeywordTagDefinition word)
    {
        return word.getName();
    }
    
    private void find(KeywordTagDefinition keyword, String condition, List<KeywordTagDefinition> res)
    {
        for (KeywordTagDefinition child : keyword.getChildren())
        {
            if (child.getName().toLowerCase().startsWith(condition))
            {
                res.add(child);
            }
            find(child, condition, res);
        }
    }
}