package agenta;

import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Engine
{
    private final GameMap map;
    private final List<Unit> units;
    private final List<Viewer> viewers = new ArrayList<>();
    private int winner = -1;
    private long ticks = 0;

    public Engine(GameMap gameMap, List<Unit> units) {
        this.map = gameMap;
        this.units = units;
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

    public static List<Unit> init(SingleRandom generator, GameMap gameMap, Map<String, Long> player0Units,
            Map<String, Long> player1Units, List<UnitType> unitTypes)
    {
        List<Unit> createdUnits = new ArrayList<>();
        for (UnitType unitType: unitTypes)
        {
            for (int player = 0; player < 2; player++)
            {
                Map<String, Long> playerUnits = player == 0 ? player0Units : player1Units;
                final long count = playerUnits.getOrDefault(unitType.getName().toLowerCase(), 0L);
                for (int u = 0; u < count; u++)
                {
                    Unit unit = new Unit(unitType, player, gameMap, generator);
                    gameMap.placeWherePossible(unit);
                    createdUnits.add(unit);
                }
            }
        }
        return createdUnits;
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
