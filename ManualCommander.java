import agenta.*;
import java.util.*;

public class ManualCommander extends BaseCommander {
	private UnitState state = UnitState.ATTACK;
	private int priority = 1;
	
	public void act(){
		for(Commander c: subordinates){
			if(c instanceof Unit){
				UnitCommand uc = new UnitCommand(((Unit)c).getType(), state, priority);
				c.obtain(uc);
			}
		}
	}
	
	public void set(String value){
		String s = value.toLowerCase();
		int pri;
		StringTokenizer st = new StringTokenizer(s);
		try{
			s = st.nextToken();
			pri = new Integer(st.nextToken()).intValue();
		}catch(NoSuchElementException nse){
			return;
		}
		if(s.equals("stand")){ state = UnitState.STAND; }
		else if(s.equals("attack")){ state = UnitState.ATTACK; }
		else if(s.equals("escape")){ state = UnitState.ESCAPE; }
		priority = pri;
	}
}
