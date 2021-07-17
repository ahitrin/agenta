package agenta;

public class MapCell
{
    private final MapCellType type;

    public MapCell()
    {
        this(MapCellType.GRASS);
    }

    public MapCell(MapCellType type)
    {
        this.type = type;
    }

    public MapCellType getType()
    {
        return type;
    }

}
