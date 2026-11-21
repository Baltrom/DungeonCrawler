package dungeon;

import character.Attribute;

/** Ein Ansatz, einen versperrten Durchgang zu passieren. Der schwerere Ansatz hat eine Stufe mehr Schwierigkeit. */
public record WayDef(String label, Attribute attribute, boolean harder, String successText, String failureText) {
}
