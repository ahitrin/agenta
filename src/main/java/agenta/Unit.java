package agenta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javafaker.Faker;

public class Unit
{
    private static final Logger LOG = LoggerFactory.getLogger(Unit.class);

    protected int x;
    protected int y;
    public final UnitType type;
    private final int id;
    private UnitState state;
    private final int player;
    public int speedCounter;
    private int attackCounter;
    public int healthCounter;
    public int currentHitPoints;
    private final String name;
    private final Random random;
    private final Function<List<Unit>, Unit> selectTargetPerk;

    public Unit(UnitType type, int id, int player, int speedCounter, int attackCounter, int healthCounter,
            Random random, Function<List<Unit>, Unit> selectTargetPerk)
    {
        this.type = type;
        this.id = id;
        this.player = player;
        this.random = random;
        this.selectTargetPerk = selectTargetPerk;
        this.speedCounter = speedCounter;
        this.attackCounter = attackCounter;
        this.healthCounter = healthCounter;
        currentHitPoints = type.getHitPoints();
        state = UnitState.ATTACK;
        name = String.format("%s %s%d", new Faker().name().firstName(), type, player);
    }

    public void doThink(List<Unit> visibleObjects) {
        int escapeThreshold = type.getHitPoints() / 5;
        int attackThreshold = type.getHitPoints() / 4;
        UnitState newState = state;

        if (currentHitPoints < escapeThreshold) {
            newState = UnitState.ESCAPE;
        }
        else if (currentHitPoints >= attackThreshold) {
            newState = UnitState.ATTACK;
        }
        if (newState != state) {
            LOG.debug("{} will {}", this, newState);
            state = newState;
        }
    }

    public List<Map<String, Object>> act(List<Unit> visibleObjects)
    {
        if (attackCounter > 0)
        {
            attackCounter--;
        }
        if (speedCounter > 0)
        {
            speedCounter--;
        }
        if ((attackCounter > 0) && (speedCounter > 0))
        {
            return List.of();
        }

        List<Unit> neighbours;
        List<Map<String, Object>> unitActions = new ArrayList<>();
        switch (state)
        {
        case STAND:
            neighbours = filterEnemies(filterInAttackRadius(visibleObjects));
            if (!neighbours.isEmpty())
            {
                List<Unit> currentNeighbours = new ArrayList<>(neighbours);
                unitActions.add(attack(selectTargetPerk.apply(currentNeighbours)));
            }
            break;
        case ATTACK:
            neighbours = filterEnemies(filterInAttackRadius(visibleObjects));
            if (!neighbours.isEmpty())
            {
                List<Unit> currentNeighbours = new ArrayList<>(neighbours);
                unitActions.add(attack(selectTargetPerk.apply(currentNeighbours)));
            }
            else
            {
                neighbours = filterEnemies(visibleObjects);
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
            break;
        case ESCAPE:
            neighbours = filterEnemies(visibleObjects);
            if (!neighbours.isEmpty())
            {
                double dx = 0, dy = 0, r;
                for (Unit uu : neighbours)
                {
                    r = Math.sqrt((uu.x - x) * (uu.x - x) + (uu.y - y) * (uu.y - y));
                    dx += (x - uu.x) / r;
                    dy += (y - uu.y) / r;
                }
                int idx = (dx > 0) ? 1 : (dx < 0) ? -1 : 0;
                int idy = (dy > 0) ? 1 : (dy < 0) ? -1 : 0;
                unitActions.add(move(idx, idy));
            }
            break;
        }
        return unitActions;
    }

    private static Map<String, Object> move(int dx, int dy)
    {
        return Map.of("type", "move", "dx", dx, "dy", dy);
    }

    private static Map<String, Object> attack(Unit other)
    {
        return Map.of("type", "attack", "target", other.id);
    }

    public int getPlayer()
    {
        return player;
    }

    public UnitType getType()
    {
        return type;
    }

    public final int getX()
    {
        return x;
    }

    public final int getY()
    {
        return y;
    }

    public boolean isAlive()
    {
        return (currentHitPoints > 0);
    }

    @Override
    public String toString()
    {
        return String.format("%s #%d (%d HP) at [%d, %d]", name, id, currentHitPoints, x, y);
    }

    public int doAttack()
    {
        if (attackCounter > 0)
        {
            return 0;
        }
        attackCounter = type.getAttackSpeed();
        return random.nextInt(type.getRandAttack()) + type.getBaseAttack();
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
