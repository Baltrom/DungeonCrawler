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

### Skillchecks im Dungeon
Es gibt vier Arten von Checks:

| Art | Zweck | Fehlschlag |
|-----|-------|------------|
| **Weg-Check** | Nötig, um in den nächsten Raum zu kommen (Tür, Gitter, Abgrund) | Falle (siehe unten) |
| **Beute-Check** | Optional, bringt ein Item oder eine Belohnung (toten Gegner looten, Schatulle öffnen, Runen lesen, geheimes Fach öffnen) | Meist folgenlos, keine schlimmen Folgen |
| **Kampf-Vorbereitung** | Schleichen (Geschick) oder Spähen (Intelligenz) vor einem Kampf | Nichts, es gilt der Münzwurf |
| **Wissens-Check** | Runen lesen, Hinweise finden (Intelligenz) | Man erfährt nichts, sonst keine Strafe |

- **Weg-Checks** bieten **2–3 Ansätze** mit unterschiedlichen Attributen und Schwierigkeiten, z. B. bei einer Tür: Schloss knacken (Geschick, mittel), Aufbrechen (Stärke, schwer), Runen entziffern (Intelligenz, mittel). Der Spieler wählt den Ansatz.
- **Falle bei Weg-Checks:**
  - Normaler Fehlschlag: **2×D6 Schaden**.
  - Eine **1 (Patzer)**: die Falle ist **tödlich**.
- **Schwierigkeit nach Raum:**

| Raum | Weg-Check | Beute-Check |
|------|-----------|-------------|
| 1 | leicht bis mittel | leicht |
| 2–3 | mittel | mittel |
| 4 | mittel bis schwer | schwer |
| 5 | kein Check, Endgegner | — |

- **Raum 0 (draußen)** hat die Möglichkeit, bei richtigem Vorgehen mit einem Item in den Dungeon zu starten (ein leichter Check als Einstieg, der die Mechanik zeigt): Magier Ladungstrank, Schurke Schutzamulett.

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
- Ein Heiltrank heilt **immer 6 Lebenspunkte**.
- Vor dem Endgegner gibt es einen **Heiltrank** (wichtig für den Magier). In der **ersten Version gibt es genau einen Heiltrank** (der vor dem Endgegner). Er kommt nicht im zufälligen Beute-Pool vor. Spätere Versionen mit mehr Räumen brauchen mehr Items und mehr Heiltränke.

### Rüstungsklasse (RK)
- Gegner haben eine Rüstungsklasse. Ein Treffer ist erfolgreich, wenn **D20 + Attribut** die Rüstungsklasse des Gegners erreicht oder übertrifft.
- Auch Magier und Schurke haben eine Rüstungsklasse: **Magier 11**, **Schurke 13**. Das Schutzamulett und der Zauber Schutzschild erhöhen sie.

### Items
- Maximal **5 Plätze**. Es gibt kein Anlegen: Was man im Inventar hat, wirkt (z. B. ein Ring mit Bonuswerten).
- **Tränke und Schlüssel** zählen zu den 5 Plätzen.
- Ist das Inventar voll, **wählt der Spieler**, ob und welches Item weg soll. Ein abgeworfenes Item ist **endgültig weg**.
- Boni **stapeln sich**. Attribute können durch Items **über 5** steigen.
- **Jedes Item belegt einen Platz**, auch mehrere gleiche Tränke (kein Stapeln).
- Items findet man bei **besonderen Gegnern** oder **in Räumen nach Skillchecks**, z. B.:
  - Untersuchen (Intelligenz)
  - Kleine verschlossene Schatulle öffnen (Geschick)
  - Etwas aufbrechen (Stärke)
- **Beute-Checks:** Auch bei Erfolg gibt es eine **niedrige Chance, kein Item** zu bekommen (Vorschlag: 10 %, z. B. eine 1 auf einem D10).
- **Besondere Gegner** (freiwilliger Kampf) geben **immer ein Item**.
- Raum 0: Starthilfe bei richtigem Vorgehen, abhängig von der Klasse: der **Magier** findet einen **Ladungstrank**, der **Schurke** ein **Schutzamulett**. Es gibt dort keinen Heiltrank, da es in der ersten Version nur einen gibt.
- Items sind nicht an die Klasse gebunden. Der Schurke kann z. B. den Ladungstrank finden, muss ihn aber nicht aufheben.

Items der ersten Version:

| Item | Art | Wirkung |
|------|-----|---------|
| Heiltrank | Trank (aktiv) | Heilt 6 Lebenspunkte (in Version 1 nur einer, vor dem Endgegner) |
| Ladungstrank | Trank (aktiv) | Füllt die 3 Zauberladungen des Magiers auf |
| Ring des Lebens | passiv, selten | +3 maximale Lebenspunkte (hebt die Obergrenze an) |
| Ring der Klugheit | passiv | +1 Intelligenz |
| Stiefel der Schatten | passiv | +1 Geschick |
| Kraftgürtel | passiv | +1 Stärke |
| Schutzamulett | passiv | +1 Rüstungsklasse |
| Magischer Dolch | passiv, für den Schurken | +1 auf Treffer und +1 Schaden |
| Eiserner Schlüssel | Schlüssel | Vierter Ansatz bei einem Weg-Check: Tür öffnen ohne Wurf, der Schlüssel ist danach weg |

Die Liste wird in späteren Versionen **größer**. Welches Item wo liegt, gehört zur Dungeon-Erzeugung (Seed).

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

### Magier-Zauber
- Der Magier hat **3 gemeinsame Zauberladungen** pro Spiel (für alle Zauber zusammen).
- Ein normaler Angriff (Zauberstab, D6) kostet keine Ladung. Jeder Zauber kostet **1 Ladung**.
- Ein **Trank, der Ladungen auffüllt**, ist als Item vorgesehen (Details bei den Items).
- Die Option **„Zauber wirken“** steht in **jedem Auswahlmenü** und führt in ein Untermenü, in dem man den Zauber wählt.
- Angriffsattribut: Der Magier würfelt Treffer mit **Intelligenz**, der Schurke mit **Geschick**.

| Zauber       | Wirkung                                                          | Kosten    |
|--------------|------------------------------------------------------------------|-----------|
| Feuerball    | D10 Schaden, trifft immer (kein Trefferwurf)                     | 1 Ladung  |
| Schutzschild | +3 Rüstungsklasse für den Rest des Kampfes                       | 1 Ladung  |
| Arkane Sicht | +3 auf einen Intelligenz-Check (z. B. Spähen oder Runen lesen)   | 1 Ladung  |

**Hinterhalt (Schurke):** Gelingt der Schleichen-Check, macht der erste Treffer des Schurken **doppelten Schaden** (2×D10).

### Gegner
Rüstungsklasse (RK), Trefferbonus und Schaden der Gegner in der ersten Version (Startwerte, werden später im Test geprüft):

| Typ | Lebenspunkte | RK | Trefferbonus | Schaden | Beute |
|-----|--------------|----|--------------|---------|-------|
| Gewöhnlicher Gegner | 8 | 10 | +2 | D6 | Beute-Check am Leichnam |
| Besonderer Gegner (freiwillig) | 12 | 12 | +3 | D6 | immer ein Item |
| Endgegner | 20 | 13 | +3 | D6 | keine, das Spiel endet |

- Ein Gegner trifft, wenn **D20 + Trefferbonus** die Rüstungsklasse des Spielers erreicht. Gegner greifen jede Runde an, ohne Taktik.
- **Besonderer Gegner:** z. B. eine **Statue oder ein Gargoyle**, die der Spieler durch **Untersuchen oder einen Skillcheck „aktiviert“**. Erst dann beginnt der Kampf. Man kann ihn also umgehen.
- **Endgegner:** In Version 1 ist er **einfach nur zäher** (mehr Lebenspunkte). **Später** soll er sich anders verhalten (z. B. Spezialangriffe oder Phasen).
- **Anzahl der Kämpfe in Version 1** (maximal drei): ein gewöhnlicher Gegner in einem zufälligen Raum zwischen 2 und 4, ein besonderer Gegner (freiwillig), der Endgegner in Raum 5 (einziger Pflichtkampf).

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

### Story
- Setting: **Die Gruft eines toten Königs.** Der Endgegner ist der Geist bzw. Wiedergänger des Königs.
- Ton: **düster**.
- Der Charakter hat je nach Klasse **eigene Gründe**, in die Gruft zu gehen, und bekommt einen **eigenen Intro-Text**:
  - **Schurke:** wegen eines **Auftrags**.
  - **Magier:** aus **Neugier**.
- Die Story-Texte sind auf Deutsch und werden in ASCII ausgegeben (ae/oe/ue).

### Raumvorlagen
Raum 1–4 werden aus dem Seed aus einem Pool von acht Vorlagen gezogen. Jede Vorlage hat einen Weg-Check mit 2–3 Ansätzen und einen Beute-Check.

| Vorlage | Weg-Check (Ansätze) | Beute-Check | Besonderheit |
|---------|---------------------|-------------|--------------|
| Bibliothek | Verschlossenes Tor: Runen (Int), Schloss (Ge) | Runen lesen (Int) | |
| Gruft | Sarkophag verschiebt Ausgang: Aufbrechen (St), Runen (Int) | Toten Gegner looten | geeignet für den gewöhnlichen Gegner |
| Statuensaal | Tür mit Statuen: Schloss (Ge), Aufbrechen (St) | Schatulle öffnen (Ge) | geeignet für den besonderen Gegner |
| Fallengang | Druckplatten: Akrobatik (Ge), Muster erkennen (Int) | Geheimes Fach (Int) | |
| Alchemielabor | Giftgas-Tür: Schloss (Ge), Aufbrechen (St) | Schatulle (Ge) | |
| Verlies | Gitter: Aufbrechen (St), Schloss (Ge) | Geheimes Fach (Int) | |
| Zisterne | Abgrund/Wasser: Akrobatik (Ge), Brücke aufbrechen (St) | Untersuchen (Int) | |
| Waffenkammer | Zugestürzter Gang: Aufbrechen (St), Runen (Int) | Truhe aufbrechen (St) | |

- Die **Schwierigkeit hängt von der Raumnummer ab**, nicht von der Vorlage (siehe Tabelle bei den Skillchecks).
- Der Generator stellt sicher, dass **genau ein gewöhnlicher und ein besonderer Gegner** vorkommen. Dafür sind die Vorlagen entsprechend markiert.
- **Raum 0:** Tor bzw. Gitter vor der Gruft, erster leichter Check, Starthilfe (Magier: Ladungstrank, Schurke: Schutzamulett).
- **Raum 5:** Thronsaal mit dem Endgegner. Am Eingang liegt der Heiltrank.

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
1. ~~Magier-Zauber~~ (erledigt, siehe oben)
2. ~~Arten der Skillchecks im Dungeon~~ (erledigt, siehe „Skillchecks im Dungeon“)
3. ~~Konkrete Items~~ (erledigt, siehe „Items“)
4. ~~Gegner und Endgegner~~ (erledigt, siehe „Gegner“)
5. ~~Story und Raumvorlagen~~ (erledigt, siehe „Story“ und „Raumvorlagen“)

### Noch zu klären
- Wie wird gespeichert: eigener Menüpunkt „Speichern und Beenden“ im Spiel oder automatisch? Was passiert mit dem Spielstand nach Tod oder Sieg?

## Nächste Schritte
- [x] Eingabesystem entscheiden (nummerierte Auswahl)
- [x] Anzahl Klassen festlegen (2: Magier, Schurke)
- [x] Attribute und Werte pro Klasse festlegen
- [x] Skillcheck-Regeln definieren (D20 + Attribut vs. Schwierigkeit)
- [x] Umfang der ersten Version festlegen (5 Räume)
- [x] Die fünf Themen unter „Als Nächstes besprechen“ durchgehen
- [ ] Punkte aus „Noch zu klären“ entscheiden
