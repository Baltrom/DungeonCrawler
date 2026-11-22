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
- Kein Check darf den Weg einfach blockieren (kein Festhängen im Spiel). Ist ein Check nötig, um in den nächsten Raum zu kommen, ist er eine **Falle** (Schaden bei Fehlschlag, nach dem Feinschliff nicht mehr tödlich, siehe „Feinschliff“).
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
- **Falle bei Weg-Checks** (nach dem Feinschliff):
  - Normaler Fehlschlag: **1W6 Schaden**.
  - Eine **1 (Patzer)**: **2W6 Schaden** (vorher tödlich).
  - Kleine Fallen beim Untersuchen (z. B. ein umstürzendes Regal): bei Fehlschlag **1W4 Schaden**.
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
| Lebenspunkte | 14     | 16      |

### Lebenspunkte
- Maximal **20 Lebenspunkte**.
- Start: **Magier 14** (vorher 12), **Schurke 16**.
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
- Artwork nur in ASCII. Texte bleiben vorerst ae/oe/ue statt Umlauten. **Geändert (Sitzung zu Farben):** Text darf ANSI-Farben haben (Terminal-Tab in IntelliJ, UTF-8), das Artwork bleibt ASCII und wird nur eingefärbt. Block- und Schattierungszeichen (`░▒▓█`) und Halbblöcke mit Hintergrundfarbe wurden im Terminal-Tab ausprobiert und sahen schlecht aus (Muster, Streifen), deshalb verworfen. Siehe `agents.md` und `tools/AnsiDemo.java`.

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
| Gewöhnlicher Gegner (Skelett) | 6 | 9 | +1 | D6 | Beute-Check am Leichnam |
| Besonderer Gegner (Gargoyle, freiwillig) | 10 | 11 | +2 | D6 | immer ein Item |
| Endgegner (Der tote König) | 18 | 13 | +2 | D6 | keine, das Spiel endet |

(Werte nach dem Feinschliff, vorher: Skelett 8/10/+2, Gargoyle 12/12/+3, Endgegner 20/13/+3.)

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
- Gespeichert wird über einen **eigenen Menüpunkt** im Spiel.
- Bei **Tod oder Sieg** kommt zuerst der **Eintrag in die Rangliste**, danach wird der **Spielstand gelöscht**.

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
Aktuell keine offenen Fragen.

## Umsetzungsstand Version 1
Version 1 ist umgesetzt: Hauptmenü, Klassenauswahl mit ASCII-Bildern, 5 Räume aus einem Seed, Würfelmechanik mit angezeigtem Wurfergebnis, Skillchecks, Kampf, Items, Rangliste, Speichern und Laden.

Bei der Umsetzung festgelegt (Details, die oben noch offen waren):
- **Speichern:** Menüpunkt „Speichern und zum Hauptmenue“ in den Raummenüs von Raum 0–4 (nicht im Kampf und nicht im Endgegner-Raum). Gespeichert wird in `savegame.txt`, auch was im Raum schon versucht wurde (Beute, Statue), damit man durch Speichern und Laden nichts wiederholen kann.
- **Rangliste:** `highscores.txt`, Zeile pro Eintrag. Raum 0 zählt nicht, nach einem Sieg stehen 5 Räume.
- **Schwierigkeit pro Raum:** Weg-Checks: Raum 1 leicht (schwerer Ansatz mittel), Raum 2–4 mittel (schwerer Ansatz schwer). Beute-Checks: Raum 1 leicht, Raum 2–3 mittel, Raum 4 schwer. Die schwereren Ansätze sind die Stärke-Ansätze „aufbrechen“ sowie je ein Ansatz in Bibliothek und Fallengang.
- **Kampf:** Eine 1 verfehlt und eine 20 trifft auch bei Angriffen immer. Das Schutzschild kann mehrfach gewirkt werden (die Boni addieren sich). Der Hinterhalt wirkt nur für den Schurken und nur nach gelungenem Schleichen (nicht nach Spähen).
- **Ring des Lebens:** Schenkt die zusätzlichen Lebenspunkte beim Aufnehmen sofort (aktuell +3) und senkt sie beim Wegwerfen wieder auf das neue Maximum.
- **Beute am Leichnam:** Der gewöhnliche Gegner hat keine eigene Beute. Das Looten eines Toten ist die Beute-Aktion der Raumvorlage „Die Gruft“.
- **Name:** Keine Umlaute (nur A–Z, a–z, 0–9).
- **Würfe:** Sind nicht an den Seed gebunden. Beim Laden wird der Dungeon aus dem Seed neu erzeugt, die Würfe fallen neu aus.

## Feinschliff nach dem ersten Durchspielen
Rückmeldung nach Version 1:
1. **Ergebnisse sichtbar machen:** Das Ergebnis einer Aktion (Wurf, Weiterkommen, Angriff, Fund) steht im nächsten Textblock, nachdem die 50 Leerzeilen den Bildschirm geleert haben. **Erledigt.**
2. **Zu schwer, besonders im Kampf:** Gemessen per Simulation (Bot, 60000 Durchläufe je Klasse): Vorher gewann der Magier etwa 25 %, der Schurke etwa 60 %. Größte Todesursachen waren der tödliche Patzer bei Weg-Checks (rund 17 % bei beiden), der Endgegner (Magier 34 %) und der Fallenschaden (Magier 19 %). **Umgesetzt (Variante „P4“):** Fallenschaden 1W6, Patzer 2W6 statt tödlich, schwächere Gegner (siehe Tabelle bei „Gegner“), Magier mit 14 Lebenspunkten. Danach (mit den freien Räumen) im Bot-Test: Magier etwa 60–70 %, Schurke etwa 90 %.
3. **Flavourtext überall:** Jede Interaktion, jeder Erfolg und Fehlschlag, jeder Übergang in den nächsten Raum, jeder Fund, jeder Trank und jeder Angriff hat Text (1–2 Sätze, düster). **Erledigt.**
4. **Nicht jeder Raum ist versperrt:** Siehe „Raumgestaltung nach dem Feinschliff“. **Erledigt.**

### Raumgestaltung nach dem Feinschliff
- **Freie und versperrte Räume:** Raum 1 ist immer frei, dazu Raum 2 oder 3 (zufällig), Raum 4 (vor dem Endgegner) ist immer versperrt. In freien Räumen gibt es einen Menüpunkt „Weitergehen“ mit Text, ohne Check.
- **Versperrte Räume:** Zuerst „Das Hindernis untersuchen“ (ohne Wurf, nur Text). Danach erscheinen die zwei Ansätze mit Attribut und Schwierigkeit.
- **Dinge zum Untersuchen:** Jede Raumvorlage hat 3 Stellen, jede nur einmal. Pro Raum genau eine mit Item (10 % leer), dazu Stimmung, Hinweise und Erkenntnisse. Manche haben bei Fehlschlag eine kleine Falle (1W4).
- **Verzweigungen:** Eine Stelle kann eine Folge-Interaktion freischalten („Die Kratzspuren genauer ansehen“). Diese erscheint erst nach der ersten und wird nur einmal gemacht.
- **Wirkungen einer gelungenen Interaktion:** Item, **Hinweis** (verrät, was im nächsten Raum wartet: Gegner, Statue, Endgegner, Raumname und ob der Durchgang versperrt ist), **Erkenntnis** (+2 auf den nächsten Check in diesem Raum) oder nur Stimmung.
- **Texte:** Pro Raumvorlage eine Datei `src/main/resources/story/rooms/NAME.properties` (Beschreibung mit 4 Zeilen, Hindernis, Ansätze, Stellen), allgemeine Texte (Kampf, Funde, Fallen, Hinweise) in `story/flavour.properties` mit mehreren Varianten, die zufällig gewählt werden.

## Nächste Schritte
- [x] Eingabesystem entscheiden (nummerierte Auswahl)
- [x] Anzahl Klassen festlegen (2: Magier, Schurke)
- [x] Attribute und Werte pro Klasse festlegen
- [x] Skillcheck-Regeln definieren (D20 + Attribut vs. Schwierigkeit)
- [x] Umfang der ersten Version festlegen (5 Räume)
- [x] Die fünf Themen unter „Als Nächstes besprechen“ durchgehen
- [x] Punkte aus „Noch zu klären“ entscheiden
- [x] Projektgerüst anlegen und Version 1 umsetzen
- [x] Anzeige umgedreht: Raumname und Beschreibung stehen oben, darunter der Block „Was zuletzt geschah“, dann Status und Auswahl (im Kampf: unter dem Kampf-Kopf)
- [ ] **TODO Räume mit Charakter füllen:** Mit dem Autor jeden Raum einzeln durchgehen (Raum 0, die acht Vorlagen, Raum 5/Thronsaal) und interessanter machen. Danach die Räume **personalisieren** (jeder Raum bekommt eigene Persönlichkeit, Details, Stellen und Texte). Arbeit in den Dateien `story/rooms/NAME.properties`. Vorgehen: ein Raum pro Schritt, erst besprechen, dann schreiben.
- [ ] Version 1 selbst durchspielen und die Werte feintunen (Gegner, Schwierigkeit, Items)
- [ ] Spätere Versionen: mehr Räume, mehr Items und Heiltränke, Endgegner mit Spezialangriffen, Zweitkriterium in der Rangliste (Lebenspunkte/Züge)
