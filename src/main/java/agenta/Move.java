package agenta;

import java.util.Map;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Move extends Action
{
    public Move(Unit self, GameMap gameMap, int dx, int dy)
    {
        super(self, Map.of("type", "move", "dx", dx, "dy", dy));
    }
}
