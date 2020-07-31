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
    private static List<Unit> filterUnits(List<MapObject> objs)
    {
        List<Unit> units = new ArrayList<>();

        for (MapObject obj : objs)
        {
            if (obj instanceof Unit)
                units.add((Unit)obj);
        }
        return units;
    }

    private final UnitType type;
    private UnitState state;
    private final Map map;
    private Unit target;
    private final int player;
    private int speedCounter, attackCounter, healthCounter;
    private int currentHitPoints;
    private UnitCommand currentCommand = null;
    int kills = 0;

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
        List<Unit> neighbours;
        switch (state)
        {
        case STAND:
            /*
             * Находясь в этом состоянии, юнит не двигается с места. Противник
             * атакуется только в том случае, когда он находится на расстоянии атаки
             */
            neighbours = filterCanAttack(filterEnemies(filterUnits(
                    map.getObjectsInRadius(x, y, type.getRange()))));
            if (!neighbours.isEmpty())
            {
                // пока что атакуем случайно выбранного противника
                performAttack(selectTarget(neighbours, true));
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
            if (!neighbours.isEmpty())
                performAttack(selectTarget(neighbours, true));
            else
            {
                neighbours = filterCanAttack(filterEnemies(filterUnits(
                        map.getObjectsInRadius(x, y, type.getVisibility()))));
                if (!neighbours.isEmpty())
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
            if (!neighbours.isEmpty())
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

    private void performAttack(Unit other)
    {
        int damage = doAttack();
        if (damage > 0) {
            System.out.println(toString() + " strikes " + other.toString() + " with " + damage);
        }
        other.sufferDamage(damage);
        if (!other.isAlive()) {
            kills += 1;
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
    private List<Unit> filterCanAttack(List<Unit> units)
    {
        List<Unit> able = new ArrayList<>();

        for (Unit unit : units)
        {
            if ((type.getAttackType() & unit.getPlacementType()) != 0)
                able.add(unit);
        }
        return able;
    }

    /**
     * Фильтрует список юнитов, оставляя только врагов
     * @param units Входящий список юнитов
     * @return Враги из входящего списка
     */
    private List<Unit> filterEnemies(List<Unit> units)
    {
        List<Unit> enemies = new ArrayList<>();

        for (Unit unit : units)
        {
            if (unit.getPlayer() != player)
                enemies.add(unit);
        }

        return enemies;
    }

    /**
     * Фильтрует список юнитов, оставляя только дружественных
     * @param units Входящий список юнитов
     * @return Дружественные из входящего списка
     */
    public List<Unit> filterFriends(List<Unit> units)
    {
        List<Unit> friends = new ArrayList<>();

        for (Unit unit : units)
        {
            if (unit.getPlayer() == player)
                friends.add(unit);
        }
        return friends;
    }

    /**
     * Фильтрует список объектов, отбрасывая из него те, что слишком далеки для атаки
     * @param objs Входящий список объектов
     * @return Только доступные для атаки объекты
     */
    public List<MapObject> filterInAttackRadius(List<MapObject> objs)
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

        return avail;
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
            if (currentCommand.getState() != uc.getState()) {
                System.out.println(toString() + " will " + uc.toString());
            }
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
    private Unit selectTarget(List<Unit> units, boolean useOld)
    {
        if (useOld && (target != null))
            return target;
        if (!units.isEmpty())
            target = units.get(SingleRandom.get().nextInt(units.size()));
        else
            target = null;
        return target;
    }

    /**
     * Пока что реализуем только регистрацию командующего
     */
    public void submit(Commander comm, boolean subordinate)
    {
    }

    /**
     * Обработка повреждения юнита.
     * @param value Величина повреждений.
     */
    private void sufferDamage(int value)
    {
        if (!isAlive())
            return;
        currentHitPoints -= value;
        if (healthCounter == -1)
        {
            healthCounter = 100;
        }
        if (value > 0)
            System.out.println(toString() + " has " + currentHitPoints + " HP");
        // Проверка на приоритет приказа над самосохранением
        if (currentHitPoints < type.getHealthLimit(currentCommand.getPriority()))
        {
            if (state != UnitState.ESCAPE)
                System.out.println(toString() + " runs away");
            state = UnitState.ESCAPE;
        }
        if (!isAlive())
        {
            System.out.println(toString() + " is dead");
            map.removeObject(this, x, y);
        }
    }

    public String toString()
    {
        return type.toString() + player + " (" + kills + " ks) at [" + x + ", " + y + "]";
    }
}
