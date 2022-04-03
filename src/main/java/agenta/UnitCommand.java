package agenta;

public class UnitCommand
{
    private final UnitState state;
    private final int priority;

    public UnitCommand(UnitState state, int priority)
    {
        this.state = state;
        this.priority = Math.min(Math.max(priority, UnitType.MIN_PRIORITY), UnitType.MAX_PRIORITY);
    }

    public int getPriority()
    {
        return priority;
    }

    public UnitState getState()
    {
        return state;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof UnitCommand)) {
            return false;
        }
        UnitCommand c = (UnitCommand) obj;
        return priority == c.priority && state.equals(c.state);
    }

    public String toString()
    {
        return state.name() + " with " + priority;
    }
}
