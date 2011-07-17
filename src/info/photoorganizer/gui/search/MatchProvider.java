package info.photoorganizer.gui.search;

import java.util.Iterator;

public interface MatchProvider
{
    Iterator<Match> getItems();
}
