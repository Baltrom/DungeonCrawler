# Dungeon Crawler – Erste Ideen

## Grundkonzept
Textbasierter Dungeon Crawler in Java.

## Eingabesystem
**Entschieden:** Auswahl über nummerierte Optionen (z. B. 1–3 oder 1–5), siehe Option A. Freitext (Option B) ist vorerst nicht geplant.

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
- Attributwerte auf einer Skala von **1–5** (durch Items können sie darüber steigen).
- Der Attributwert wird direkt auf den **D20-Wurf addiert**.
- Eine **1** ist immer ein Fehlschlag, eine **20** immer ein Erfolg.
- Das **Wurfergebnis wird immer angezeigt**.
- Skillchecks sind **nicht wiederholbar**. Bei einem Fehlschlag passiert nichts, oder eine Falle löst aus und verursacht Schaden.
- Kein Check darf den Weg einfach blockieren (kein Festhängen im Spiel). Ist ein Check nötig, um in den nächsten Raum zu kommen, ist er eine **Falle, bei deren Fehlschlag der Charakter sterben kann**.
- Schwierigkeitswerte:

| Stufe  | Zielwert |
|--------|----------|
| Leicht | 6–8      |
| Mittel | 10–12    |
| Schwer | 14–18    |

### Werte der Klassen

| Attribut     | Magier | Schurke |
|--------------|--------|---------|
| Stärke       | 1      | 2       |
| Geschick     | 2      | 5       |
| Intelligenz  | 5      | 1       |
| Lebenspunkte | 12     | 16      |

### Lebenspunkte
- Maximal **20 Lebenspunkte**.
- Start: **Magier 12**, **Schurke 16**.
- Ein seltenes Item (vielleicht ein Ring) erhöht die maximalen Lebenspunkte **dauerhaft um 3** und **hebt damit die Obergrenze von 20 an**.
- Heilen nur über **Tränke**. Einen Trank kann man in **jedem Auswahlmenü** trinken.
- Vor dem Endgegner gibt es einen **Heiltrank** (wichtig für den Magier).

### Rüstungsklasse (RK)
- Gegner haben eine Rüstungsklasse. Ein Treffer ist erfolgreich, wenn **D20 + Attribut** die Rüstungsklasse des Gegners erreicht oder übertrifft.
- Auch Magier und Schurke brauchen eine Rüstungsklasse, damit gegen sie Treffer gewürfelt werden können. Die Werte legen wir noch fest.

### Items
- Maximal **5 Plätze**. Es gibt kein Anlegen: Was man im Inventar hat, wirkt (z. B. ein Ring mit Bonuswerten).
- **Tränke und Schlüssel** zählen zu den 5 Plätzen.
- Ist das Inventar voll, **wählt der Spieler**, ob und welches Item weg soll. Ein abgeworfenes Item ist **endgültig weg**.
- Boni **stapeln sich**. Attribute können durch Items **über 5** steigen.
- Items findet man bei **besonderen Gegnern** oder **in Räumen nach Skillchecks**, z. B.:
  - Untersuchen (Intelligenz)
  - Kleine verschlossene Schatulle öffnen (Geschick)
  - Etwas aufbrechen (Stärke)

### Klassenauswahl
- Ablauf:
  1. Klasse wählen
  2. „Bist du sicher?“ (bei „Nein“ zurück zur Auswahl)
  3. Namen eingeben (1–15 Zeichen, Eingabe darf nicht leer sein, Buchstaben und Zahlen, keine Sonderzeichen, keine Leerzeichen)
  4. Spiel startet
- Darstellung: ASCII-Figur der Klasse mit Attributwerten und Lebenspunkten.
- Nur ASCII-Zeichen ausgeben (ae/oe/ue statt Umlauten), damit die Windows-Konsole nichts verstümmelt.

### Konsole „leeren“
- Vor jedem neuen Bildschirm viele Leerzeilen ausgeben, damit der alte Text aus dem Sichtfeld verschwindet.
- Das passiert zentral in einer Methode `clearScreen()`.

### Kampf (soll selten vorkommen)
- Die Würfe passieren, sobald der Spieler seine Aktion wählt.
- **Treffer:** D20 + Attribut gegen die Rüstungsklasse des Gegners.
- **Wer beginnt:**
  - Betritt man einen Raum direkt mit einem Gegner, entscheidet normalerweise ein **Münzwurf**.
  - Vorsichtiges Vorgehen wird belohnt: Der Spieler kann **schleichen** (Check auf Geschick) oder **spähen** (Check auf Intelligenz). Gelingt der Check, **beginnt der Spieler immer**. Misslingt er, entscheidet der normale Münzwurf.
- **Fliehen ist nicht möglich.**
- Schaden: **Magier D6**, **Schurke D10**.
- Der Magier bekommt vielleicht eine begrenzte Anzahl Zauber (später besprechen).

### Dungeon
- Der Dungeon ist **zufällig**, erzeugt aus einem **Seed**.
- **Raum 0** liegt immer **draußen** vor dem Tor bzw. Gitter. Er zählt **nicht** als erreichter Raum.
- Erste Version: **5 Räume** (Raum 1–5). Raum 1–4 werden zufällig aus einem Pool von etwa 8 Raumvorlagen gezogen, **Raum 5 ist der Endgegner**.
- Zwei getrennte Zufallsgeneratoren: einer für den Dungeon (Seed, wird gespeichert), einer für die Würfe. **Die Würfe sind nicht an den Seed gebunden** und fallen bei jedem Spiel (und nach dem Laden) neu aus.

### Speichern und Laden
- Speichern und Laden ist in der **ersten Version** enthalten.
- Gespeichert werden **Seed, Raum, Lebenspunkte, Items, Name und Klasse**.
- Beim Laden wird der alte Spielstand **gelöscht**, damit es kein Savescumming gibt.

### Rangliste
- Gewertet wird die **Anzahl erreichter Räume** (Raum 0 zählt nicht, das Maximum ist in der ersten Version 5).
- **Gleichstand:** Wer zuerst da war, steht höher. Später, mit mehr Räumen, kommen **Lebenspunkte und/oder Züge** als zusätzliches Kriterium dazu.
- Spieler mit **demselben Namen** bekommen getrennte Einträge.
- Eingetragen wird **nur nach Tod oder Sieg**, nicht beim Beenden.
- Gespeichert werden die **Top 10** in einer Datei (z. B. `highscores.txt`).
- Im Hauptmenü gibt es den Punkt „Rangliste anzeigen“.

### Erste lauffähige Version (MVP)
- Menü
- Klassenwahl mit ASCII-Bildern
- 5 Räume
- Würfelmechanik (Wurfergebnis wird angezeigt)
- Skillchecks
- Kampf
- Items
- Rangliste
- Speichern und Laden

### Als Nächstes besprechen
1. Magier-Zauber (begrenzte Anzahl)
2. Arten der Skillchecks im Dungeon und ihre Auswirkungen (Fallen, Schaden)
3. Konkrete Items (Ring, Tränke, Schlüssel, Loot von besonderen Gegnern)
4. Gegner und Endgegner (inkl. Rüstungsklasse, Lebenspunkte, Schaden)
5. Story und Raumvorlagen

### Noch zu klären
- Rüstungsklasse von Magier und Schurke (und welches Attribut für den Trefferwurf zählt).
- Wie wird gespeichert: eigener Menüpunkt „Speichern und Beenden“ im Spiel oder automatisch? Was passiert mit dem Spielstand nach Tod oder Sieg?
- Soll ein Fehlschlag bei einem Weg-Check immer tödlich sein oder „nur“ hohen Schaden verursachen? (Eine 1 würde sonst automatisch den Charakter töten.)

## Nächste Schritte
- [x] Eingabesystem entscheiden (nummerierte Auswahl)
- [x] Anzahl Klassen festlegen (2: Magier, Schurke)
- [x] Attribute und Werte pro Klasse festlegen
- [x] Skillcheck-Regeln definieren (D20 + Attribut vs. Schwierigkeit)
- [x] Umfang der ersten Version festlegen (5 Räume)
- [ ] Die fünf Themen unter „Als Nächstes besprechen“ durchgehen
- [ ] Punkte aus „Noch zu klären“ entscheiden
