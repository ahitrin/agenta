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

    public Unit getGroundObject(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= sizeX) || (y >= sizeY))
        {
            return null;
        }
        return cells[x][y].getObject();
    }
}
