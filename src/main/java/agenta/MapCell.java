package agenta;

public class MapCell
{
    private final MapCellType type;
    private MapObject object = null;

    public MapCell()
    {
        this(MapCellType.GRASS);
    }

    public MapCell(MapCellType type)
    {
        this.type = type;
    }

    public MapObject getObject()
    {
        return object;
    }

    public MapCellType getType()
    {
        return type;
    }

    public void setObject(MapObject obj)
    {
        object = obj;
    }
}
