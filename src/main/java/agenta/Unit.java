package agenta;

public class Unit
{
    protected int x;
    protected int y;
    public final UnitType type;
    public int currentHitPoints;

    public Unit(UnitType type)
    {
        this.type = type;
        currentHitPoints = type.getHitPoints();
    }

    public final int getX()
    {
        return x;
    }

    public final int getY()
    {
        return y;
    }

    public void moveTo(int newX, int newY)
    {
        x = newX;
        y = newY;
    }
}
