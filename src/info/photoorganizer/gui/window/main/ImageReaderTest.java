package info.photoorganizer.gui.window.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifThumbnailDirectory;

public class ImageReaderTest
{

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix("jpg");
        while (iterator.hasNext())
        {
            ImageReader reader = iterator.next();
            System.out.println(reader.getFormatName());
            File folder = new File("F:\\Fotografier\\Diverse\\Morgondis över Mensättra");
//            File folder = new File("F:\\Fotografier\\Diverse\\Mitt rum");
            for (File f : folder.listFiles())
            {
                try
                {
                    reader.setInput(new FileImageInputStream(f));
                    System.out.println(reader.getHeight(0));
                    System.out.println(reader.getWidth(0));
                    System.out.println(reader.getNumImages(true));
                    System.out.println(reader.getNumThumbnails(0));
                    
                    Metadata metadata = ImageMetadataReader.readMetadata(f);
                    ExifThumbnailDirectory directory = metadata.getDirectory(ExifThumbnailDirectory.class);
                    System.out.println(directory.getThumbnailData().length);
                }
                catch (FileNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (ImageProcessingException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
        }
    }

}
