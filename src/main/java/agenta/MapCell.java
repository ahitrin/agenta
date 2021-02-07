package agenta;

public class MapCell
{
    private final MapCellType type;
    private Unit object = null;

    public MapCell()
    {
        this(MapCellType.GRASS);
    }

    public MapCell(MapCellType type)
    {
        this.type = type;
    }

    public Unit getObject()
    {
        return object;
    }

    public MapCellType getType()
    {
        return type;
    }

    public void setObject(Unit obj)
    {
        object = obj;
    }
}
