package info.photoorganizer.gui.components.thumblist;


public class FullWidthGrid implements Grid
{
    
    private int _columnCount = 2;
    private int _cellWidth = 0;
    private int _cellHeight = 0;
    
    public FullWidthGrid()
    {
    }
    
    public int getColumnCount()
    {
        return _columnCount;
    }

    public void setColumnCount(int columnCount)
    {
        _columnCount = columnCount;
    }

    @Override
    public int getCellWidth()
    {
        return _cellWidth;
    }

    @Override
    public int getCellHeight()
    {
        return _cellHeight;
    }

    @Override
    public void updateCellSize(int componentWidth, int componentHeight)
    {
        _cellWidth = (int) (1.0 * componentWidth / _columnCount);
    }

}
