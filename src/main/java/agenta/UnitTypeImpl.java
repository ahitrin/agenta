package agenta;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UnitType implements Serializable
{
    public static final int MIN_PRIORITY = 1;
    public static final int MAX_PRIORITY = 5;

    public static void main(String[] args) throws Exception
    {
        UnitType ut0 = new UnitType();
        UnitType ut1 = new UnitType();
        UnitType ut2 = new UnitType();

        ut0.setName("Footman");
        ut0.setBaseAttack(5);
        ut0.setRandAttack(6);
        ut0.setRange(1.45f);
        ut0.setAttackSpeed(25);
        ut0.setVisibility(7);
        ut0.setSpeed(40);
        ut0.setHitPoints(70);

        ut1.setName("Archer");
        ut1.setBaseAttack(3);
        ut1.setRandAttack(5);
        ut1.setRange(5.5f);
        ut1.setAttackSpeed(40);
        ut1.setVisibility(7);
        ut1.setSpeed(40);
        ut1.setHitPoints(55);

        ut2.setName("Knight");
        ut2.setBaseAttack(9);
        ut2.setRandAttack(7);
        ut2.setRange(1.45f);
        ut2.setAttackSpeed(40);
        ut2.setVisibility(7);
        ut2.setSpeed(25);
        ut2.setHitPoints(110);

        ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream("types.ser"));

        str.writeObject(ut0);
        str.writeObject(ut1);
        str.writeObject(ut2);
        str.flush();
        str.close();

        System.out.println("Done");
    }

    private String name = "";
    private int hitPoints = 0;
    private int baseAttack = 0;
    private int randAttack = 0;
    private int attackSpeed = 0;
    private float range = 0f;
    private int visibility = 0;
    private int speed = 0;
    private int[] healthLimit = null;

    UnitType()
    {
        healthLimit = new int[MAX_PRIORITY];
    }

    public UnitType(UnitType ut)
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

    void setAttackSpeed(int value)
    {
        attackSpeed = value;
    }

    private void setBaseAttack(int value)
    {
        baseAttack = value;
    }

    void setHitPoints(int value)
    {
        hitPoints = value;
        for (int i = 0; i < healthLimit.length; i++)
        {
            healthLimit[i] = (4 - i) * hitPoints / 5;
        }
    }

    private void setName(String name)
    {
        this.name = name;
    }

    private void setRandAttack(int value)
    {
        randAttack = value;
    }

    private void setRange(float value)
    {
        range = value;
    }

    void setSpeed(int value)
    {
        speed = value;
    }

    private void setVisibility(int value)
    {
        visibility = value;
    }
}
