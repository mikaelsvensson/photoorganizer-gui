package info.photoorganizer.gui.components.thumblist;


public class FileNameGrouper implements ImageGrouper
{

    @Override
    public String getName()
    {
        return "File name";
    }

    @Override
    public String getGroupName(ListItem item)
    {
        return String.valueOf(item.getFile().getName().toUpperCase().charAt(0));
    }

}
