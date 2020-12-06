package agenta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class Engine
{
    private final GameMap map;
    private List<Unit> units;
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
        units = units.stream()
                .filter(Unit::isAlive)
                .collect(Collectors.toList());
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
        new HashSet<>(allActions).forEach(this::performAction);
    }

    private void performAction(Action a) {
        if (a.getActor().isAlive()) {
            a.act();
        }
    }
}
