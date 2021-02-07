package agenta;

public class GameMap
{
    public final int sizeX;
    public final int sizeY;
    public final MapCell[][] cells;

    public GameMap(MapCell[][] cells)
    {
        this.sizeX = cells.length;
        this.sizeY = cells[0].length;
        this.cells = cells;
    }
}
