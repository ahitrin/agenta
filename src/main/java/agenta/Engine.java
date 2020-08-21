package agenta;

import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class Engine
{
    private final InputParameters ip;
    private final GameMap map;
    private final List<Unit> units = new ArrayList<>();
    private final Commander[] commanders = new Commander[2];
    private final List<Viewer> viewers = new ArrayList<>();
    private final SingleRandom generator;
    private int winner = -1;

    public Engine(InputParameters ip, SingleRandom generator)
    {
        this.ip = ip;
        this.generator = generator;
        map = new GameMap(generator);
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

    public void init(List<UnitType> unitTypes)
    {
        map.renderTrees();
        commanders[0] = ip.getCommander(0);
        commanders[1] = ip.getCommander(1);

        for (UnitType unitType: unitTypes)
        {
            for (int player = 0; player < 2; player++)
            {
                final int count = ip.getUnit(player, unitType.getName().toLowerCase());
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
                    commanders[player].submit(unit, true);
                }
            }
        }
    }

    public void step()
    {
        removeDeadUnits();
        checkForWinner();
        updateViewers();
        if (winner != -1) {
            return;
        }
        runUnitActions();
        runCommanderActions();
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

    private void runCommanderActions()
    {
        ActionListener emptyListener = a -> {};
        commanders[0].act(emptyListener);
        commanders[1].act(emptyListener);
    }

}
