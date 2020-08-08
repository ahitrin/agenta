package agenta;

/**
 * @author Andrey Hitrin
 * @since 08.08.2020
 */
public class Application
{
    public static void main(String[] args)
    {
        PanelViewer p = new PanelViewer();
        Commander mc0 = new BaseCommander();
        Commander mc1 = new BaseCommander();
        CommandLineInitiator cli = new CommandLineInitiator("placement.txt", mc0, mc1);
        cli.load();
        Engine e = new Engine(cli.getParameters(), SingleRandom.get());
        e.init(DefaultUnits.build());
        e.addViewer(p);
        PanelViewerFrame f = new PanelViewerFrame("Agenta test", p);
        doRun(e, f, p);
    }

    private static void doRun(Engine e, PanelViewerFrame parentComponent, PanelViewer p)
    {
        while (e.getWinner() == -1)
        {
            e.step();
            /*try{
                Thread.sleep(5);
            }catch(InterruptedException ex){}*/
        }
        final String message = "Player " + e.getWinner() + " has won!";
        parentComponent.showEndMessage(p, message);
    }
}
