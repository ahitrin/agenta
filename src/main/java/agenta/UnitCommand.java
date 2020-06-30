package agenta;

public class UnitCommand implements Command
{
    private UnitType type;
    private UnitState state;
    private int priority;

    public UnitCommand(UnitType type, UnitState state, int priority)
    {
        this.type = type;
        this.state = state;
        this.priority = (priority < UnitType.MIN_PRIORITY) ? UnitType.MIN_PRIORITY :
                (priority > UnitType.MAX_PRIORITY) ? UnitType.MAX_PRIORITY : priority;
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
