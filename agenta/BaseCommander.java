package agenta;

import java.util.*;

/**
 * Базовый класс для командиров.
 * @author Ahitrin
 *
 */
public class BaseCommander implements Commander {
	/**
	 * Список подчинённых
	 */
	protected Vector<Commander> subordinates = new Vector<Commander>();
	/**
	 * Единственный начальник
	 */
	protected Commander overlord;
	/**
	 * Очередь входящих сообщений/приказов
	 */
	protected Vector<Command> queue = new Vector<Command>();
	
	// Не делаем ничего
	public void act(){}
	
	// Не делаем ничего
	public void obtain(Command com){ queue.add(com); }

	// Стандартный алгоритм регистрации связи с другим командиром
	public void submit(Commander comm, boolean subordinate){
		if(subordinate){
			subordinates.add(comm);
			comm.submit(this, false);
		}else{
			overlord = comm;
		}
	}

}
