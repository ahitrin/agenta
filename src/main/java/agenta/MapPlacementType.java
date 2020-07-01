package agenta;

import java.io.Serializable;

public class MapPlacementType implements Serializable
{
    public static final int GROUND = 1;
    public static final int AIR = 2;

    private final int value;

    public MapPlacementType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
