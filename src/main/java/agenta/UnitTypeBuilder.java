package agenta;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UnitTypeBuilder
{
    public static final int MIN_PRIORITY = 1;
    public static final int MAX_PRIORITY = 5;

    private String name = "";
    private int hitPoints = 0;
    private int baseAttack = 0;
    private int randAttack = 0;
    private int attackSpeed = 0;
    private float range = 0f;
    private int visibility = 0;
    private int speed = 0;
    private int[] healthLimit = new int[MAX_PRIORITY];

    public UnitTypeBuilder setAttackSpeed(int value)
    {
        attackSpeed = value;
        return this;
    }

    public UnitTypeBuilder setBaseAttack(int value)
    {
        baseAttack = value;
        return this;
    }

    public UnitTypeBuilder setHitPoints(int value)
    {
        hitPoints = value;
        for (int i = 0; i < healthLimit.length; i++)
        {
            healthLimit[i] = (4 - i) * hitPoints / 5;
        }
        return this;
    }

    public UnitTypeBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    public UnitTypeBuilder setRandAttack(int value)
    {
        randAttack = value;
        return this;
    }

    public UnitTypeBuilder setRange(float value)
    {
        range = value;
        return this;
    }

    public UnitTypeBuilder setSpeed(int value)
    {
        speed = value;
        return this;
    }

    public UnitTypeBuilder setVisibility(int value)
    {
        visibility = value;
        return this;
    }
}
