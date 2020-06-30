package agenta;

import java.util.Vector;

public class Map
{
    SingleRandom generator = SingleRandom.get();
    private final int SIZE = 18;
    private final int TREE_NUMBER = 20;
    private MapCell cells[][];

    public Map()
    {
        int x, y, i = 0;

        cells = new MapCell[SIZE][SIZE];
        for (x = 0; x < SIZE; x++)
            for (y = 0; y < SIZE; y++)
                cells[x][y] = new MapCell();

        do
        {
            x = generator.nextInt(SIZE);
            y = generator.nextInt(SIZE);
            if (cells[x][y].getType() == MapCellType.GRASS)
            {
                cells[x][y].setType(MapCellType.TREE);
                i++;
            }
        }
        while (i < TREE_NUMBER);
    }

    /**
     * Проверяет, можно ли поставить данный объект в даную точку на карте.
     * При этом учитывается, наземный или воздушный этот объект
     * @param obj Проверяемый объект
     * @param x
     * @param y
     * @return
     */
    public boolean canPlaceObject(MapObject obj, int x, int y)
    {
        try
        {
            switch (obj.getPlacementType())
            {
            case MapPlacementType.GROUND:
                return (cells[x][y].getType() == MapCellType.GRASS) &&
                        (cells[x][y].getGroundObject() == null);
            case MapPlacementType.AIR:
                return (cells[x][y].getAirObject() == null);
            default:
                return false;
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public MapObject getAirObject(int x, int y)
    {
        try
        {
            return cells[x][y].getAirObject();
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public MapCellType getCellType(int x, int y)
    {
        try
        {
            return cells[x][y].getType();
        }
        catch (Exception e)
        {
        }
        return MapCellType.TREE;
    }

    public MapObject getGroundObject(int x, int y)
    {
        try
        {
            return cells[x][y].getGroundObject();
        }
        catch (Exception e)
        {
        }
        return null;
    }

    /**
     * Возвращает список всех объектов на карте, находящихся в пределах указанного расстояния
     * от указанной точки
     * @param x Координата х
     * @param y Координата у
     * @param r Радиус
     * @return Список объектов
     */
    public MapObject[] getObjectsInRadius(int x, int y, float r)
    {
        Vector<MapObject> vec = new Vector<MapObject>();
        MapObject o;

        int limit = Math.round(r);
        for (int i = -limit; i <= limit; i++)
        {
            for (int j = -limit; j <= limit; j++)
            {
                int sum = i * i + j * j;
                if ((sum > r * r) || (sum == 0))
                    continue;
                if ((o = getAirObject(x + i, y + j)) != null)
                {
                    vec.add(o);
                }
                if ((o = getGroundObject(x + i, y + j)) != null)
                {
                    vec.add(o);
                }
            }
        }

        MapObject[] obj = new MapObject[vec.size()];
        return vec.toArray(obj);
    }

    public int getSIZE()
    {
        return SIZE;
    }

    public void placeObject(MapObject obj, int x, int y)
    {
        if (canPlaceObject(obj, x, y))
        {
            switch (obj.getPlacementType())
            {
            case MapPlacementType.GROUND:
                cells[x][y].setGroundObject(obj);
                break;
            case MapPlacementType.AIR:
                cells[x][y].setAirObject(obj);
                break;
            default:
                break;
            }
            obj.moveTo(x, y);
        }
    }

    public void removeObject(MapObject obj, int x, int y)
    {
        try
        {
            if (cells[x][y].getAirObject() == obj)
                cells[x][y].setAirObject(null);
            if (cells[x][y].getGroundObject() == obj)
                cells[x][y].setGroundObject(null);
        }
        catch (Exception e)
        {
        }
    }

    public String toString()
    {
        String s = "";

        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
            {
                switch (cells[i][j].getType())
                {
                case GRASS:
                    s = s + '.';
                    break;
                case TREE:
                    s = s + '#';
                    break;
                }
            }
            s = s + '\n';
        }
        return s;
    }
}
