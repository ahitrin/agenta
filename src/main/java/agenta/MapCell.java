package agenta;

class MapCell
{
    private MapCellType type = MapCellType.GRASS;
    private MapObject groundObject = null;

    public MapObject getGroundObject()
    {
        return groundObject;
    }

    public MapCellType getType()
    {
        return type;
    }

    public void setGroundObject(MapObject obj)
    {
        groundObject = obj;
    }

    public void setType(MapCellType type)
    {
        this.type = type;
    }
}
