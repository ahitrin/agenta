package agenta;

class MapCell
{
    private MapCellType type = MapCellType.GRASS;
    private MapObject airObject = null;
    private MapObject groundObject = null;

    public MapObject getAirObject()
    {
        return airObject;
    }

    public MapObject getGroundObject()
    {
        return groundObject;
    }

    public MapCellType getType()
    {
        return type;
    }

    public void setAirObject(MapObject obj)
    {
        airObject = obj;
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
