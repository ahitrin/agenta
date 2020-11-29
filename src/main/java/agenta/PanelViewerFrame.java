package agenta;

import java.awt.*;

import javax.swing.*;

/**
 * @author Andrey Hitrin
 * @since 28.11.2020
 */
public class PanelViewerFrame extends JFrame
{
    private final PanelViewer p;

    public PanelViewerFrame(String caption, PanelViewer p)
    {
        super(caption);
        this.p = p;

        setSize(550, 480);

        Container c = getContentPane();
        c.add(p, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showEndMessage(String message)
    {
        JOptionPane.showMessageDialog(this, message,
                "End of game", JOptionPane.INFORMATION_MESSAGE);
        p.repaint();
    }
}
