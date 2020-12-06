package agenta;

import java.text.MessageFormat;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Attack implements Action
{
    private final Unit self;
    private final Unit other;

    public Attack(Unit self, Unit other)
    {
        this.self = self;
        this.other = other;
    }

    @Override
    public Unit getActor()
    {
        return self;
    }

    @Override
    public void act()
    {
        int damage = self.doAttack();
        if (damage > 0)
        {
            System.out.println(MessageFormat.format("{0} strikes {1} with {2}", self.toString(), other.toString(),
                    damage));
        }
        other.sufferDamage(damage);
        if (!other.isAlive())
        {
            self.kills += 1;
        }
    }
}
