package agenta.experiment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        List<RunResult> results = Experiment.runExperiment(100, 5000L, unitTypes, playerUnits, playerUnits);
        Experiment.calculateStatistics(results);
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
