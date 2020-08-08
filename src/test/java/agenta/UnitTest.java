package agenta;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author Andrey Hitrin
 * @since 01.08.2020
 */
class UnitTest
{
    @Test
    void unitIsAliveByDefault()
    {
        UnitType type = new UnitTypeBuilder()
                .setSpeed(3)
                .setAttackSpeed(3)
                .setHitPoints(10)
                .build();
        int player = 13;
        SingleRandom random = SingleRandom.get();
        Map map = new Map(random);
        Unit unit = new Unit(type, player, map, random, new SelectRandomWithMemory(random));
        assertTrue(unit.isAlive());
    }
}
