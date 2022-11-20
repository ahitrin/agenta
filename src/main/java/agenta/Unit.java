package agenta;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Unit
{
    protected int x;
    protected int y;
    public final UnitType type;
    private final int id;
    private final int player;
    public int currentHitPoints;
    private final Random random;
    private final Function<List<Unit>, Unit> selectTargetPerk;

    public Unit(UnitType type, int id, int player, Random random, Function<List<Unit>, Unit> selectTargetPerk)
    {
        this.type = type;
        this.id = id;
        this.player = player;
        this.random = random;
        this.selectTargetPerk = selectTargetPerk;
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
