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
        CommandLineInitiator cli = new CommandLineInitiator("placement.txt");
        cli.load();
        Engine e = new Engine(cli.getParameters(), SingleRandom.get());
        e.init(DefaultUnits.build());
        e.addViewer(p);
        PanelViewerFrame f = new PanelViewerFrame("Agenta test", p);
        while (e.getWinner() == -1)
        {
            e.step();
            /*try{
                Thread.sleep(5);
            }catch(InterruptedException ex){}*/
        }
        final String message = String.format("Player %d has won after %d ticks!",
                e.getWinner(),
                e.getTicks());
        f.showEndMessage(p, message);
    }

}
