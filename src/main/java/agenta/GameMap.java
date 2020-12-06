package agenta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameMap
{
    public static final int SIZE = 18;
    private final SingleRandom generator;
    private final MapCell[][] cells;
    private final Set<MapObject> objects = new HashSet<>();

    public GameMap(SingleRandom generator)
    {
        this.generator = generator;
        cells = new MapCell[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++)
        {
            for (int y = 0; y < SIZE; y++)
            {
                cells[x][y] = new MapCell();
            }
        }
    }

    public boolean canPlaceObject(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= SIZE) || (y >= SIZE))
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
            x = this.generator.nextInt(SIZE);
            y = this.generator.nextInt(SIZE);
        }
        while (!canPlaceObject(x, y));
        objects.add(unit);
        placeObject(unit, x, y);
    }

    public void move(Unit actor, int dx, int dy)
    {
        cells[actor.x][actor.y].setObject(null);
        placeObject(actor, actor.x + dx, actor.y + dy);
    }

    public MapCellType getCellType(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= SIZE) || (y >= SIZE))
        {
            return MapCellType.TREE;
        }
        return cells[x][y].getType();
    }

    public MapObject getGroundObject(int x, int y)
    {
        if ((x < 0) || (y < 0) ||
                (x >= SIZE) || (y >= SIZE))
        {
            return null;
        }
        return cells[x][y].getObject();
    }

    public List<MapObject> getObjectsInRadius(int x, int y, float r)
    {
        List<MapObject> objects = new ArrayList<>();
        MapObject o;

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
                if ((o = getGroundObject(x + i, y + j)) != null)
                {
                    objects.add(o);
                }
            }
        }
        return objects;
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
            x = generator.nextInt(SIZE);
            y = generator.nextInt(SIZE);
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

        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
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
