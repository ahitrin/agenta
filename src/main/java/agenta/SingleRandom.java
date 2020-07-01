package agenta;

import java.util.Random;

/**
 * Служебный класс, хранящий генератор случайных чисел.
 * @author Андрей Хитрин
 */
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
