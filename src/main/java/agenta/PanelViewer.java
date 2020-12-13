package agenta;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class PanelViewer extends JPanel implements Viewer
{
    private final Map<String, Image> tileCache = new HashMap<>();
    private BufferedImage current;

    public PanelViewer(int maxX, int maxY)
    {
        super();
        setSize(25 * maxX, 25 * maxY);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g)
    {
        g.drawImage(current, 0, 0, this);
    }

    @Override
    public void update(GameMap map)
    {
        final Dimension size = getSize();
        var image = (BufferedImage)createImage(size.width, size.height);
        Graphics2D currentGraph = image.createGraphics();
        Image tile = null;
        for (int i = 0; i < map.getSizeX(); i++)
        {
            for (int j = 0; j < map.getSizeY(); j++)
            {
                Unit u = (Unit)(map.getGroundObject(i, j));

                if (u != null)
                {
                    String imageKey = u.getType().getImage() + u.getPlayer();
                    tile = getImage(imageKey);
                }
                else
                {
                    switch (map.getCellType(i, j))
                    {
                    case GRASS:
                        tile = getImage("grass0");
                        break;
                    case TREE:
                        tile = getImage("tree0");
                        break;
                    }
                }
                currentGraph.drawImage(tile, i * 25, j * 25, this);
            }
        }
        current = image;
        repaint();
    }

    private Image getImage(String imageKey)
    {
        if (!tileCache.containsKey(imageKey))
        {
            String fullPath = "Pictures/" + imageKey + ".gif";
            tileCache.put(imageKey, getToolkit().getImage(fullPath));
        }
        return tileCache.get(imageKey);
    }
}

