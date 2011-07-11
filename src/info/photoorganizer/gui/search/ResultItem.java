package info.photoorganizer.gui.search;

import info.photoorganizer.metadata.Photo;

import java.io.File;

public class ResultItem
{
    private Photo _photo = null;
    private File _photoFile = null;
    
    public ResultItem(File photoFile)
    {
        super();
        setFile(photoFile);
    }

    public ResultItem(Photo photo)
    {
        super();
        setPhoto(photo);
    }
    
    public Photo getPhoto()
    {
        return _photo;
    }

    public File getPhotoFile()
    {
        if (_photo != null)
        {
            return _photo.getFile();
        }
        else
        {
            return _photoFile;
        }
    }

    private void setFile(File photoFile)
    {
        _photo = null;
        _photoFile = photoFile;
    }

    private void setPhoto(Photo photo)
    {
        _photo = photo;
        _photoFile = null;
    }
}
