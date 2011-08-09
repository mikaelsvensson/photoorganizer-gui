package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.gui.components.thumblist.ImageLoader;
import info.photoorganizer.gui.shared.Logging;
import info.photoorganizer.metadata.Orientation;
import info.photoorganizer.metadata.Rotation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleFilter;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * The overall goal of the default image is <em>to as fast as possible 
 * The default image loader works like this:
 *
 */
public class DefaultImageLoader implements ImageLoader
{
    private static final Logger L = Logging.getLogger(ImageLoader.class);
    
    public static class CacheEntry
    {
        private BufferedImage image = null;
        private Dimension size = null;
        private Orientation orientation = null;
        
        private CacheEntry(BufferedImage image, Dimension size, Orientation orientation)
        {
            super();
            this.image = image;
            this.size = size;
            this.orientation = orientation;
        }
        
        public boolean isEqualOrLarger(Dimension size)
        {
            return size != null && this.size != null && this.size.width >= size.width && this.size.height >= size.height;
        }
    }
    
    /*
     * Some performance metrics for the different algorithms (some seem to have the same name):
     * 
     * Algorithm                     "Points" (less is more)
     * -----------------------------------------------------
     * Box                           4484
     * BSpline                       6127
     * Triangle                      6233
     * Bell                          7841
     * BSpline                       9111
     * BSpline                       9186
     * BiCubicHighFreqResponse       9190
     * BiCubic                       9595
     * Lanczos3                      12873
     * 
     * Box                           19533
     * Triangle                      21076
     * BSpline                       21344
     * Bell                          23109
     * BiCubicHighFreqResponse       24391
     * BSpline                       24407
     * BSpline                       24515
     * BiCubic                       25157
     * Lanczos3                      28468
     * 
     * Box                           19580
     * BSpline                       21172
     * Bell                          23185
     * BSpline                       24217
     * BSpline                       24249
     * BiCubicHighFreqResponse       24311
     * BiCubic                       25219
     * Triangle                      26423
     * Lanczos3                      28018
     * 
     * The two last test runs are significantly slower than the first one 
     * because the images were not cached and it can therefore be assumed 
     * that image loading took roughly 15 seconds per algorithm.
     * 
     * In any case, the Box algorithm is the fastest one (at least in this 
     * test, which reduced 4 megapixel images to thumbnail-sized images). 
     */
    
    private static final ResampleFilter DEFAULT_FILTER_ALGORITHM = ResampleFilters.getBoxFilter(); // Box seems to be the fastest one

    private static final int DEFAULT_CACHE_SIZE = 100;
    
    private ResampleFilter resampleAlgorithm = null;
    private LinkedHashMap<File, CacheEntry> cache = new LinkedHashMap<File, CacheEntry>(DEFAULT_CACHE_SIZE);
    private int cacheSize = DEFAULT_CACHE_SIZE;
    
    public DefaultImageLoader()
    {
        this(DEFAULT_FILTER_ALGORITHM);
    }
    
    public DefaultImageLoader(ResampleFilter resampleAlgorithm)
    {
        super();
        this.resampleAlgorithm = resampleAlgorithm;
    }

    public ResampleFilter getResampleAlgorithm()
    {
        return resampleAlgorithm;
    }

    public void setResampleAlgorithm(ResampleFilter resampleAlgorithm)
    {
        this.resampleAlgorithm = resampleAlgorithm;
    }
    
    protected void addCacheEntry(File f, BufferedImage img, Dimension size, Orientation orientation)
    {
        purgeCache(true);
        L.fine("Adding " + f.getName() + " to cache.");
        cache.put(f, new CacheEntry(img, size, orientation));
    }
    
    protected CacheEntry getCacheEntry(File f)
    {
        return cache.get(f);
    }

    private void purgeCache(boolean makeRoomForOneNewEntry)
    {
        int num = cache.size() - (makeRoomForOneNewEntry ? cacheSize-1 : cacheSize);
        if (cache.size() >= cacheSize)
        {
            L.fine("Removing " + num + " cached images.");
            Iterator<CacheEntry> iterator = cache.values().iterator();
            while (num > 0)
            {
                iterator.next();
                iterator.remove();
                num--;
            }
        }
    }
    
    @Override
    public Image getImage(File file, Dimension targetSize/*, Orientation imageOrientationInFile*/)
    {
        BufferedImage image = null;
        try
        {
            BufferedImage sourceImage = null;
            Orientation imageOrientationInFile = null;
            Dimension sourceImageSize = null;
            CacheEntry cacheEntry = getCacheEntry(file/*, targetSize*/);
            if (null != cacheEntry)
            {
                boolean useCachedImage = false;
                if (cacheEntry.isEqualOrLarger(targetSize))
                {
                    useCachedImage = true;
                }
                else
                {
                    sourceImage = getSourceImage(file, targetSize);
                    if (null != sourceImage)
                    {
                        imageOrientationInFile = getImageOrientation(file);
                        sourceImageSize = new Dimension(sourceImage.getWidth(), sourceImage.getHeight());
                    }
                    else
                    {
                        L.fine("Could not load image " + file.getName() + ". Using whatever image has been previously cached.");
                        useCachedImage = true;
                    }
                }
                if (useCachedImage)
                {
                    sourceImage = cacheEntry.image;
                    imageOrientationInFile = cacheEntry.orientation;
                    sourceImageSize = cacheEntry.size;
                }
            }
            else
            {
                sourceImage = getSourceImage(file, targetSize);
                if (null != sourceImage)
                {
                    imageOrientationInFile = getImageOrientation(file);
                    sourceImageSize = new Dimension(sourceImage.getWidth(), sourceImage.getHeight());
                }
                else
                {
                    L.fine("Could not load image " + file.getName());
                    return null;
                }
            }
            
            if (sourceImageSize.height != targetSize.height || sourceImageSize.width != targetSize.width)
            //if (sourceImageSize.height > targetSize.height || sourceImageSize.width > targetSize.width)
            {
                // Resize is necessary.
                DimensionConstrain targetImageDimCons = null;
                if (imageOrientationInFile.getRotation() != Rotation.NONE)
                {
                    // Swap height and width since image will be rotated later on.
                    targetImageDimCons = DimensionConstrain.createMaxDimension(targetSize.height, targetSize.width, true);
                }
                else
                {
                    targetImageDimCons = DimensionConstrain.createMaxDimension(targetSize.width, targetSize.height, true);
                }
                
                ResampleOp resampleOperation = new ResampleOp(targetImageDimCons);
                resampleOperation.setFilter(resampleAlgorithm);
                BufferedImage resizedSourceImage = resampleOperation.filter(sourceImage, null);
                sourceImage = resizedSourceImage;
                
            }
            else
            {
                //image = sourceImage;
            }
            
            AffineTransform transformation = getCenterAndRotateTransformation(imageOrientationInFile, new Dimension(sourceImage.getWidth(), sourceImage.getHeight()), targetSize);
            
            image = new BufferedImage(targetSize.width, targetSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawRenderedImage(sourceImage, transformation);
            g.dispose();
            
            if (targetSize.width * targetSize.height < sourceImageSize.width * sourceImageSize.height * 0.8)
            {
                addCacheEntry(file, image, targetSize, Orientation.NORMAL);
            }
            
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return image;
    }
    
    protected CacheEntry getCacheEntry(File file, Dimension size)
    {
        CacheEntry cacheEntry = getCacheEntry(file);
        if (cacheEntry != null && cacheEntry.isEqualOrLarger(size))
        {
            return cacheEntry;
        }
        return null;
    }

    protected BufferedImage getSourceImage(File file, Dimension size) throws IOException
    {
        CacheEntry cacheEntry = getCacheEntry(file, size);
        if (null != cacheEntry)
        {
            // Cached image is large enough
            L.fine("Found suitable image in cache.");
            return cacheEntry.image;
        }
        else
        {
            L.fine("No suitable image cached. Reading complete image.");
            return ImageIO.read(file);
        }
    }
    
    protected Orientation getImageOrientation(File file)
    {
        return Orientation.NORMAL;
    }
    
    private AffineTransform getCenterAndRotateTransformation(Orientation imageOrientationInFile, Dimension sourceSize, Dimension targetSize)
    {
        double sourceSizeWidth = (double)sourceSize.width;
        double sourceSizeHeight = (double)sourceSize.height;
        double targetSizeWidth = (double)targetSize.width;
        double targetSizeHeight = (double)targetSize.height;
        
        AffineTransform transformation = new AffineTransform();
        
        // Center
        transformation.translate(
                (targetSizeWidth - sourceSizeWidth) / 2,
                (targetSizeHeight - sourceSizeHeight) / 2);
        
        if (null != imageOrientationInFile)
        {
            switch (imageOrientationInFile.getRotation())
            {
            case NONE:
                break;
            case LEFT:
                // Rotate
                transformation.quadrantRotate(1, sourceSizeWidth/2, sourceSizeHeight/2);
                break;
            case RIGHT:
                // Rotate
                transformation.quadrantRotate(-1, sourceSizeWidth/2, sourceSizeHeight/2);
            }
            if (imageOrientationInFile.isFlippedRightLeft())
            {
                transformation.translate(-sourceSizeWidth, sourceSizeHeight);
            }
            if (imageOrientationInFile.isFlippedUpDown())
            {
                transformation.translate(sourceSizeWidth, -sourceSizeHeight);
            }
        }
        
        return transformation;
    }

}