package agenta;

/*
Класс-контейнер для передачи стартовых параметров программе. С помощью этого класса
мы можем разделить способ получения стартовых параметров и логику работы программы.
В качестве параметров выступают:
  - тип расположения юнитов - по линиям или случайным образом
  - количество юнитов каждого типа у каждого игрока
  - ссылки на объекты класса Commander, отвечающие за управление юнитами
*/

public final class InputParameters {
	private static final int MAX_UNITS_PER_TYPE = 20;
	private UnitPlacementType unitPlacement = UnitPlacementType.RANDOM;
	private boolean[] initialized;
	private int[] player0;
	private int[] player1;
	private Commander commanders[] = new Commander[2];
	private int commanderCounter = 0;

	public InputParameters(){
		UnitDatabase ud = UnitDatabase.get();
	    int size = ud.size();

	    initialized = new boolean[size];
	    player0 = new int[size];
	    player1 = new int[size];
	    commanders[0] = null;
	    commanders[1] = null;
	    for(int i = 0; i < initialized.length; i++)
	    	initialized[i] = false;
	}

	public void setUnitPlacement(UnitPlacementType up){ unitPlacement = up; }

	public void addUnit(int index, int player0, int player1){
	    try{
	    	if((player0 < 0) || (player1 < 0) || (player1 > MAX_UNITS_PER_TYPE) ||
	          (player1 > MAX_UNITS_PER_TYPE)) throw new ArrayIndexOutOfBoundsException();
	    	if(!initialized[index]){
	    		this.player0[index] = player0;
	    		this.player1[index] = player1;
	    		initialized[index] = true;
	    	}
	    }catch(ArrayIndexOutOfBoundsException e){}
	}

	public void addUnit(UnitType unit, int player0, int player1){
	    UnitDatabase ud = UnitDatabase.get();
	    addUnit(ud.indexOf(unit), player0, player1);
	}

	public void addCommander(Commander commander){
	    if(commanderCounter >= 2) return;
	    commanders[commanderCounter] = commander;
	    commanderCounter++;
	}

	public UnitPlacementType getUnitPlacement(){ return unitPlacement; }

	public Commander getCommander(int index){
	    if((index >= 0) && (index < commanderCounter))
	    	return commanders[index];
	    return null;
	}

	public int getUnit(int index, int player){
	    if((index >= 0) && (index < initialized.length))
	    	switch(player){
	        	case  0: return player0[index];
	        	case  1: return player1[index];
	        	default: return 0;
	    	}
	    return 0;
	}
}
