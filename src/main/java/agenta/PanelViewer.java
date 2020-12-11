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

    public PanelViewer()
    {
        super();
        setSize(450, 450);
        setVisible(true);
    }

    public void paint(Graphics g)
    {
        if (enabled)
        {
            g.drawImage(current, 0, 0, this);
        }
        else
        {
            g.drawImage(old, 0, 0, this);
        }
    }

    @Override
    public void update(GameMap map)
    {
        old = current;
        enabled = false;
        int size = GameMap.SIZE;

        Image ima = null;
        current = (BufferedImage)createImage(450, 450);
        Graphics2D currentGraph = current.createGraphics();
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
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

