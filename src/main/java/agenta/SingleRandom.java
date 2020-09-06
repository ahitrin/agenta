package agenta;

import java.util.Random;

public final class SingleRandom
{
    private static final Random root = new Random();

    public static SingleRandom get()
    {
        final long seed = root.nextLong();
        System.out.println(String.format("Seed: %s", seed));
        return new SingleRandom(new Random(seed));
    }

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
