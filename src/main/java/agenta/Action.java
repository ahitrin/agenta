package agenta;

import java.util.Map;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Action
{
    public static Action attack(Unit other)
    {
        return new Action(Map.of("type", "attack", "target", other));
    }

    public static Action move(int dx, int dy)
    {
        return new Action(Map.of("type", "move", "dx", dx, "dy", dy));
    }

    protected final Map<String, Object> data;

    public Action(Map<String, Object> data)
    {
        this.data = data;
    }

    public Map<String, Object> getData()
    {
        return data;
    }
}
