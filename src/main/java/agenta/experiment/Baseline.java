package agenta.experiment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import agenta.BaseCommander;
import agenta.Engine;
import agenta.InputParameters;
import agenta.SingleRandom;
import agenta.UnitType;
import agenta.UnitTypeBuilder;

/**
 * @author Andrey Hitrin
 * @since 08.08.2020
 */
public class Baseline
{
    private static final class RunResult
    {
        public final long winner;
        public final long steps;

        private RunResult(long winner, long steps)
        {
            this.winner = winner;
            this.steps = steps;
        }
    }

    public static void main(String[] args)
    {
        final List<UnitType> unitTypes = buildUnitTypes();
        InputParameters params = buildParams(unitTypes);

        List<RunResult> results = IntStream.range(0, 100)
                .mapToObj(i -> singleRun(unitTypes, params))
                .collect(Collectors.toList());
        long wins0 = 0, wins1 = 0, p0 = 0, p1 = 0, draws = 0, steps0 = 0, steps1 = 0;
        for (RunResult r : results) {
            if (r.winner == 0) {
                wins0++;
                steps0 += r.steps;
            }
            else if (r.winner == 1) {
                wins1++;
                steps1 += r.steps;
            }
            else {
                draws++;
            }
        }
        if (wins0 > 0) {
            steps0 /= wins0;
        }
        if (wins1 > 0) {
            steps1 /= wins1;
        }
        if (wins0 + wins1 > 0) {
            p0 = 100 * wins0 / (wins0 + wins1);
            p1 = 100 - p0;
        }
        System.out.printf("Player 0 won %d times (%d%%), in %d steps average%n", wins0, p0, steps0);
        System.out.printf("Player 1 won %d times (%d%%), in %d steps average%n", wins1, p1, steps1);
        System.out.printf("Total %d draws (battle runs for too long)%n", draws);
        System.out.printf("Total runs: %d%n", wins0 + wins1 + draws);
    }

    private static RunResult singleRun(List<UnitType> unitTypes, InputParameters params)
    {
        Engine e = new Engine(params, SingleRandom.get());
        e.init(unitTypes);
        int steps = 0;
        while (e.getWinner() == -1 && steps < 2000)
        {
            e.step();
            steps++;
        }
        System.out.printf("Player %d has won after %d steps%n", e.getWinner(), steps);
        return new RunResult(e.getWinner(), steps);
    }

    private static InputParameters buildParams(List<UnitType> unitTypes)
    {
        InputParameters params = new InputParameters();
        params.addCommander(new BaseCommander());
        params.addCommander(new BaseCommander());
        unitTypes.forEach(ut -> params.addUnit(ut.getName().toLowerCase(), 10, 10));
        return params;
    }

    private static List<UnitType> buildUnitTypes()
    {
        UnitType warrior = new UnitTypeBuilder()
                .setName("Warrior")
                .setAttackSpeed(3)
                .setBaseAttack(2)
                .setRandAttack(1)
                .setHitPoints(10)
                .setSpeed(3)
                .setVisibility(10)
                .setRange(1.45f)
                .build();
        final List<UnitType> unitTypes = List.of(warrior);
        return unitTypes;
    }
}
