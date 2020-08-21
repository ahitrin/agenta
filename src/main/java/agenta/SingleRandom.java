package agenta;

import java.util.Random;

public final class SingleRandom
{
    private static final Random rand = new Random();
    private static final SingleRandom instance = new SingleRandom();

    public static SingleRandom get()
    {
        return instance;
    }

    private SingleRandom()
    {
    }

    public int nextInt()
    {
        return rand.nextInt();
    }

    public int nextInt(int n)
    {
        return rand.nextInt(n);
    }
}
