package agenta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameMap
{
    private final int sizeX;
    private final int sizeY;
    private final SingleRandom generator;
    private final MapCell[][] cells;
    private final Set<MapObject> objects = new HashSet<>();

    public GameMap(SingleRandom generator, int sizeX, int sizeY)
    {
        this.generator = generator;
        this.sizeX = 18;
        this.sizeY = 18;
        cells = new MapCell[this.sizeX][this.sizeY];
        for (int x = 0; x < this.sizeX; x++)
        {
            for (int y = 0; y < this.sizeY; y++)
            {
                cells[x][y] = new MapCell();
            }
        }
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

    public void placeWherePossible(Unit unit)
    {
        int x, y;
        do
        {
            x = this.generator.nextInt(sizeX);
            y = this.generator.nextInt(sizeY);
        }
        while (!canPlaceObject(x, y));
        objects.add(unit);
        placeObject(unit, x, y);
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
        objects.remove(obj);
        cells[obj.x][obj.y].setObject(null);
    }

    public void renderTrees()
    {
        int i = 20;
        int x, y;
        while (i > 0)
        {
            x = generator.nextInt(sizeX);
            y = generator.nextInt(sizeY);
            if (cells[x][y].getType() == MapCellType.GRASS)
            {
                cells[x][y].setType(MapCellType.TREE);
                i--;
            }
        }
    }

    public String toString()
    {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < sizeX; i++)
        {
            for (int j = 0; j < sizeY; j++)
            {
                switch (cells[i][j].getType())
                {
                case GRASS:
                    s.append('.');
                    break;
                case TREE:
                    s.append('#');
                    break;
                }
            }
            s.append('\n');
        }
        return s.toString();
    }
}
