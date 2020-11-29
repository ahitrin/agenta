package agenta.experiment;

/**
 * @author Andrey Hitrin
 * @since 29.11.2020
 */
public class RunResult
{
    public final long winner;
    public final long steps;

    public RunResult(long winner, long steps)
    {
        this.winner = winner;
        this.steps = steps;
    }
}
