package info.photoorganizer.gui.search;

import java.util.regex.Pattern;

import info.photoorganizer.metadata.Photo;

public class FilenameCriterion implements SearchCriterion
{
    private Pattern p = Pattern.compile("^MS_");

    @Override
    public boolean accept(Photo photo)
    {
        return p.matcher(photo.getFile().getName()).matches();
    }

}
