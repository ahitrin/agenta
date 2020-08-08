package agenta;

public class UnitTypeBuilder
{
    private final UnitTypeImpl type = new UnitTypeImpl();

    public UnitTypeBuilder setAttackSpeed(int value)
    {
        type.setAttackSpeed(value);
        return this;
    }

    public UnitTypeBuilder setBaseAttack(int value)
    {
        type.setBaseAttack(value);
        return this;
    }

    public UnitTypeBuilder setHitPoints(int value)
    {
        type.setHitPoints(value);
        return this;
    }

    public UnitTypeBuilder setName(String name)
    {
        type.setName(name);
        return this;
    }

    public UnitTypeBuilder setRandAttack(int value)
    {
        type.setRandAttack(value);
        return this;
    }

    public UnitTypeBuilder setRange(float value)
    {
        type.setRange(value);
        return this;
    }

    public UnitTypeBuilder setSpeed(int value)
    {
        type.setSpeed(value);
        return this;
    }

    public UnitTypeBuilder setVisibility(int value)
    {
        type.setVisibility(value);
        return this;
    }

    public UnitType build() {
        return new UnitTypeImpl(type);
    }
}
