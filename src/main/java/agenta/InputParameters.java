package agenta;

import java.util.HashMap;
import java.util.Map;

public final class InputParameters
{
    private final Map<String, Integer> player0Units = new HashMap<>();
    private final Map<String, Integer> player1Units = new HashMap<>();

    public void addUnit(String name, int player0, int player1)
    {
        player0Units.put(name, player0);
        player1Units.put(name, player1);
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
