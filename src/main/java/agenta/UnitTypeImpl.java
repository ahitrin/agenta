package agenta;

import java.io.Serializable;

public class UnitTypeImpl implements Serializable
{
    private String name = "";
    private int hitPoints = 0;
    private int baseAttack = 0;
    private int randAttack = 0;
    private int attackSpeed = 0;
    private float range = 0f;
    private int visibility = 0;
    private int speed = 0;
    private int[] healthLimit = new int[MAX_PRIORITY];

    UnitTypeImpl()
    {
    }

    public UnitTypeImpl(UnitTypeImpl ut)
    {
        this();
        setName(ut.getName());
        setBaseAttack(ut.getBaseAttack());
        setRandAttack(ut.getRandAttack());
        setRange(ut.getRange());
        setAttackSpeed(ut.getAttackSpeed());
        setVisibility(ut.getVisibility());
        setSpeed(ut.getSpeed());
        setHitPoints(ut.getHitPoints());
    }

    public int getAttackSpeed()
    {
        return attackSpeed;
    }

    public int getBaseAttack()
    {
        return baseAttack;
    }

    public int getHealthLimit(int index)
    {
        if ((index < healthLimit.length) && (index >= 0))
        {
            return healthLimit[index];
        }
        return -1;
    }

    public int getHitPoints()
    {
        return hitPoints;
    }

    public String getName()
    {
        return name;
    }

    public int getRandAttack()
    {
        return randAttack;
    }

    public float getRange()
    {
        return range;
    }

    public int getSpeed()
    {
        return speed;
    }

    public int getVisibility()
    {
        return visibility;
    }

    public String toString()
    {
        return name;
    }

    public void setAttackSpeed(int value)
    {
        attackSpeed = value;
    }

    public void setBaseAttack(int value)
    {
        baseAttack = value;
    }

    public void setHitPoints(int value)
    {
        hitPoints = value;
        for (int i = 0; i < healthLimit.length; i++)
        {
            healthLimit[i] = (4 - i) * hitPoints / 5;
        }
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRandAttack(int value)
    {
        randAttack = value;
    }

    public void setRange(float value)
    {
        range = value;
    }

    public void setSpeed(int value)
    {
        speed = value;
    }

    public void setVisibility(int value)
    {
        visibility = value;
    }
}
