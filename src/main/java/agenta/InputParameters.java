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

public final class InputParameters
{
    private static final int MAX_UNITS_PER_TYPE = 20;
    private final boolean[] initialized;
    private final int[] player0;
    private final int[] player1;
    private final Commander[] commanders = new Commander[2];
    private int commanderCounter = 0;

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
        try
        {
            if ((player0 < 0) || (player1 < 0) || (player0 > MAX_UNITS_PER_TYPE) ||
                    (player1 > MAX_UNITS_PER_TYPE))
            {
                throw new ArrayIndexOutOfBoundsException();
            }
            if (!initialized[index])
            {
                this.player0[index] = player0;
                this.player1[index] = player1;
                initialized[index] = true;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
    }

    public Commander getCommander(int index)
    {
        if ((index >= 0) && (index < commanderCounter))
        {
            return commanders[index];
        }
        return null;
    }

    public int getUnit(int index, int player)
    {
        if ((index >= 0) && (index < initialized.length))
        {
            switch (player)
            {
            case 0:
                return player0[index];
            case 1:
                return player1[index];
            default:
                return 0;
            }
        }
        return 0;
    }
}
