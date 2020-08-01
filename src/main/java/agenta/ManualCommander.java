package agenta;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import agenta.BaseCommander;
import agenta.Commander;
import agenta.Unit;
import agenta.UnitCommand;
import agenta.UnitState;

public class ManualCommander extends BaseCommander
{
    private UnitState state = UnitState.ATTACK;
    private int priority = 1;

    public void act()
    {
        for (Commander c : subordinates)
        {
            if (c instanceof Unit)
            {
                UnitCommand uc = new UnitCommand(((Unit)c).getType(), state, priority);
                c.obtain(uc);
            }
        }
    }

    public void set(String value)
    {
        String s = value.toLowerCase();
        int pri;
        StringTokenizer st = new StringTokenizer(s);
        try
        {
            s = st.nextToken();
            pri = Integer.parseInt(st.nextToken());
        }
        catch (NoSuchElementException nse)
        {
            return;
        }
        switch (s)
        {
        case "stand":
            state = UnitState.STAND;
            break;
        case "attack":
            state = UnitState.ATTACK;
            break;
        case "escape":
            state = UnitState.ESCAPE;
            break;
        }
        priority = pri;
    }
}
