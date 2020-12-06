package agenta;

import java.util.Map;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Action
{
    protected final Unit self;
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
