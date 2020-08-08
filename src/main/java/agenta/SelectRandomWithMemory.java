package agenta;

import java.util.List;

/**
 * @author Andrey Hitrin
 * @since 08.08.2020
 */
public class SelectRandomWithMemory implements Selector<Unit>
{
    private Unit target = null;
    private final SingleRandom random;

    public SelectRandomWithMemory(SingleRandom random)
    {
        this.random = random;
    }

    @Override
    public Unit apply(List<Unit> units)
    {
        if (target != null)
        {
            if (!target.isAlive())
            {
                target = null;
            }
        }
        if ((target != null) && units.contains(target))
        {
            return target;
        }
        if (!units.isEmpty())
        {
            target = units.get(random.nextInt(units.size()));
        }
        else
        {
            target = null;
        }
        return target;
    }
}
