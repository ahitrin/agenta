package agenta;

public class UnitTypeImpl implements UnitType
{
    private String name = "";
    private int hitPoints = 0;
    private int baseAttack = 0;
    private int randAttack = 0;
    private int attackSpeed = 0;
    private float range = 0f;
    private int visibility = 0;
    private int speed = 0;
    private final int[] healthLimit = new int[UnitType.MAX_PRIORITY];

    public UnitTypeImpl()
    {
    }

    public UnitTypeImpl(UnitType ut)
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

    @Override
    public int getAttackSpeed()
    {
        return attackSpeed;
    }

    @Override
    public int getBaseAttack()
    {
        return baseAttack;
    }

    @Override
    public int getHealthLimit(int index)
    {
        if ((index < healthLimit.length) && (index >= 0))
        {
            return healthLimit[index];
        }
        return -1;
    }

    @Override
    public int getHitPoints()
    {
        return hitPoints;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getRandAttack()
    {
        return randAttack;
    }

    @Override
    public float getRange()
    {
        return range;
    }

    @Override
    public int getSpeed()
    {
        return speed;
    }

    @Override
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
