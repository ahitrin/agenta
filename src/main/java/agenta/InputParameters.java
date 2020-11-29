package agenta;

import java.util.HashMap;
import java.util.Map;

public final class InputParameters
{
    public final Map<String, Long> player0Units = new HashMap<>();
    public final Map<String, Long> player1Units = new HashMap<>();

    public void addUnit(String name, int player0, int player1)
    {
        player0Units.put(name, (long)player0);
        player1Units.put(name, (long)player1);
    }

    public long getUnit(int player, String name)
    {
        if (player == 0)
        {
            return player0Units.getOrDefault(name, 0L);
        }
        else if (player == 1)
        {
            return player1Units.getOrDefault(name, 0L);
        }
        throw new IllegalArgumentException("Player must be equal 0 or 1, not " + player);
    }
}
