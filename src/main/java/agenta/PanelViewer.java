package agenta;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class PanelViewer extends JPanel implements Viewer
{
    private final Map<String, Image> imagesCache = new HashMap<>();
    private BufferedImage current, old;
    private boolean enabled;

    public PanelViewer(int maxX, int maxY)
    {
        super();
        setSize(25 * maxX, 25 * maxY);
        setVisible(true);
    }

    public void paint(Graphics g)
    {
        BufferedImage img = enabled ? current : old;
        g.drawImage(img, 0, 0, this);
    }

    @Override
    public void update(GameMap map)
    {
        old = current;
        enabled = false;

        Image ima = null;
        final Dimension size = getSize();
        current = (BufferedImage)createImage(size.width, size.height);
        Graphics2D currentGraph = current.createGraphics();
        for (int i = 0; i < map.getSizeX(); i++)
        {
            for (int j = 0; j < map.getSizeY(); j++)
            {
                Unit u = (Unit)(map.getGroundObject(i, j));

                if (u != null)
                {
                    String imageKey = u.getType().getImage() + u.getPlayer();
                    ima = getImage(imageKey);
                }
                else
                {
                    switch (map.getCellType(i, j))
                    {
                    case GRASS:
                        ima = getImage("grass0");
                        break;
                    case TREE:
                        ima = getImage("tree0");
                        break;
                    }
                }
                currentGraph.drawImage(ima, i * 25, j * 25, this);
            }
        }
        enabled = true;
        repaint();
    }

    private Image getImage(String imageKey)
    {
        if (!imagesCache.containsKey(imageKey))
        {
            String fullPath = "Pictures/" + imageKey + ".gif";
            imagesCache.put(imageKey, getToolkit().getImage(fullPath));
        }
        return imagesCache.get(imageKey);
    }
}

