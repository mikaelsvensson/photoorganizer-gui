package info.photoorganizer.gui.search;

import java.io.File;

public class FolderContentsImageProducer extends ResultItemProducer
{
    
    private File _folder = null;
    private boolean _recursive = false;

    public FolderContentsImageProducer(File folder, boolean recursive)
    {
        super();
        _folder = folder;
        _recursive = recursive;
        System.err.println("FolderContentsImageProducer for " + _folder + " has been CREATED.");
    }

    @Override
    protected void doInBackgroundImpl()
    {
        addFiles(_folder, _recursive);
        if (isCancelled())
        {
            System.err.println("FolderContentsImageProducer for " + _folder + " has been cancelled.");
        }
    }
    
    private void addFiles(File folder, boolean recursive)
    {
        if (folder.isDirectory())
        {
            for (File f : folder.listFiles())
            {
                if (isCancelled())
                {
                    break;
                }
                if (f.isDirectory() && recursive)
                {
                    addFiles(f, recursive);
                }
                else if (f.isFile())
                {
                    addResultItem(new ResultItem(f));
                }
            }
        }
    }
    
}
