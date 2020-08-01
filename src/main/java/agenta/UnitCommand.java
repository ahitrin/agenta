package agenta;

public class UnitCommand implements Command
{
    private final UnitState state;
    private final int priority;

    public UnitCommand(UnitState state, int priority)
    {
        this.state = state;
        this.priority = (priority < UnitType.MIN_PRIORITY) ? UnitType.MIN_PRIORITY :
                Math.min(priority, UnitType.MAX_PRIORITY);
    }

    public int getPriority()
    {
        return priority;
    }

    public UnitState getState()
    {
        return state;
    }

    public String toString()
    {
        return state.name() + " with " + priority;
    }
}
