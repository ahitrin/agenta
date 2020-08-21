package agenta;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

class PanelViewer extends JPanel implements Viewer
{
    private final Image iGrass;
    private final Image iTree;
    private final Image[] iFootman = new Image[2];
    private final Image[] iArcher = new Image[2];
    private final Image[] iKnight = new Image[2];
    private BufferedImage current, old;
    private boolean enabled;

    public PanelViewer()
    {
        super();
        setSize(450, 450);

        iGrass = getToolkit().getImage("Pictures/grass0.gif");
        iTree = getToolkit().getImage("Pictures/tree0.gif");
        iFootman[0] = getToolkit().getImage("Pictures/footman0.gif");
        iFootman[1] = getToolkit().getImage("Pictures/footman1.gif");
        iArcher[0] = getToolkit().getImage("Pictures/archer0.gif");
        iArcher[1] = getToolkit().getImage("Pictures/archer1.gif");
        iKnight[0] = getToolkit().getImage("Pictures/knight0.gif");
        iKnight[1] = getToolkit().getImage("Pictures/knight1.gif");

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
                    String s = u.getType().getName().toLowerCase();
                    char c = s.charAt(0);
                    switch (c)
                    {
                    case 'f':
                        ima = iFootman[u.getPlayer()];
                        break;
                    case 'a':
                        ima = iArcher[u.getPlayer()];
                        break;
                    case 'k':
                        ima = iKnight[u.getPlayer()];
                        break;
                    }
                }
                else
                {
                    switch (map.getCellType(i, j))
                    {
                    case GRASS:
                        ima = iGrass;
                        break;
                    case TREE:
                        ima = iTree;
                        break;
                    }
                }
                currentGraph.drawImage(ima, i * 25, j * 25, this);
            }
        }
        enabled = true;
        repaint();
    }
}

class PanelViewerFrame extends JFrame
{
    public PanelViewerFrame(String caption, PanelViewer p)
    {
        super(caption);

        setSize(550, 480);

        Container c = getContentPane();
        c.add(p, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showEndMessage(PanelViewer p, String message)
    {
        JOptionPane.showMessageDialog(this, message,
                "End of game", JOptionPane.INFORMATION_MESSAGE);
        p.repaint();
    }
}
