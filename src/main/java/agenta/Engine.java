package agenta;

import java.util.ArrayList;
import java.util.List;

/**
 * Здесь идёт процесс обработки. Движку при создании передаются входные параметры
 * с помощью объекта InputParameters. Далее к нему присоединяются необходимые
 * Viewer'ы. На каждом шаге работы приложения вызывается метод движка step(), в котором
 * обрабатывается движение юнитов. При наличии изменений движок уведомляет об этом
 * Viewer'ов, а те запрашивают необходимую информацию с помощью get-методов и отображаются
 * в нужном для себя виде.
*/

public final class Engine
{
    private final InputParameters ip;
    private final Map map;
    private final List<Unit> units = new ArrayList<>();
    private final int[] unitCounter = new int[2];
    private final Commander[] commanders = new Commander[2];
    private final List<Viewer> viewers = new ArrayList<>();
    private final SingleRandom generator;
    private int winner = -1;

    public Engine(InputParameters ip, SingleRandom generator)
    {
        this.ip = ip;
        this.generator = generator;
        map = new Map(generator);
    }

    // Позволяет подключить к движку вид. Возвращается идентификатор вида
    public int addViewer(Viewer viewer)
    {
        viewers.add(viewer);
        return viewers.size();
    }

    public int getWinner()
    {
        return winner;
    }

    public void init()
    {
        map.renderTrees();
        // Расставляем юнитов на карте
        commanders[0] = ip.getCommander(0);
        commanders[1] = ip.getCommander(1);

        for (UnitType unitType: DefaultUnits.build())
        {
            for (int player = 0; player < 2; player++)
            {
                final int count = ip.getUnit(player, unitType.getName().toLowerCase());
                for (int u = 0; u < count; u++)
                {
                    Unit unit = new Unit(unitType, player, map, generator);
                    unitCounter[player]++;
                    int x, y;
                    do
                    {
                        x = generator.nextInt(Map.SIZE);
                        y = generator.nextInt(Map.SIZE);
                    }
                    while (!map.canPlaceObject(x, y));
                    map.placeObject(unit, x, y);
                    units.add(unit);
                    commanders[player].submit(unit, true);
                }
            }
        }
    }

    // Один шаг работы
    public void step()
    {
        // Если хотя бы один из игроков не имеет юнитов, финиш
        if ((unitCounter[0] * unitCounter[1]) == 0)
        {
            return;
        }
        updateViewers();

        for (int i = 0; i < units.size(); i++)
        {
            Unit u = units.get(i);
            if (u.isAlive())
            {
                u.act();
            }
            else
            {
                unitCounter[u.getPlayer()]--;
                if (unitCounter[u.getPlayer()] == 0)
                {
                    winner = 1 - u.getPlayer();
                }
                units.remove(i);
                i--;
            }
        }
        commanders[0].act();
        commanders[1].act();
    }

    private void updateViewers()
    {
        for (Viewer viewer : viewers)
        {
            viewer.update(map);
        }
    }

}
