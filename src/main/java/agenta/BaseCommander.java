package agenta;

import java.util.ArrayList;
import java.util.List;


public class BaseCommander implements Commander
{
    private final List<Commander> subordinates = new ArrayList<>();
    private final List<Command> queue = new ArrayList<>();

    public void act(ActionListener actionListener)
    {
        while (!queue.isEmpty())
        {
            System.out.println(queue.remove(0).toString());
        }
    }

    public void obtain(Command com)
    {
        queue.add(com);
    }

}
