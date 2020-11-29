package agenta.experiment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import agenta.Engine;
import agenta.SingleRandom;
import agenta.UnitType;
import agenta.UnitTypeBuilder;

/**
 * @author Andrey Hitrin
 * @since 08.08.2020
 */
public class Baseline
{

    public static void main(String[] args)
    {
        final List<UnitType> unitTypes = buildUnitTypes();

        Map<String, Long> playerUnits = unitTypes.stream()
                .map(UnitType::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toMap(Function.identity(), (s) -> 10L));
        List<RunResult> results = runExperiment(100, 5000L, unitTypes, playerUnits, playerUnits);
        calculateStatistics(results);
    }

    private static List<RunResult> runExperiment(
            long totalExperiments,
            long maxTicks,
            List<UnitType> unitTypes,
            Map<String, Long> player0Units,
            Map<String, Long> player1Units)
    {
        return LongStream.range(0, totalExperiments)
                .mapToObj(i -> singleRun(maxTicks, unitTypes, player0Units, player1Units))
                .collect(Collectors.toList());
    }

    private static void calculateStatistics(List<RunResult> results)
    {
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

    private static RunResult singleRun(long maxTicks, List<UnitType> unitTypes, Map<String, Long> player0Units,
            Map<String, Long> player1Units)
    {
        Engine e = new Engine(SingleRandom.get(), player0Units, player1Units);
        e.init(unitTypes);
        while (e.getWinner() == -1 && e.getTicks() < maxTicks)
        {
            e.step();
        }
        System.out.printf("Player %d has won after %d ticks%n", e.getWinner(), e.getTicks());
        return new RunResult(e.getWinner(), e.getTicks());
    }

    private static List<UnitType> buildUnitTypes()
    {
        return List.of(new UnitTypeBuilder()
                .setName("Warrior")
                .setAttackSpeed(3)
                .setBaseAttack(1)
                .setRandAttack(4)
                .setHitPoints(10)
                .setSpeed(3)
                .setVisibility(10)
                .setRange(1.45f)
                .build());
    }
}
