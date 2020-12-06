package agenta;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Action
{
    protected final Unit self;

    public Action(Unit self)
    {
        this.self = self;
    }

    public void act()
    {

    }

    public Unit getActor()
    {
        return self;
    }
}
