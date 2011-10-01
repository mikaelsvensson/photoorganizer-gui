package info.photoorganizer.gui.mainwindow;

import info.photoorganizer.database.Database;
import info.photoorganizer.gui.components.thumblist.DefaultImageLoader;
import info.photoorganizer.gui.components.thumblist.ImageLoader;
import info.photoorganizer.metadata.DefaultTagDefinition;
import info.photoorganizer.metadata.IntegerNumberTag;
import info.photoorganizer.metadata.Orientation;
import info.photoorganizer.metadata.Photo;
import info.photoorganizer.metadata.Tag;
import info.photoorganizer.metadata.TagDefinition;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifThumbnailDirectory;

public class POImageLoader extends DefaultImageLoader
{

    private static final Logger L = info.photoorganizer.util.Log.getLogger(ImageLoader.class);
    
    private Database _database = null;
    private TagDefinition _orientationTagDefinition = null; 
    private LinkedHashMap<File, BufferedImage> _embeddedThumbnails = new LinkedHashMap<File, BufferedImage>();
    
    public POImageLoader(Database database)
    {
        _database = database;
        _orientationTagDefinition = _database.getTagDefinition(DefaultTagDefinition.ORIENTATION.getId());
        //setResampleAlgorithm(ResampleFilters.getBellFilter());
    }

    @Override
    protected Orientation getImageOrientation(File file)
    {
        Photo photo = _database.getPhoto(file);
        if (null != photo)
        {
            Tag<? extends TagDefinition> orientationTag = photo.getTag(_orientationTagDefinition);
            if (orientationTag instanceof IntegerNumberTag)
            {
                return Orientation.fromExifValue(((IntegerNumberTag)orientationTag).getValue());
            }
        }
        return super.getImageOrientation(file);
    }

    @Override
    public Image getImage(File file, Dimension targetSize)
    {
        if (!_embeddedThumbnails.containsKey(file))
        {
            BufferedImage img;
            try
            {
                img = getEmbeddedThumbnail(file);
                _embeddedThumbnails.put(file, img);
                L.fine("Loaded embedded thumbnail in " + file.getName());
                CacheEntry cacheEntry = getCacheEntry(file, targetSize);
                if (null == cacheEntry && null != img)
                {
                    Orientation imageOrientation = getImageOrientation(file);
                    L.fine("Embedded thumbnail in " + file.getName() + " added to cache since no previously cached version exists. Orientation: " + imageOrientation + ".");
                    addCacheEntry(file, img, new Dimension(img.getWidth(), img.getHeight()), imageOrientation);
                }
            }
            catch (IOException e)
            {
                L.info("Could not load embedded thumbnail from " + file.getName() + ". " + e.getMessage() + ".");
            }
            
        }
        return super.getImage(file, targetSize);
    }

//    @Override
//    protected BufferedImage getSourceImage(File file, Dimension size) throws IOException
//    {
//        if (!_embeddedThumbnails.containsKey(file))
//        {
//            BufferedImage img = getEmbeddedThumbnail(file);
//            
//            _embeddedThumbnails.put(file, img);
//        }
//        
//        BufferedImage img = _embeddedThumbnails.get(file);
//        if (null != img)
//        {
//            if (img.getWidth() >= size.width && img.getHeight() >= size.height)
//            {
//                L.fine("Using embedded thumbnail for " + file.getName());
//                return img;
//            }
//            else
//            {
//                CacheEntry cacheEntry = getCacheEntry(file);
//                if (cacheEntry == null)
//                {
//                    addCacheEntry(f, img, size, orientation)
//                }
//            }
//        }
//        
//        return super.getSourceImage(file, size);
//    }

    private BufferedImage getEmbeddedThumbnail(File file) throws IOException
    {
        BufferedImage img = null;
        try
        {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            if (null != metadata)
            {
                ExifThumbnailDirectory directory = metadata.getDirectory(ExifThumbnailDirectory.class);
                if (null != directory)
                {
                    img = ImageIO.read(new ByteArrayInputStream(directory.getThumbnailData()));
                    L.fine("Found thumbnail for " + file.getName() + " inside image file.");
                }
            }
        }
        catch (ImageProcessingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }

}