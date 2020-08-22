package agenta;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javafaker.Faker;

/**
 * Юнит. Ходит по карте, слушается приказов и бъёт врагов
 * @author Ahitrin
 */
public class Unit extends MapObject implements Commander
{
    private static final Predicate<MapObject> IS_UNIT = o -> o instanceof Unit;

    /**
     * Фильтрует список объектов, отбрасывая из него всех не-юнитов
     * @param objs Входящий список объектов
     * @return Только объекты-юниты из входящего списка
     */
    private static List<Unit> filterUnits(List<MapObject> objs)
    {
        return objs.stream().filter(IS_UNIT).map(o -> (Unit)o).collect(Collectors.toList());
    }

    private final UnitType type;
    private UnitState state;
    private final GameMap map;
    private final int player;
    private int speedCounter, attackCounter, healthCounter;
    private int currentHitPoints;
    private UnitCommand currentCommand;
    private int kills = 0;
    private final String name;
    private final SingleRandom random;
    private final Selector<Unit> selectTargetPerk;

    public Unit(UnitType type, int player, GameMap map, SingleRandom random)
    {
        this.type = type;
        this.player = player;
        this.map = map;
        this.random = random;
        speedCounter = this.random.nextInt(type.getSpeed()) + 1;
        attackCounter = this.random.nextInt(type.getAttackSpeed()) + 1;
        currentHitPoints = type.getHitPoints();
        state = UnitState.ATTACK;
        currentCommand = new UnitCommand(state, UnitType.MIN_PRIORITY);
        name = String.format("%s %s%d", new Faker().name().firstName(), type.toString(), player);
        healthCounter = this.random.nextInt(100) + 1;
        selectTargetPerk = new SelectRandomWithMemory(random);
    }

    @Override
    public void act(ActionListener actionListener)
    {
        if (healthCounter > 0)
        {
            healthCounter--;
            if (healthCounter == 0)
            {
                healthCounter = 100;
                if (currentHitPoints < type.getHitPoints())
                {
                    currentHitPoints++;
                }
                if (currentHitPoints >= type.getHealthLimit(currentCommand.getPriority()) &&
                        state == UnitState.ESCAPE)
                {
                    obtain(new UnitCommand(UnitState.ATTACK, currentCommand.getPriority() - 1));
                }
                if (currentHitPoints == type.getHitPoints())
                {
                    healthCounter = -1;
                }
            }
        }
        if (attackCounter > 0)
        {
            attackCounter--;
        }
        if (speedCounter > 0)
        {
            speedCounter--;
        }
        if ((attackCounter > 0) && (speedCounter > 0))
        {
            return;
        }

        List<Unit> neighbours;
        switch (state)
        {
        case STAND:
            /*
             * Находясь в этом состоянии, юнит не двигается с места. Противник
             * атакуется только в том случае, когда он находится на расстоянии атаки
             */
            neighbours = filterEnemies(filterUnits(
                    map.getObjectsInRadius(x, y, type.getRange())));
            if (!neighbours.isEmpty())
            {
                // пока что атакуем случайно выбранного противника
                List<Unit> currentNeighbours = new ArrayList<>(neighbours);
                actionListener.submit(() -> performAttack(selectTargetPerk.apply(currentNeighbours)));
            }
            break;
        case ATTACK:
            /*
             * Юнит ищет цель заданного типа в пределах расстояния своей атаки. Если цели
             * нет, юнит пытается найти противника в радиусе своей зоны видимости. Если же
             * и там нет врага, то юнит движется случайным образом
             */
            neighbours = filterEnemies(filterUnits(
                    map.getObjectsInRadius(x, y, type.getRange())));
            if (!neighbours.isEmpty())
            {
                List<Unit> currentNeighbours = new ArrayList<>(neighbours);
                actionListener.submit(() -> performAttack(selectTargetPerk.apply(currentNeighbours)));
            }
            else
            {
                neighbours = filterEnemies(filterUnits(
                        map.getObjectsInRadius(x, y, type.getVisibility())));
                if (!neighbours.isEmpty())
                {
                    Unit target = selectTargetPerk.apply(neighbours);
                    int dx = Integer.compare(target.x - x, 0);
                    int dy = Integer.compare(target.y - y, 0);
                    actionListener.submit(() -> doMove(dx, dy));
                }
                else
                {
                    int dx = random.nextInt(3) - 1;
                    int dy = random.nextInt(3) - 1;
                    actionListener.submit(() -> doMove(dx, dy));
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
                actionListener.submit(() -> doMove(idx, idy));
            }
            break;
        }
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
        if (!(com instanceof UnitCommand))
        {
            return;
        }
        UnitCommand uc = (UnitCommand)com;
        if (currentCommand.getState() != uc.getState())
        {
            System.out.println(toString() + " will " + uc.toString());
        }
        currentCommand = uc;
        if (currentHitPoints >= type.getHealthLimit(uc.getPriority()))
        {
            state = uc.getState();
        }
    }

    /**
     * Пока что реализуем только регистрацию командующего
     */
    public void submit(Commander comm, boolean subordinate)
    {
    }

    public String toString()
    {
        return String.format("%s (%d HP; %d ks) at [%d, %d]", name, currentHitPoints, kills, x, y);
    }

    /**
     * Нанесение удара юнитом
     * @return Величина наносимого повреждения
     */
    private int doAttack()
    {
        if (attackCounter > 0)
        {
            return 0;
        }
        attackCounter = type.getAttackSpeed();
        return random.nextInt(type.getRandAttack()) + type.getBaseAttack();
    }

    /**
     * Перемещает юнит по карте.
     * @param dx Смещение по x
     * @param dy Смещение по y
     */
    private void doMove(int dx, int dy)
    {
        if (!isAlive()) {
            return;
        }
        if (speedCounter > 0)
        {
            return;
        }
        if (map.canPlaceObject(x + dx, y + dy))
        {
            map.removeObject(this, x, y);
            map.placeObject(this, x + dx, y + dy);
            speedCounter = type.getSpeed();
        }
    }

    /**
     * Фильтрует список юнитов, оставляя только врагов
     * @param units Входящий список юнитов
     * @return Враги из входящего списка
     */
    private List<Unit> filterEnemies(List<Unit> units)
    {
        return units.stream().filter(u -> u.getPlayer() != player).collect(Collectors.toList());
    }

    private void performAttack(Unit other)
    {
        if (!isAlive()) {
            return;
        }
        int damage = doAttack();
        if (damage > 0)
        {
            System.out.println(MessageFormat.format("{0} strikes {1} with {2}", toString(), other.toString(), damage));
        }
        other.sufferDamage(damage);
        if (!other.isAlive())
        {
            kills += 1;
        }
    }

    /**
     * Обработка повреждения юнита.
     * @param value Величина повреждений.
     */
    private void sufferDamage(int value)
    {
        if (!isAlive())
        {
            return;
        }
        currentHitPoints -= value;
        if (healthCounter == -1)
        {
            healthCounter = 100;
        }
        // Проверка на приоритет приказа над самосохранением
        if (currentHitPoints < type.getHealthLimit(currentCommand.getPriority()))
        {
            obtain(new UnitCommand(UnitState.ESCAPE, currentCommand.getPriority() + 1));
        }
        if (!isAlive())
        {
            System.out.println(MessageFormat.format("{0} is dead", toString()));
            map.removeObject(this, x, y);
        }
    }
}
