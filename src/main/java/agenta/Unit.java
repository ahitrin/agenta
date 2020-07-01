package agenta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Юнит. Ходит по карте, слушается приказов и бъёт врагов
 * @author Ahitrin
 */
public class Unit extends MapObject implements Commander
{
    /**
     * Фильтрует список объектов, отбрасывая из него всех не-юнитов
     * @param objs Входящий список объектов
     * @return Только объекты-юниты из входящего списка
     */
    private static Unit[] filterUnits(MapObject[] objs)
    {
        List<Unit> units = new ArrayList<>();

        for (MapObject obj : objs)
        {
            if (obj instanceof Unit)
                units.add((Unit)obj);
        }
        Unit[] u = new Unit[units.size()];
        return units.toArray(u);
    }
    private final UnitType type;
    private UnitState state;
    private final Map map;
    private Unit target;
    private final int player;
    private int speedCounter, attackCounter, healthCounter;
    private int currentHitPoints;
    private UnitCommand currentCommand = null;
    private Commander commander = null;

    public Unit(UnitType type, int player, Map map)
    {
        this.type = type;
        this.player = player;
        this.map = map;
        SingleRandom gen = SingleRandom.get();
        speedCounter = gen.nextInt(type.getSpeed()) + 1;
        attackCounter = gen.nextInt(type.getAttackSpeed()) + 1;
        currentHitPoints = type.getHitPoints();
        state = UnitState.STAND;
        currentCommand = new UnitCommand(type, state, UnitType.MAX_PRIORITY);
    }

    /**
     * Метод действия юнита
     */
    public void act()
    {
        if (healthCounter > 0)
        {
            healthCounter--;
            if (healthCounter == 0)
            {
                healthCounter = 100;
                if (currentHitPoints < type.getHitPoints())
                    currentHitPoints++;
                if (currentHitPoints == type.getHitPoints())
                    healthCounter = -1;
            }
        }
        if (attackCounter > 0)
            attackCounter--;
        if (speedCounter > 0)
            speedCounter--;
        if ((attackCounter > 0) && (speedCounter > 0))
            return;

        if (target != null)
        {
            if (!target.isAlive())
                target = null;
        }
        Unit[] neighbours = null;
        switch (state)
        {
        case STAND:
            /*
             * Находясь в этом состоянии, юнит не двигается с места. Противник
             * атакуется только в том случае, когда он находится на расстоянии атаки
             */
            neighbours = filterCanAttack(filterEnemies(filterUnits(
                    map.getObjectsInRadius(x, y, type.getRange()))));
            if (neighbours.length > 0)
            {
                // пока что атакуем случайно выбранного противника
                selectTarget(neighbours, true).sufferDamage(doAttack());
            }
            break;
        case ATTACK:
            /*
             * Юнит ищет цель заданного типа в пределах расстояния своей атаки. Если цели
             * нет, юнит пытается найти противника в радиусе своей зоны видимости. Если же
             * и там нет врага, то юнит движется случайным образом
             */
            neighbours = filterCanAttack(filterEnemies(filterUnits(
                    map.getObjectsInRadius(x, y, type.getRange()))));
            if (neighbours.length > 0)
                selectTarget(neighbours, true).sufferDamage(doAttack());
            else
            {
                neighbours = filterCanAttack(filterEnemies(filterUnits(
                        map.getObjectsInRadius(x, y, type.getVisibility()))));
                if (neighbours.length > 0)
                {
                    selectTarget(neighbours, true);
                    int dx = target.x - x, dy = target.y - y;
                    dx = Integer.compare(dx, 0);
                    dy = Integer.compare(dy, 0);
                    doMove(dx, dy);
                }
                else
                {
                    int dx = SingleRandom.get().nextInt(3) - 1;
                    int dy = SingleRandom.get().nextInt(3) - 1;
                    doMove(dx, dy);
                }
            }
            break;
        case ESCAPE:
            /*
             * Юнит пытается убежать от врагов
             */
            neighbours = filterEnemies(filterUnits(
                    map.getObjectsInRadius(x, y, type.getVisibility())));
            if (neighbours.length > 0)
            {
                double dx = 0, dy = 0, r;
                for (Unit uu : neighbours)
                {
                    r = Math.sqrt((uu.x - x) * (uu.x - x) + (uu.y - y) * (uu.y - y));
                    dx += (x - uu.x) / r;
                    dy += (y - uu.y) / r;
                }
                int idx = (dx > 0) ? 1 : (dx < 0) ? -1 : 0;
                int idy = (dy > 0) ? 1 : (dy < 0) ? -1 : 0;
                doMove(idx, idy);
            }
            break;
        }
    }

    /**
     * Нанесение удара юнитом
     * @return Величина наносимого повреждения
     */
    private int doAttack()
    {
        if (attackCounter > 0)
            return 0;
        attackCounter = type.getAttackSpeed();
        Random gen = new Random();
        return gen.nextInt(type.getRandAttack()) + type.getBaseAttack();
    }

    /**
     * Перемещает юнит по карте.
     * @param dx Смещение по x
     * @param dy Смещение по y
     */
    private void doMove(int dx, int dy)
    {
        if (speedCounter > 0)
            return;
        if (map.canPlaceObject(this, x + dx, y + dy))
        {
            map.removeObject(this, x, y);
            map.placeObject(this, x + dx, y + dy);
            speedCounter = type.getSpeed();
        }
    }

    /**
     * Фильтрует список юнитов, оставляя только те, которые юнит может атаковать
     * @param units Входящий список юнитов
     * @return Юниты, которые могут быть атакованы
     */
    private Unit[] filterCanAttack(Unit[] units)
    {
        List<Unit> able = new ArrayList<>();

        for (Unit unit : units)
        {
            if ((type.getAttackType() & unit.getPlacementType()) != 0)
                able.add(unit);
        }
        Unit[] u = new Unit[able.size()];
        return able.toArray(u);
    }

    /**
     * Фильтрует список юнитов, оставляя только врагов
     * @param units Входящий список юнитов
     * @return Враги из входящего списка
     */
    private Unit[] filterEnemies(Unit[] units)
    {
        List<Unit> enemies = new ArrayList<>();

        for (Unit unit : units)
        {
            if (unit.getPlayer() != player)
                enemies.add(unit);
        }

        Unit[] u = new Unit[enemies.size()];
        return enemies.toArray(u);
    }

    /**
     * Фильтрует список юнитов, оставляя только дружественных
     * @param units Входящий список юнитов
     * @return Дружественные из входящего списка
     */
    public Unit[] filterFriends(Unit[] units)
    {
        List<Unit> friends = new ArrayList<>();

        for (Unit unit : units)
        {
            if (unit.getPlayer() == player)
                friends.add(unit);
        }
        Unit[] u = new Unit[friends.size()];
        return friends.toArray(u);
    }

    /**
     * Фильтрует список объектов, отбрасывая из него те, что слишком далеки для атаки
     * @param objs Входящий список объектов
     * @return Только доступные для атаки объекты
     */
    public MapObject[] filterInAttackRadius(MapObject[] objs)
    {
        List<MapObject> avail = new ArrayList<>();

        float limit = type.getRange() * type.getRange();
        for (MapObject obj : objs)
        {
            if (obj.x * obj.x + obj.y * obj.y - x * x - y * y <= limit)
            {
                avail.add(obj);
            }
        }

        MapObject[] mo = new MapObject[avail.size()];
        return avail.toArray(mo);
    }

    public int getPlacementType()
    {
        return type.getWalk();
    }

    public int getPlayer()
    {
        return player;
    }

    public UnitType getType()
    {
        return type;
    }

    /**
     * Возвращает true, если юнит жив.
     */
    public boolean isAlive()
    {
        return (currentHitPoints > 0);
    }

    /**
     * Реализация подчинения команде
     */
    public void obtain(Command com)
    {
        /*if(!(com instanceof UnitCommand))
            return;*/
        UnitCommand uc = null;
        try
        {
            uc = (UnitCommand)com;
        }
        catch (Exception e)
        {
            return;
        }
        if (uc.getType() == type)
        {
            currentCommand = uc;
            if (currentHitPoints >= type.getHealthLimit(uc.getPriority()))
                state = uc.getState();
        }
    }

    /**
     * Выбирает случайного юнита из списка
     * @param units Список юнитов
     * @return Случайный юнит
     */
    private Unit selectTarget(Unit[] units, boolean useOld)
    {
        if (useOld && (target != null))
            return target;
        if (units.length > 0)
            target = units[SingleRandom.get().nextInt(units.length)];
        else
            target = null;
        return target;
    }

    /**
     * Пока что реализуем только регистрацию командующего
     */
    public void submit(Commander comm, boolean subordinate)
    {
        if (!subordinate)
            commander = comm;
    }

    /**
     * Обработка повреждения юнита.
     * @param value Величина повреждений.
     */
    private void sufferDamage(int value)
    {
        if (currentHitPoints <= 0)
            return;
        currentHitPoints -= value;
        if (healthCounter == -1)
        {
            healthCounter = 100;
        }
        // Проверка на приоритет приказа над самосохранением
        if (currentHitPoints < type.getHealthLimit(currentCommand.getPriority()))
        {
            state = UnitState.ESCAPE;
            // М.б., извещаем командира
            // commander.obtain(null);
        }
        if (currentHitPoints <= 0)
        {
            if (commander != null)
            {
                // Извещаем командира о своей смерти :)
                commander.obtain(null);
            }
            map.removeObject(this, x, y);
        }
    }

    public String toString()
    {
        return type.toString() + player + " at [" + x + ", " + y + "]";
    }
}
