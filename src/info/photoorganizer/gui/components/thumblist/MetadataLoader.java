package info.photoorganizer.gui.components.thumblist;

import java.io.File;
import java.util.Map;

public interface MetadataLoader
{
    Map<Object, Object> getMetadata(File file);
}
