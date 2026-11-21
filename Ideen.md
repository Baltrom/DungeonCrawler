# Dungeon Crawler – Erste Ideen

## Grundkonzept
Textbasierter Dungeon Crawler in Java.

## Offene Fragen
- **Umfang / Ausmaße:** Noch unklar (Anzahl Räume, Länge der Story).
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

Entschieden: Es bleibt bei **2 Klassen** (Magier und Schurke).

## Entscheidungen

### Attribute und Skillchecks
- Attributwerte auf einer Skala von **1–5**.
- Der Attributwert wird direkt auf den **D20-Wurf addiert**.
- Schwierigkeitswerte:

| Stufe  | Zielwert |
|--------|----------|
| Leicht | 6–8      |
| Mittel | 10–12    |
| Schwer | 14–18    |

### Lebenspunkte
- Maximal **20 Lebenspunkte**.
- Start: **Magier 12**, **Schurke 16**.
- Ein seltenes Item (vielleicht ein Ring) erhöht die maximalen Lebenspunkte **dauerhaft um 3**. Details zu Items besprechen wir später.
- Heilen nur über **Tränke**.

### Klassenauswahl
- Ablauf:
  1. Klasse wählen
  2. „Bist du sicher?“ (bei „Nein“ zurück zur Auswahl)
  3. Namen eingeben (maximal 15 Zeichen)
  4. Spiel startet
- Darstellung: ASCII-Figur der Klasse mit Attributwerten und Lebenspunkten.
- Nur ASCII-Zeichen ausgeben (ae/oe/ue statt Umlauten), damit die Windows-Konsole nichts verstümmelt.

### Konsole „leeren“
- Vor jedem neuen Bildschirm viele Leerzeilen ausgeben, damit der alte Text aus dem Sichtfeld verschwindet.
- Das passiert zentral in einer Methode `clearScreen()`.

### Rangliste
- Gewertet wird die **Anzahl erreichter Räume**.
- Eingetragen wird bei **Tod, Sieg und Beenden**.
- Gespeichert werden die **Top 10** in einer Datei (z. B. `highscores.txt`).
- Im Hauptmenü gibt es den Punkt „Rangliste anzeigen“.

### Noch offen
- Wie werden Gleichstände in der Rangliste entschieden?
- Gilt eine 1 oder 20 auf dem D20 als automatischer Fehlschlag bzw. Erfolg?
- Welche Attribute gibt es genau, und welche Werte hat jede Klasse?
- Wie funktionieren Items (inkl. Ring)?

## Nächste Schritte
- [ ] Eingabesystem entscheiden (A, B oder Mischform)
- [x] Anzahl Klassen festlegen (2: Magier, Schurke)
- [ ] Attribute und Werte pro Klasse festlegen
- [x] Skillcheck-Regeln definieren (D20 + Attribut vs. Schwierigkeit)
- [ ] Umfang des Dungeons festlegen
- [ ] Items besprechen
