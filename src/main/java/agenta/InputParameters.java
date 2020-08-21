package agenta;

import java.util.HashMap;

public final class InputParameters
{
    private final Commander[] commanders = new Commander[2];
    private int commanderCounter = 0;
    private final java.util.Map<String, Integer> player0Units = new HashMap<>();
    private final java.util.Map<String, Integer> player1Units = new HashMap<>();

    public InputParameters()
    {
        commanders[0] = null;
        commanders[1] = null;
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

    public int getUnit(int player, String name)
    {
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
