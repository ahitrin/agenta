package agenta;

class MapCell
{
    private MapCellType type = MapCellType.GRASS;
    private MapObject object = null;

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

    public void setType(MapCellType type)
    {
        this.type = type;
    }
}
