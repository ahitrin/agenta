package agenta;

import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Engine
{
    private final Map<String, Long> player0Units;
    private final Map<String, Long> player1Units;
    private final GameMap map;
    private final List<Unit> units = new ArrayList<>();
    private final List<Viewer> viewers = new ArrayList<>();
    private final SingleRandom generator;
    private int winner = -1;
    private long ticks = 0;

    public Engine(SingleRandom generator, GameMap gameMap, Map<String, Long> player0Units,
            Map<String, Long> player1Units) {
        this.player0Units = player0Units;
        this.player1Units = player1Units;
        this.generator = generator;
        map = gameMap;
    }

    public int addViewer(Viewer viewer)
    {
        viewers.add(viewer);
        return viewers.size();
    }

    public int getWinner()
    {
        return winner;
    }

    public long getTicks() {
        return ticks;
    }

    public void init(List<UnitType> unitTypes)
    {
        map.renderTrees();

        for (UnitType unitType: unitTypes)
        {
            for (int player = 0; player < 2; player++)
            {
                Map<String, Long> playerUnits = player == 0 ? player0Units : player1Units;
                final long count = playerUnits.getOrDefault(unitType.getName().toLowerCase(), 0L);
                for (int u = 0; u < count; u++)
                {
                    Unit unit = new Unit(unitType, player, map, generator);
                    int x, y;
                    do
                    {
                        x = generator.nextInt(GameMap.SIZE);
                        y = generator.nextInt(GameMap.SIZE);
                    }
                    while (!map.canPlaceObject(x, y));
                    map.placeObject(unit, x, y);
                    units.add(unit);
                }
            }
        }
    }

    public void step()
    {
        ticks++;
        removeDeadUnits();
        checkForWinner();
        updateViewers();
        if (winner != -1) {
            return;
        }
        runUnitActions();
    }

    private void removeDeadUnits()
    {
        new ArrayList<>(units).stream()
                .filter(not(Unit::isAlive))
                .forEach(units::remove);
    }

    private void checkForWinner()
    {
        java.util.Map<Integer, Long> unitsPerPlayer = units.stream()
                .collect(Collectors.groupingBy(Unit::getPlayer, Collectors.counting()));
        if (unitsPerPlayer.getOrDefault(0, 0L) == 0L) {
            winner = 1;
        } else if (unitsPerPlayer.getOrDefault(1, 0L) == 0L) {
            winner = 0;
        }
    }

    private void updateViewers()
    {
        for (Viewer viewer : viewers)
        {
            viewer.update(map);
        }
    }

    private void runUnitActions()
    {
        List<Action> allActions = new ArrayList<>();
        for (Unit unit : units)
        {
            List<Action> unitActions = new ArrayList<>();
            if (unit.isAlive())
            {
                unit.act(unitActions::add);
                unitActions.stream()
                        .findAny()
                        .ifPresent(allActions::add);
            }
        }
        new HashSet<>(allActions).forEach(Action::act);
    }
}
