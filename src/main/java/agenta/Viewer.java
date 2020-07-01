package agenta;

/**
 * Интерфейс для отображения содержимого модели.
 */

@FunctionalInterface
public interface Viewer
{
    void update(Map map);
}
