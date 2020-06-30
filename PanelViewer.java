import agenta.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Выводим изображение не на саму форму, а на панель на этой форме.
 * Соответственно, часть функциональности уходит в эту панель.
 */
class PanelViewer extends JPanel implements Viewer {
    private Image iGrass, iTree;
    private Image[] iFootman = new Image[2], iArcher = new Image[2];
    private Image[] iKnight = new Image[2], iGryphon = new Image[2];
    private BufferedImage current, old;
    private boolean enabled;

    public PanelViewer(){
        super();
        setSize(450, 450);

        iGrass = getToolkit().getImage("Pictures/grass0.gif");
        iTree = getToolkit().getImage("Pictures/tree0.gif");
        iFootman[0] = getToolkit().getImage("Pictures/Footman0.gif");
        iFootman[1] = getToolkit().getImage("Pictures/Footman1.gif");
        iArcher[0] = getToolkit().getImage("Pictures/archer0.gif");
        iArcher[1] = getToolkit().getImage("Pictures/archer1.gif");
        iKnight[0] = getToolkit().getImage("Pictures/knight0.gif");
        iKnight[1] = getToolkit().getImage("Pictures/knight1.gif");
        iGryphon[0] = getToolkit().getImage("Pictures/gryphon0.gif");
        iGryphon[1] = getToolkit().getImage("Pictures/gryphon1.gif");

        setVisible(true);
    }

    public void update(Map map) {
        old = current;
        enabled = false;
        int size = map.getSIZE();

        Unit u = null;
        char c = ' ';
        Image ima = null;
        current = (BufferedImage)createImage(450, 450);
        Graphics2D currentGraph = current.createGraphics();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                u = (Unit)(map.getAirObject(i, j));
                if(u == null)
                    u = (Unit)(map.getGroundObject(i, j));
                
                if(u != null){
                    String s = u.getType().getName().toLowerCase();
                    c = s.charAt(0);
                    switch(c){
                    case 'f':    ima = iFootman[u.getPlayer()];    break;
                    case 'a':    ima = iArcher[u.getPlayer()];    break;
                    case 'k':    ima = iKnight[u.getPlayer()];    break;
                    case 'g':    ima = iGryphon[u.getPlayer()];    break;
                    }
                }else{
                    switch(map.getCellType(i, j)){
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
    
    public void paint(Graphics g){
        if(enabled)
            g.drawImage(current, 0, 0, this);
        else
            g.drawImage(old, 0, 0, this);
    }
}

class PanelViewerFrame extends JFrame{
    PanelViewer p = null;
    Engine e = null;
    ManualCommander mc0 = new ManualCommander(), mc1 = new ManualCommander();
    
    public PanelViewerFrame(String caption){
        super(caption);
        p = new PanelViewer();
        CommandLineInitiator cli = new CommandLineInitiator("placement.txt", mc0, mc1);
        e = new Engine(cli.getParameters());
        e.addViewer(p);
        
        setSize(550, 480);        
        
        Container c = getContentPane();        
        c.add(p, BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(6, 2, 10, 10));
        JButton bs0 = new JButton("Stand"), ba0 = new JButton("Attack"), be0 = new JButton("Escape");
        JButton bs1 = new JButton("Stand"), ba1 = new JButton("Attack"), be1 = new JButton("Escape");
        PanelViewerListener l0 = new PanelViewerListener(mc0), l1 = new PanelViewerListener(mc1);
        bs0.addActionListener(l0);                
        ba0.addActionListener(l0);
        be0.addActionListener(l0);        
        bs1.addActionListener(l1);
        ba1.addActionListener(l1);
        be1.addActionListener(l1);
        buttons.add(bs0);    buttons.add(new JSpinner());
        buttons.add(ba0);    buttons.add(new JSpinner());
        buttons.add(be0);    buttons.add(new JSpinner());
        buttons.add(bs1);    buttons.add(new JSpinner());
        buttons.add(ba1);    buttons.add(new JSpinner());
        buttons.add(be1);    buttons.add(new JSpinner());
                
        c.add(buttons, BorderLayout.EAST);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            
    }
    
    public void doRun(){
        while(e.getWinner() == -1){
            e.step();
            /*try{
                Thread.sleep(5);
            }catch(InterruptedException ex){}*/
        }    
        JOptionPane.showMessageDialog(this, "Player " + e.getWinner() + " has won!",
                "End of game", JOptionPane.INFORMATION_MESSAGE);
        p.repaint();
    }
    
    public static void main(String[] args){
        PanelViewerFrame f = new PanelViewerFrame("Agenta test");
        f.doRun();
    }
}

class PanelViewerListener implements ActionListener{
    private ManualCommander target;
    public PanelViewerListener(ManualCommander target){ this.target = target; }
    public void actionPerformed(ActionEvent e) {
        target.set(e.getActionCommand() + "1");
    }
}