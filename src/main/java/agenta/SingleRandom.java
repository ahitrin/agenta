package agenta;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SingleRandom
{
    private static final Logger LOG = LoggerFactory.getLogger(SingleRandom.class);
    private static final Random root = new Random();

    public static SingleRandom get()
    {
        final long seed = root.nextLong();
        LOG.debug(String.format("Seed: %s", seed));
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
