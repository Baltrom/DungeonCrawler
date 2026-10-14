# Dungeon Crawler – Erste Ideen

## Grundkonzept
Textbasierter Dungeon Crawler in Java.

## Offene Fragen
- **Umfang / Ausmaße:** Noch unklar (Anzahl Räume, Länge der Story, Anzahl Klassen).
- **Eingabesystem:** Noch nicht entschieden, siehe unten.

### Option A: Auswahlmenü (if/else bzw. switch)
Ablauf: Textausgabe (Storytelling), dann Prompt an den Spieler: `Wähle: a / b / c / d`.
- Einfach umzusetzen, klar kontrollierbar
- Weniger Freiheit für den Spieler

### Option B: Freitexteingabe
Java sucht in der Eingabe nach Schlagwörtern, um die Absicht des Spielers zu "erraten".
- Mehr Freiheit und Immersion
- Aufwändiger, Fehlinterpretationen möglich

(Mögliche Mischform: Menü als Standard, Freitext für ausgewählte Szenen.)

## Würfelsystem (RNG)
DnD-ähnliche Würfel:
- D20
- D10
- D6

## Klassen
Der Spieler wählt am Anfang eine Klasse mit unterschiedlichen Attributen für die RNG-basierten Skillchecks.

| Klasse | Beispiele für Stärken |
|--------|-----------------------|
| Mage   | Alte Runen lesen |
| Rogue  | Schloss knacken, Falle entschärfen, Akrobatik-Checks |

Idee: Manche Aktionen sind nur für bestimmte Klassen (gut) machbar. Der "alte Magier" schafft z. B. keine Akrobatik.

## Nächste Schritte
- [ ] Eingabesystem entscheiden (A, B oder Mischform)
- [ ] Weitere Klassen und Attribute festlegen
- [ ] Skillcheck-Regeln definieren (Würfel + Attribut vs. Schwierigkeit)
- [ ] Umfang des Dungeons festlegen
