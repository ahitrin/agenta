import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import agenta.Engine;
import agenta.Map;
import agenta.Unit;
import agenta.Viewer;

/**
 * Выводим изображение не на саму форму, а на панель на этой форме.
 * Соответственно, часть функциональности уходит в эту панель.
 */
class PanelViewer extends JPanel implements Viewer
{
    private final Image iGrass;
    private final Image iTree;
    private final Image[] iFootman = new Image[2];
    private final Image[] iArcher = new Image[2];
    private final Image[] iKnight = new Image[2];
    private final Image[] iGryphon = new Image[2];
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
        iGryphon[0] = getToolkit().getImage("Pictures/gryphon0.gif");
        iGryphon[1] = getToolkit().getImage("Pictures/gryphon1.gif");

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

    public void update(Map map)
    {
        old = current;
        enabled = false;
        int size = Map.SIZE;

        Unit u = null;
        char c = ' ';
        Image ima = null;
        current = (BufferedImage)createImage(450, 450);
        Graphics2D currentGraph = current.createGraphics();
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                u = (Unit)(map.getGroundObject(i, j));

                if (u != null)
                {
                    String s = u.getType().getName().toLowerCase();
                    c = s.charAt(0);
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
                    case 'g':
                        ima = iGryphon[u.getPlayer()];
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
    public static void main(String[] args)
    {
        PanelViewerFrame f = new PanelViewerFrame("Agenta test");
        f.doRun();
    }

    private PanelViewer p = null;
    private Engine e = null;
    private ManualCommander mc0 = new ManualCommander();
    private ManualCommander mc1 = new ManualCommander();

    private PanelViewerFrame(String caption)
    {
        super(caption);
        p = new PanelViewer();
        CommandLineInitiator cli = new CommandLineInitiator("placement.txt", mc0, mc1);
        e = new Engine(cli.getParameters());
        e.addViewer(p);

        setSize(550, 480);

        Container c = getContentPane();
        c.add(p, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void doRun()
    {
        while (e.getWinner() == -1)
        {
            e.step();
            /*try{
                Thread.sleep(5);
            }catch(InterruptedException ex){}*/
        }
        JOptionPane.showMessageDialog(this, "Player " + e.getWinner() + " has won!",
                "End of game", JOptionPane.INFORMATION_MESSAGE);
        p.repaint();
    }
}

class PanelViewerListener implements ActionListener
{
    private final ManualCommander target;

    public PanelViewerListener(ManualCommander target)
    {
        this.target = target;
    }

    public void actionPerformed(ActionEvent e)
    {
        target.set(e.getActionCommand() + "1");
    }
}
