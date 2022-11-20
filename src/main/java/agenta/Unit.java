package agenta;

public class Unit
{
    protected int x;
    protected int y;
    public final UnitType type;
    private final int id;
    private final int player;
    public int currentHitPoints;

    public Unit(UnitType type, int id, int player)
    {
        this.type = type;
        this.id = id;
        this.player = player;
        currentHitPoints = type.getHitPoints();
    }

    public int getPlayer()
    {
        return player;
    }

    public final int getX()
    {
        return x;
    }

    public final int getY()
    {
        return y;
    }

    public final int getId() {
        return id;
    }

    public void moveTo(int newX, int newY)
    {
        x = newX;
        y = newY;
    }
}
