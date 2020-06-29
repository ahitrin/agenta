package agenta;

class MapCell {
    private MapCellType type = MapCellType.GRASS;
    private MapObject airObject = null;
    private MapObject groundObject = null;

    public MapCellType getType(){ return type; }
    public MapObject getAirObject(){ return airObject; }
    public MapObject getGroundObject(){ return groundObject; }

    public void setType(MapCellType type){ this.type = type; }
    public void setAirObject(MapObject obj){ airObject = obj; }
    public void setGroundObject(MapObject obj){ groundObject = obj; }
}
