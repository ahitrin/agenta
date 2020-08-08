package agenta;

import java.util.List;
import java.util.function.Function;

/**
 * @author Andrey Hitrin
 * @since 08.08.2020
 */
@FunctionalInterface
public interface Selector<T> extends Function<List<T>, T>
{
}
