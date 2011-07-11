package info.photoorganizer.gui.search;

import java.util.EventListener;

public interface IndexerEventListener extends EventListener
{
    void fileIndexed(IndexerEvent event);
}
