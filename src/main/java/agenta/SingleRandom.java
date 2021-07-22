package agenta;

import java.util.Random;

public final class SingleRandom
{

    private final Random random;

    public SingleRandom(Random random)
    {
        this.random = random;
    }

    public int nextInt(int n)
    {
        return random.nextInt(n);
    }
}
