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

    public List<Map<String, Object>> act(String externalState, List<Unit> visibleObjects)
    {
        List<Map<String, Object>> unitActions = new ArrayList<>();
        if (!":attack".equals(externalState))
        {
            throw new RuntimeException("Unknown action " + externalState);
        }

        List<Unit> enemies = filterEnemies(visibleObjects);
        List<Unit> neighbours = filterInAttackRadius(enemies);
        if (!neighbours.isEmpty())
        {
            unitActions.add(attack(neighbours));
        }
        else
        {
            neighbours = enemies;
            if (!neighbours.isEmpty())
            {
                Unit target = selectTargetPerk.apply(neighbours);
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
        }
        return unitActions;
    }

    private static Map<String, Object> move(int dx, int dy)
    {
        return Map.of("type", "move", "dx", dx, "dy", dy);
    }

    private Map<String, Object> attack(List<Unit> targets)
    {
        Unit chosen = selectTargetPerk.apply(targets);
        String ids = targets.stream()
                .map(unit -> unit.id)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return Map.of("type", "attack", "target", chosen.id, "ids", ids);
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

    private List<Unit> filterEnemies(List<Unit> units)
    {
        return units.stream().filter(u -> u.getPlayer() != player).collect(Collectors.toList());
    }

    private List<Unit> filterInAttackRadius(List<Unit> objs)
    {
        float limit = type.getRange() * type.getRange();
        return objs.stream()
                .filter(o -> (o.x - x) * (o.x - x) + (o.y - y) * (o.y - y) <= limit)
                .collect(Collectors.toList());
    }
}
