package info.photoorganizer.gui.components.tagfield;

import java.util.List;

public interface POTagFieldSuggestionProvider<T>
{
    List<T> getSuggestions(String condition);
    List<T> getSynonyms(T word);
    String toString(T word);
}
