package agenta;

public interface UnitType
{
    int MIN_PRIORITY = 1;
    int MAX_PRIORITY = 5;

    int getAttackSpeed();

    int getBaseAttack();

    int getHealthLimit(int index);

    int getHitPoints();

    String getName();

    int getRandAttack();

    float getRange();

    int getSpeed();

    int getVisibility();

    String toString();
}
