package info.photoorganizer.gui.components.thumblist;

import info.photoorganizer.metadata.Orientation;

import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mortennobel.imagescaling.ResampleFilter;
import com.mortennobel.imagescaling.ResampleFilters;

public class DefaultImageLoaderSpeedTest
{

    private static final File FOLDER = new File("F:\\Fotografier\\Diverse\\Mitt rum");
    
    private static final int ITERATIONS = 1;

    public static void main(String[] args)
    {
        
        Map<ResampleFilter, Long> result = new HashMap<ResampleFilter, Long>();
        
        ResampleFilter[] algorithms = {
                ResampleFilters.getBiCubicFilter(),
                ResampleFilters.getBellFilter(),
                ResampleFilters.getBiCubicHighFreqResponse(),
                ResampleFilters.getBoxFilter(),
                ResampleFilters.getBSplineFilter(),
                ResampleFilters.getHermiteFilter(),
                ResampleFilters.getLanczos3Filter(),
                ResampleFilters.getMitchellFilter(),
                ResampleFilters.getTriangleFilter() };
        DefaultImageLoader loader = new DefaultImageLoader();
        File[] files = FOLDER.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".jpg");
            }

        });
        
        Dimension size = new Dimension(200, 200);
        for (int x=0; x < algorithms.length; x++)
        {
            for (int y=x; y < x + algorithms.length; y++)
            {
                ResampleFilter resampleFilter = algorithms[y % algorithms.length];
//            }
//            for (ResampleFilter resampleFilter : algorithms)
//            {
                loader.setResampleAlgorithm(resampleFilter);
                System.err.println("Resizing " + files.length + " images " + ITERATIONS + " times using the " + resampleFilter.getName() + " algorithm.");
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < ITERATIONS; i++)
                {
                    for (File f : files)
                    {
                        loader.getImage(f, size/*, Orientation.NORMAL*/);
                    }
                }
                long endTime = System.currentTimeMillis();
                long sum = result.containsKey(resampleFilter) ? result.get(resampleFilter) : 0;
                sum += (endTime - startTime);
                result.put(resampleFilter, Long.valueOf(sum));
            }
        }
        List<Entry<ResampleFilter, Long>> res = new ArrayList<Map.Entry<ResampleFilter,Long>>();
        res.addAll(result.entrySet());
        Collections.sort(res, new Comparator<Entry<ResampleFilter, Long>>()
        {
            @Override
            public int compare(Entry<ResampleFilter, Long> o1, Entry<ResampleFilter, Long> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        for (Entry<ResampleFilter, Long> entry : res)
        {
            System.out.format("%-30s%d\n", entry.getKey().getName(), entry.getValue());
        }
    }
}
