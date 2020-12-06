package agenta;

import java.util.Map;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Attack extends Action
{
    public Attack(Unit self, Unit other)
    {
        super(self, Map.of("type", "attack", "target", other));
    }
}
