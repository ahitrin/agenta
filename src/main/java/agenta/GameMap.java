package agenta;

import java.util.ArrayList;
import java.util.List;

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

    public boolean canPlaceObject(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= sizeX) || (y >= sizeY))
        {
            return false;
        }
        return (cells[x][y].getType() == MapCellType.GRASS) &&
                (cells[x][y].getObject() == null);
    }

    public boolean tryMove(Unit actor, int dx, int dy)
    {
        boolean moved = canPlaceObject(actor.x + dx, actor.y + dy);
        if (moved)
        {
            cells[actor.x][actor.y].setObject(null);
            placeObject(actor, actor.x + dx, actor.y + dy);
        }
        return moved;
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

    public List<Unit> getObjectsInRadius(Unit o, float r)
    {
        List<Unit> objects1 = new ArrayList<>();
        Unit o1;

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

    public void placeObject(Unit obj, int x, int y)
    {
        if (canPlaceObject(x, y))
        {
            cells[x][y].setObject(obj);
            obj.moveTo(x, y);
        }
    }

    public void removeObject(Unit obj)
    {
        cells[obj.x][obj.y].setObject(null);
    }
}
