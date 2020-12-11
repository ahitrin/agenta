package agenta;

import java.util.Map;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Action
{
    public static Action attack(Unit self, Unit other)
    {
        return new Action(self, Map.of("type", "attack", "target", other));
    }

    public static Action move(Unit self, int dx, int dy)
    {
        return new Action(self, Map.of("type", "move", "dx", dx, "dy", dy));
    }

    private final Unit self;
    protected final Map<String, Object> data;

    public Action(Unit self, Map<String, Object> data)
    {
        this.self = self;
        this.data = data;
    }

    public Unit getActor()
    {
        return self;
    }

    public Map<String, Object> getData()
    {
        return data;
    }
}
