package agenta;

/*
Класс-контейнер для передачи стартовых параметров программе. С помощью этого класса
мы можем разделить способ получения стартовых параметров и логику работы программы.
В качестве параметров выступают:
  - тип расположения юнитов - по линиям или случайным образом
  - количество юнитов каждого типа у каждого игрока
  - ссылки на объекты класса Commander, отвечающие за управление юнитами
*/

import java.util.Arrays;
import java.util.HashMap;

public final class InputParameters
{
    private static final int MAX_UNITS_PER_TYPE = 20;
    private final boolean[] initialized;
    private final int[] player0;
    private final int[] player1;
    private final Commander[] commanders = new Commander[2];
    private int commanderCounter = 0;
    private java.util.Map<String, Integer> player0Units = new HashMap<>();
    private java.util.Map<String, Integer> player1Units = new HashMap<>();

    public InputParameters(int size)
    {
        initialized = new boolean[size];
        player0 = new int[size];
        player1 = new int[size];
        commanders[0] = null;
        commanders[1] = null;
        Arrays.fill(initialized, false);
    }

    public void addCommander(Commander commander)
    {
        if (commanderCounter >= 2)
        {
            return;
        }
        commanders[commanderCounter] = commander;
        commanderCounter++;
    }

    public void addUnit(int index, int player0, int player1)
    {
        if (!initialized[index])
        {
            this.player0[index] = player0;
            this.player1[index] = player1;
            initialized[index] = true;
        }
    }

    public void addUnit(String name, int player0, int player1)
    {
        player0Units.put(name, player0);
        player1Units.put(name, player1);
    }

    public Commander getCommander(int index)
    {
        if ((index >= 0) && (index < commanderCounter))
        {
            return commanders[index];
        }
        return null;
    }

    public int getUnit(int player, String name) {
        if (player == 0)
        {
            return player0Units.getOrDefault(name, 0);
        }
        else if (player == 1)
        {
            return player1Units.getOrDefault(name, 0);
        }
        throw new IllegalArgumentException("Player must be equal 0 or 1, not " + player);
    }
}
