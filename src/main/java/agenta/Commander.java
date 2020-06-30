package agenta;

/**
 *    Интерфейс, отвечающий за управление юнитами.
 *    @author Ahitrin
 */
public interface Commander
{
    /**
     * Один шаг размышлений
     */
    void act();

    /**
     * Получение команды или сообщения от подчинённого
     * @param com Команда или сообщение
     */
    void obtain(Command com);

    /**
     * Регистрирует связь с другим командиром
     * @param comm Другой командир
     * @param subordinate Если true, то подчинённый, если false, то командующий
     */
    void submit(Commander comm, boolean subordinate);
}
