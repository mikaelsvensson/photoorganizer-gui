package info.photoorganizer.gui.search;

import info.photoorganizer.metadata.Photo;

public class DatabaseMatch implements Match
{
    private Photo _photo = null;

    public DatabaseMatch(Photo photo)
    {
        super();
        _photo = photo;
    }

    @Override
    public Photo getPhoto()
    {
        return _photo;
    }

}
