package agenta.experiment;

import java.util.List;

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
    public static void main(String[] args)
    {
        InputParameters params = new InputParameters();
        params.addCommander(new BaseCommander());
        params.addCommander(new BaseCommander());
        UnitType warrior = new UnitTypeBuilder()
                .setName("Warrior")
                .setAttackSpeed(3)
                .setBaseAttack(1)
                .setRandAttack(4)
                .setHitPoints(10)
                .setSpeed(3)
                .setVisibility(10)
                .setRange(1.45f)
                .build();
        params.addUnit(warrior.getName().toLowerCase(), 30, 30);
        Engine e = new Engine(params, SingleRandom.get());
        e.init(List.of(warrior));
        int steps = 0;
        while (e.getWinner() == -1 && steps < 1000) {
            e.step();
            steps++;
        }
        System.out.printf("Player %d has won after %d steps%n", e.getWinner(), steps);
    }
}
