package agenta;

/**
 * Интерфейс для всех объектов на карте.
 */
public abstract class MapObject
{
    protected int x, y;

    public final int getX()
    {
        return x;
    }

    public final int getY()
    {
        return y;
    }

    final void move(int dx, int dy)
    {
        x += dx;
        y += dy;
    }

    final void moveTo(int newX, int newY)
    {
        x = newX;
        y = newY;
    }
}
