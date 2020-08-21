package agenta;

import java.util.ArrayList;
import java.util.List;

public class Map
{
    public static final int SIZE = 18;
    private final SingleRandom generator;
    private final MapCell[][] cells;

    public Map(SingleRandom generator)
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

    /**
     * Проверяет, можно ли поставить данный объект в даную точку на карте.
     * @param x
     * @param y
     * @return
     */
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

    /**
     * Возвращает список всех объектов на карте, находящихся в пределах указанного расстояния
     * от указанной точки
     * @param x Координата х
     * @param y Координата у
     * @param r Радиус
     * @return Список объектов
     */
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

    public void placeObject(MapObject obj, int x, int y)
    {
        if (canPlaceObject(x, y))
        {
            cells[x][y].setObject(obj);
            obj.moveTo(x, y);
        }
    }

    public void removeObject(MapObject obj, int x, int y)
    {
        if (cells[x][y].getObject() == obj)
        {
            cells[x][y].setObject(null);
        }
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
