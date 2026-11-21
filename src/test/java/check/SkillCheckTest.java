package check;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dice.FixedDice;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SkillCheckTest {

    @Test
    void rollPlusBonusReachingTheTargetSucceeds() {
        CheckResult result = SkillCheck.roll(new FixedDice(7), 5, 12);
        assertEquals(12, result.total());
        assertTrue(result.success());
    }

    @Test
    void rollPlusBonusBelowTheTargetFails() {
        CheckResult result = SkillCheck.roll(new FixedDice(6), 5, 12);
        assertEquals(11, result.total());
        assertFalse(result.success());
    }

    @Test
    void aOneAlwaysFailsEvenWithAHugeBonus() {
        CheckResult result = SkillCheck.roll(new FixedDice(1), 10, 6);
        assertFalse(result.success());
        assertTrue(result.isNaturalOne());
    }

    @Test
    void aTwentyAlwaysSucceedsEvenAgainstAnImpossibleTarget() {
        CheckResult result = SkillCheck.roll(new FixedDice(20), 0, 30);
        assertTrue(result.success());
        assertTrue(result.isNaturalTwenty());
    }

    @Test
    void describeShowsRollBonusTargetAndOutcome() {
        String success = SkillCheck.roll(new FixedDice(14), 5, 12).describe("Geschick");
        assertEquals("Wurf: 14 + 5 Geschick = 19 gegen 12 -> ERFOLG", success);
        String failure = SkillCheck.roll(new FixedDice(3), 2, 12).describe("Staerke");
        assertEquals("Wurf: 3 + 2 Staerke = 5 gegen 12 -> FEHLSCHLAG", failure);
        assertTrue(SkillCheck.roll(new FixedDice(1), 2, 12).describe("Staerke").contains("PATZER"));
        assertTrue(SkillCheck.roll(new FixedDice(20), 2, 12).describe("Staerke").contains("KRITISCHER ERFOLG"));
    }

    @Test
    void pickedTargetsStayInsideTheDifficultyRange() {
        Random random = new Random(5);
        for (Difficulty difficulty : Difficulty.values()) {
            for (int i = 0; i < 200; i++) {
                int target = difficulty.pickTarget(random);
                assertTrue(target >= difficulty.getMinTarget() && target <= difficulty.getMaxTarget());
            }
        }
    }

    @Test
    void difficultyRangesMatchTheAgreedValues() {
        assertEquals(6, Difficulty.EASY.getMinTarget());
        assertEquals(8, Difficulty.EASY.getMaxTarget());
        assertEquals(10, Difficulty.MEDIUM.getMinTarget());
        assertEquals(12, Difficulty.MEDIUM.getMaxTarget());
        assertEquals(14, Difficulty.HARD.getMinTarget());
        assertEquals(18, Difficulty.HARD.getMaxTarget());
    }
}
