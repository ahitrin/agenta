package agenta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<Map<String, Object>> act(String externalState, List<Unit> enemies)
    {
        List<Map<String, Object>> unitActions = new ArrayList<>();
        if (!":attack".equals(externalState))
        {
            throw new RuntimeException("Unknown action " + externalState);
        }

        List<Unit> closestEnemies = filterInAttackRadius(enemies);
        if (!closestEnemies.isEmpty())
        {
            Unit chosen = selectTargetPerk.apply(closestEnemies);
            String ids = closestEnemies.stream()
                    .map(unit -> unit.id)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            unitActions.add(Map.of("type", "attack", "target", chosen.id, "ids", ids));
        }
        else if (!enemies.isEmpty())
        {
            Unit target = selectTargetPerk.apply(enemies);
            int dx = Integer.compare(target.x - x, 0);
            int dy = Integer.compare(target.y - y, 0);
            unitActions.add(move(dx, dy));
        }
        else
        {
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;
            unitActions.add(move(dx, dy));
        }
        return unitActions;
    }

    private static Map<String, Object> move(int dx, int dy)
    {
        return Map.of("type", "move", "dx", dx, "dy", dy);
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

    public void moveTo(int newX, int newY)
    {
        x = newX;
        y = newY;
    }

    private List<Unit> filterInAttackRadius(List<Unit> objs)
    {
        float limit = type.getRange() * type.getRange();
        return objs.stream()
                .filter(o -> (o.x - x) * (o.x - x) + (o.y - y) * (o.y - y) <= limit)
                .collect(Collectors.toList());
    }
}
