package agenta;

import java.text.MessageFormat;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Attack extends Action
{
    private final Unit other;

    public Attack(Unit self, Unit other)
    {
        super(self);
        this.other = other;
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
