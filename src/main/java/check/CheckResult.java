package check;

public record CheckResult(int roll, int bonus, int target, boolean success) {

    public int total() {
        return roll + bonus;
    }

    public boolean isNaturalOne() {
        return roll == 1;
    }

    public boolean isNaturalTwenty() {
        return roll == 20;
    }

    /** Eine Zeile fuer die Konsole, z. B. "Wurf: 14 + 5 Geschick = 19 gegen 12 -> ERFOLG". */
    public String describe(String bonusLabel) {
        String text = "Wurf: " + roll + " + " + bonus + " " + bonusLabel + " = " + total() + " gegen " + target;
        if (isNaturalOne()) {
            return text + " -> PATZER (eine 1 misslingt immer)";
        }
        if (isNaturalTwenty()) {
            return text + " -> KRITISCHER ERFOLG (eine 20 gelingt immer)";
        }
        return text + (success ? " -> ERFOLG" : " -> FEHLSCHLAG");
    }
}
