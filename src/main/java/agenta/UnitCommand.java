package agenta;

public class UnitCommand implements Command
{
    private final UnitType type;
    private final UnitState state;
    private final int priority;

    public UnitCommand(UnitType type, UnitState state, int priority)
    {
        this.type = type;
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

    public UnitType getType()
    {
        return type;
    }

    public String toString()
    {
        return type.toString() + ": " + state.name() + " with " + priority;
    }
}
