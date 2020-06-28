package agenta;

/**
 * Интерфейс для всех объектов на карте.  
 */
public abstract class MapObject {
	protected int x, y;
	
	public int getPlacementType(){ return 0; }
	public final int getX(){ return x; }
	public final int getY(){ return y; }
	final void moveTo(int newX, int newY){	x = newX;	y = newY; }
	final void move(int dx, int dy){ x += dx;	y += dy; }
}
