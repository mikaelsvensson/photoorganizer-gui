package info.photoorganizer.gui.search;

import info.photoorganizer.metadata.Photo;

public interface SearchCriterion
{
    boolean accept(Photo photo);
}
