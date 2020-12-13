package agenta;

import java.util.ArrayList;
import java.util.List;

public class GameMap
{
    private final int sizeX;
    private final int sizeY;
    private final MapCell[][] cells;

    public GameMap(MapCell[][] cells)
    {
        this.sizeX = cells.length;
        this.sizeY = cells[0].length;
        this.cells = cells;
    }

    public int getSizeX()
    {
        return sizeX;
    }

    public int getSizeY()
    {
        return sizeY;
    }

    private boolean canPlaceObject(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= sizeX) || (y >= sizeY))
        {
            return false;
        }
        return (cells[x][y].getType() == MapCellType.GRASS) &&
                (cells[x][y].getObject() == null);
    }

    boolean tryMove(MapObject actor, int dx, int dy)
    {
        boolean moved = canPlaceObject(actor.x + dx, actor.y + dy);
        if (moved)
        {
            cells[actor.x][actor.y].setObject(null);
            placeObject(actor, actor.x + dx, actor.y + dy);
        }
        return moved;
    }

    public MapCellType getCellType(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= sizeX) || (y >= sizeY))
        {
            return MapCellType.TREE;
        }
        return cells[x][y].getType();
    }

    public MapObject getGroundObject(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= sizeX) || (y >= sizeY))
        {
            return null;
        }
        return cells[x][y].getObject();
    }

    public List<MapObject> getObjectsInRadius(MapObject o, float r)
    {
        List<MapObject> objects1 = new ArrayList<>();
        MapObject o1;

        int limit = Math.round(r);
        for (int i = -limit; i <= limit; i++)
        {
            for (int j = -limit; j <= limit; j++)
            {
                int sum = i * i + j * j;
                if ((sum > r * r) || (sum == 0))
                {
                    continue;
                }
                if ((o1 = getGroundObject(o.x + i, o.y + j)) != null)
                {
                    objects1.add(o1);
                }
            }
        }
        return objects1;
    }

    private void placeObject(MapObject obj, int x, int y)
    {
        if (canPlaceObject(x, y))
        {
            cells[x][y].setObject(obj);
            obj.moveTo(x, y);
        }
    }

    public void removeObject(MapObject obj)
    {
        cells[obj.x][obj.y].setObject(null);
    }
}
