package agenta;

import java.util.*;

/*
    Здесь идёт процесс обработки. Движку при создании передаются входные параметры
    с помощью объекта InputParameters. Далее к нему присоединяются необходимые
    Viewer'ы. На каждом шаге работы приложения вызывается метод движка step(), в котором
    обрабатывается движение юнитов. При наличии изменений движок уведомляет об этом
    Viewer'ов, а те запрашивают необходимую информацию с помощью get-методов и отображаются
    в нужном для себя виде.
*/

public final class Engine {
    private Map map = new Map();
    private Vector<Unit> units = new Vector<Unit>();
    private int unitCounter[] = new int[2];
    private Commander commanders[] = new Commander[2];
    private UnitDatabase ud = UnitDatabase.get();
    private Vector<Viewer> viewers = new Vector<Viewer>();
    private SingleRandom generator = SingleRandom.get();
    private int winner = -1;

    public Engine(InputParameters ip){
        // Расставляем юнитов на карте
        Unit unit;
        commanders[0] = ip.getCommander(0);
        commanders[1] = ip.getCommander(1);

        for(int i = 0; i < ud.size(); i++){
            for(int player = 0; player < 2; player++){
                for(int j = 0; j < ip.getUnit(i, player); j++){
                    unit = new Unit(ud.typeOf(i), player, map);
                    unitCounter[player]++;
                    int x, y;
                    do{
                        x = generator.nextInt(map.getSIZE());
                        y = generator.nextInt(map.getSIZE());
                    }while(!map.canPlaceObject(unit, x, y));
                    map.placeObject(unit, x, y);                    
                    units.add(unit);
                    commanders[player].submit(unit, true);
                }
            }
        }
    }

    // Один шаг работы
    public void step(){
        // Если хотя бы один из игроков не имеет юнитов, финиш
        if((unitCounter[0] * unitCounter[1]) == 0) return;
        updateViewers();
        
        for(int i = 0; i < units.size(); i++){
            Unit u = units.get(i);
            if(u.isAlive()){
                u.act();
            }else{
                unitCounter[u.getPlayer()]--;
                if(unitCounter[u.getPlayer()] == 0){
                    winner = 1 - u.getPlayer();
                }
                units.remove(i);
                i--;
            }    
        }
        commanders[0].act();
        commanders[1].act();
    }
    
    public void updateViewers(){
        for(int i = 0; i < viewers.size(); i++)
            viewers.get(i).update(map);
    }

    // Позволяет подключить к движку вид. Возвращается идентификатор вида
    public int addViewer(Viewer viewer){
        viewers.add(viewer);
        return viewers.size();
    }

    // Удаляет вид по его идентификатору
    public void removeViewer(int index){
        viewers.remove(index);
    }
    
    public int getWinner(){ return winner; }

    public String getMap(){ return map.toString(); }

}
