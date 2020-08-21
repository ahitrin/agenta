package agenta;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для командиров.
 * @author Ahitrin
 *
 */
public class BaseCommander implements Commander
{
    /**
     * Список подчинённых
     */
    protected List<Commander> subordinates = new ArrayList<>();
    /**
     * Очередь входящих сообщений/приказов
     */
    private List<Command> queue = new ArrayList<>();

    // Не делаем ничего
    public void act(ActionListener actionListener)
    {
        while (!queue.isEmpty())
        {
            System.out.println(queue.remove(0).toString());
        }
    }

    // Не делаем ничего
    public void obtain(Command com)
    {
        queue.add(com);
    }

    // Стандартный алгоритм регистрации связи с другим командиром
    public void submit(Commander comm, boolean subordinate)
    {
        if (subordinate)
        {
            subordinates.add(comm);
            comm.submit(this, false);
        }
    }

}
