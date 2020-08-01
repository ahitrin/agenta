package agenta;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author Andrey Hitrin
 * @since 01.08.2020
 */
class UnitTest {
    @Test
    void unitIsAliveByDefault() {
        UnitType type = new UnitType();
        type.setSpeed(3);
        type.setAttackSpeed(3);
        type.setHitPoints(10);
        int player = 13;
        Map map = new Map();
        SingleRandom random = SingleRandom.get();
        Unit unit = new Unit(type, player, map, random);
        assertTrue(unit.isAlive());
    }
}
