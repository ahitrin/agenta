package agenta;

/**
 * @author Andrey Hitrin
 * @since 06.12.2020
 */
public class Move extends Action
{
    private final GameMap map;
    private final int dx;
    private final int dy;

    public Move(Unit self, GameMap gameMap, int dx, int dy)
    {
        super(self);
        this.map = gameMap;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void act()
    {
        if (self.speedCounter > 0)
        {
            return;
        }
        if (map.canPlaceObject(self.x + dx, self.y + dy))
        {
            map.removeObject(self, self.x, self.y);
            map.placeObject(self, self.x + dx, self.y + dy);
            self.speedCounter = self.getType().getSpeed();
        }
    }
}
