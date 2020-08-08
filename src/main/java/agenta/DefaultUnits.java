package agenta;

import java.util.List;

/**
 * @author Andrey Hitrin
 * @since 08.08.2020
 */
public final class DefaultUnits
{
    private DefaultUnits()
    {
    }

    public static List<UnitType> build()
    {
        UnitType footman = new UnitTypeBuilder()
                .setName("Footman")
                .setBaseAttack(5)
                .setRandAttack(6)
                .setRange(1.45f)
                .setAttackSpeed(25)
                .setVisibility(7)
                .setSpeed(40)
                .setHitPoints(70)
                .build();

        UnitType archer = new UnitTypeBuilder()
                .setName("Archer")
                .setBaseAttack(3)
                .setRandAttack(5)
                .setRange(5.5f)
                .setAttackSpeed(40)
                .setVisibility(7)
                .setSpeed(40)
                .setHitPoints(55)
                .build();

        UnitType knight = new UnitTypeBuilder()
                .setName("Knight")
                .setBaseAttack(9)
                .setRandAttack(7)
                .setRange(1.45f)
                .setAttackSpeed(40)
                .setVisibility(7)
                .setSpeed(25)
                .setHitPoints(110)
                .build();

        return List.of(footman, archer, knight);
    }
}
