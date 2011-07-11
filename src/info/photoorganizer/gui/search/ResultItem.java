package info.photoorganizer.gui.search;

import info.photoorganizer.metadata.Image;

import java.io.File;

public class ResultItem
{
    private Image _image = null;
    private File _imageFile = null;
    
    public ResultItem(File imageFile)
    {
        super();
        setFile(imageFile);
    }

    public ResultItem(Image image)
    {
        super();
        setImage(image);
    }
    
    public Image getImage()
    {
        return _image;
    }

    public File getImageFile()
    {
        if (_image != null)
        {
            return _image.getFile();
        }
        else
        {
            return _imageFile;
        }
    }

    private void setFile(File imageFile)
    {
        _image = null;
        _imageFile = imageFile;
    }

    private void setImage(Image image)
    {
        _image = image;
        _imageFile = null;
    }
}
