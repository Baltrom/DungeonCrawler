package dungeon;

import character.Attribute;
import character.CharacterClass;
import item.Item;

/**
 * Eine Interaktion aus einem Raumskript: etwas untersuchen, durchsuchen, oeffnen.
 *
 * @param id          eindeutig im Raum, z. B. "poi2" oder "poi2.follow" (wird beim Speichern verwendet)
 * @param text        Stimmung beim Wahlen, wird immer gezeigt
 * @param attribute   Attribut des Checks, null = kein Check
 * @param easier      eine Stufe leichter als die Standard-Schwierigkeit des Raums
 * @param fixedItem   festes Item (z. B. Starthilfe), sonst null (dann wuerfelt der Generator)
 * @param onlyFor     nur fuer diese Klasse sichtbar, sonst null
 * @param successText Text bei Erfolg, null erlaubt, wenn es keinen Check gibt
 * @param failureText Text bei Fehlschlag, nur mit Check
 * @param failureDamage ob ein Fehlschlag 1W4 Schaden verursacht
 * @param follow      Folge-Interaktion, die nach dieser erscheint (Verzweigung), sonst null
 */
public record InteractionDef(String id, String label, String text, Attribute attribute, boolean easier,
                             Effect effect, Item fixedItem, CharacterClass onlyFor,
                             String successText, String failureText, boolean failureDamage,
                             InteractionDef follow) {
}
