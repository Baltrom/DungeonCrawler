# Übergabe: Projektstand für einen neuen Chat

**Zweck:** Diese Datei enthält alles, was ein neuer Chat (oder eine neue Sitzung) braucht, um nahtlos weiterzuarbeiten. Sie wird am Ende jeder Sitzung aktualisiert. Lies sie zusammen mit [agents.md](agents.md) (Regeln für Agenten), [Ideen.md](Ideen.md) (Entscheidungen) und [features.md](features.md) (Feature-Stand). Das Protokoll aller Prompts steht in [PromptDiary.md](PromptDiary.md).

> **Startanweisung für den neuen Chat:** „Lies `Uebergabe.md`, `agents.md`, `Ideen.md` und `features.md` im Projektordner und fasse kurz zusammen, wo wir stehen. Dann frag mich, womit wir weitermachen.“

## 1. Das Projekt in Kürze

| | |
|---|---|
| Was | Textbasierter Dungeon Crawler in Java (Konsole), Studienprojekt „Einführung Software Engineering“ |
| Autor | Nikolaus Koch (wi26b166), Solo-Projekt: nur der Autor und Claude |
| Repository | https://github.com/Baltrom/DungeonCrawler (privat), Branch `main` |
| Projektordner | `C:\WI26\Einführung SoftwareEngineering\STPRO\DungeonCrawler` |
| Spielidee | Gruft eines toten Königs, düster. Klasse wählen (Magier oder Schurke), 5 Räume, Endgegner, Würfel (D20, D10, D6) |
| Stand | **Version 1 ist fertig und abgegeben** (Code, Handbuch, Feature-Häkchen, Prompt Diary). 201 Tests grün. |

## 2. Wie der Autor arbeiten will (wichtig)

- **Sprache:** Deutsch. Code-Bezeichner Englisch, Story-Texte und Doku Deutsch. Text darf ANSI-Farben haben (über `game.Ansi`, im Terminal-Tab von IntelliJ gestartet), das **Artwork bleibt ASCII** und wird nur eingefärbt (Block- und Schattierungszeichen sahen im Terminal schlecht aus, siehe `agents.md`). Bestehende Texte sind noch ae/oe/ue statt Umlauten.
- **Erst besprechen, dann bauen.** Entscheidungen werden im Gespräch getroffen und in `Ideen.md` festgehalten. Bei größeren Änderungen an der Struktur vorher fragen.
- **Offene Fragen immer als nummerierte Liste am Ende der Antwort.** (Ausdrücklicher Wunsch des Autors.)
- **Kurze Entscheidungen** gibt der Autor als Liste („1 passt, 2 nein, …“). Darauf einzeln antworten.
- **Commit und Push nur nach Ansage** („Ja, committen“, „pushe main“). In der V1-Phase galt: nach jedem abgehakten Punkt pushen. Direkt auf `main`, keine Feature-Branches mehr nötig.
- **Dateien des Autors nicht überschreiben oder löschen**, ohne zu fragen (`Prompt Diary.pdf`, `Ideen.zip`, eigene Texte).
- **Alles, was entschieden oder gebaut wird, steht in Markdown-Dateien im Repository**, damit in einem neuen Chat weitergearbeitet werden kann. Nach jeder Sitzung: `Uebergabe.md`, `PromptDiary.md`, `Ideen.md`, `features.md` aktualisieren.
- Der Autor spielt das Spiel selbst durch und liefert danach eine **Feinschliff-Liste**. Diese wird gebündelt abgearbeitet.

## 3. Umgebung

| | |
|---|---|
| IDE | IntelliJ IDEA 2026.2.3, Projekt ist dort offen (MCP-Zugriff: `projectPath` = Projektordner, mit `/` statt `\`) |
| JDK | `C:\Users\Baltrom\.jdks\openjdk-27` (Java-Ziel 21 in der `pom.xml`) |
| Maven | Nicht im PATH. Das von IntelliJ mitgebrachte: `C:\Users\Baltrom\AppData\Local\Programs\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd` (Pfad ändert sich mit der IntelliJ-Version) |
| GitHub | `gh` ist als `Baltrom` angemeldet |
| Shell | PowerShell. `java` und `mvn` sind nicht im PATH |

**Tests ausführen:**
```powershell
$env:JAVA_HOME = "C:\Users\Baltrom\.jdks\openjdk-27"
& "C:\Users\Baltrom\AppData\Local\Programs\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" -B test
```

**Spiel von Hand starten:** `game.Main` in IntelliJ (grüner Pfeil). Per Skript: `java -cp target/classes game.Main` mit Eingaben aus einer Datei. Der Arbeitsordner bekommt dann `savegame.txt` und `highscores.txt`.

**Stolpersteine auf dieser Maschine:**
- Die Run-Konfiguration über das IntelliJ-MCP (`execute_run_configuration`) lief bei Tests in eine Zeitüberschreitung. Maven direkt funktioniert.
- In PowerShell-Here-Strings für Commit-Nachrichten dürfen **keine doppelten Anführungszeichen** vorkommen (der Commit scheitert sonst).
- Dateien mit Windows-1252 (z. B. die alte `Readme.md.txt`) lassen sich nicht mit dem Schreib-Werkzeug überschreiben. Dann über eine UTF-8-Kopie ersetzen.
- `Prompt Diary.pdf` kann nicht gelesen werden (kein `pdftoppm`).
- Exit-Code 255 von PowerShell-Befehlen mit `2>&1` und `git` ist oft nur ein Ausgabeformat-Effekt. Ergebnis immer prüfen.
- Git warnt bei jedem Commit vor LF/CRLF. Das ist harmlos.

## 4. Architektur

Pakete in `src/main/java` (alle Details in [agents.md](agents.md)):

| Paket | Wichtigste Klassen |
|---|---|
| `character` | `CharacterClass` (MAGE, ROGUE mit allen Werten), `Player` (Lebenspunkte, Inventar, Ladungen), `Attribute`, `Spell`, `PlayerName` |
| `check` | `SkillCheck.roll(dice, bonus, target)`, `CheckResult`, `Difficulty` (EASY 6–8, MEDIUM 10–12, HARD 14–18) |
| `combat` | `Enemy` (Record: `skeleton()`, `gargoyle()`, `deadKing()`) |
| `dice` | `Dice` (D6/D10/D20, `roll(sides)`) |
| `dungeon` | `DungeonGenerator.generate(seed)` → `Dungeon` (Räume 0–5), `Room`, `RoomScript` (liest die Raumdateien), `InteractionDef`/`Interaction`, `WayDef`/`WayOption`, `Effect`, `RoomTemplate`, `EnemyKind`, `RoomKind` |
| `game` | `Main` → `MainMenu` → `ClassSelection` → `Game` (Spielablauf), `Combat` (Kampf), `PlayerActions` (Trank/Zauber/Status), `ConsoleIO`, `RoomProgress`, `GameLauncher`/`DefaultGameLauncher`, `GameResult` |
| `highscore` | `Highscores`, `HighscoreEntry` (Datei `highscores.txt`, Zeilen `Name;KLASSE;Räume`) |
| `save` | `SaveGame`, `SaveManager` (Datei `savegame.txt`, Zeilen `schluessel=wert`) |
| `item` | `Item` (9 Items), `Inventory` (5 Plätze), `Bonuses`, `ItemType` |
| `story` | `AsciiArt`, `StoryText`, `Flavour` |

**Zufall ist dreifach getrennt:**
1. Dungeon aus `Random(seed)` im `DungeonGenerator` (Seed wird gespeichert, Beute und Raumfolge hängen daran).
2. Würfe über `Dice` ohne Seed (fallen nach dem Laden neu aus). In Tests `FixedDice` oder `MaxDice`.
3. Textvarianten über `Flavour` (eigener `Random`, verbraucht keine Würfe).

**Anzeige-Prinzip (`ConsoleIO`):** `report(text)` gibt ein Ergebnis sofort aus und merkt es sich. Beim nächsten `clearScreen()` (50 Leerzeilen) wird es oben als Block „Was zuletzt geschah“ wiederholt. Jedes Menü (`Game.chooseAction`, Kampfzug) beginnt mit `clearScreen()`, zeigt Raum, Beschreibung, Status und die Auswahl. Ergebnisse und Stimmungstexte immer mit `report`, Menüs und Prompts mit `println`.

**Spielablauf (`Game`):** `run()` ruft `playRoom()` je Raum. Ein Raum hat: gewöhnlichen Gegner beim Betreten (Raum 2–4), Interaktionen (jede einmal, Folge-Interaktionen), optional Statue (besonderer Gegner), Durchgang frei oder versperrt (erst „Hindernis untersuchen“, dann zwei Ansätze), Speichern. Raum 5: Trank liegt bereit, dann Endgegner. `RoomResult` = NEXT, SAVED, DEAD, WON. Trank und Zauber stehen in jedem Menü, im Kampf kosten sie den Zug.

**Raumskripte:** `src/main/resources/story/rooms/NAME.properties` (acht Vorlagen plus `room0` und `room5`). Format:

```properties
name=Die Bibliothek
description.1=...                 # 4 Zeilen Stimmung, in jedem Menü des Raums sichtbar
free.label=...  free.text=...     # Menüpunkt und Text, wenn der Durchgang frei ist
obstacle.label=...  obstacle.text=...   # Hindernis untersuchen (nur Text)
way.1.label=...  way.1.attribute=INTELLIGENCE  way.1.harder=false
way.1.success=...  way.1.failure=...    # zwei Ansätze, einer davon harder=true
poi.1.label=...  poi.1.text=...   # Stelle zum Untersuchen, Text immer sichtbar
poi.1.attribute=DEXTERITY         # optional: Check
poi.1.easier=true                 # optional: eine Stufe leichter
poi.1.effect=ITEM                 # NONE | ITEM | HINT | INSIGHT
poi.1.item=PROTECTION_AMULET      # optional: festes Item (Raum 0)
poi.1.class=ROGUE                 # optional: nur für diese Klasse (Raum 0)
poi.1.success=...  poi.1.failure=...    # Pflicht, wenn es einen Check gibt
poi.1.damage=true                 # optional: Fehlschlag kostet 1W4
poi.1.follow.label=...            # optional: Folge-Interaktion, gleiche Schlüssel
```
Jede Vorlage braucht genau eine Stelle mit `effect=ITEM`, mindestens einen HINT, einen INSIGHT, eine Verzweigung, eine kleine Falle und eine reine Stimmung (das prüft `RoomScriptTest`). Allgemeine Texte stehen in `story/flavour.properties` (Varianten `key.1`, `key.2`, …, Platzhalter `{enemy}`, `{room}`). `FlavourTest` prüft, dass alle benutzten Schlüssel existieren.

**Schwierigkeit der Checks beim Untersuchen:** Raum 0–1 leicht, Raum 2–3 mittel, Raum 4 schwer, `easier` eine Stufe darunter. Wege: Basis mittel, der schwerere Ansatz schwer. Freie Räume: Raum 1 immer, dazu Raum 2 oder 3. Raum 4 immer versperrt.

## 5. Spielwerte (Stand nach dem Feinschliff)

| | Magier | Schurke |
|---|---|---|
| Stärke/Geschick/Intelligenz | 1 / 2 / 5 | 2 / 5 / 1 |
| Lebenspunkte | 14 | 16 |
| Rüstungsklasse | 11 | 13 |
| Schaden | D6 | D10 |
| Trefferwurf | D20 + Intelligenz | D20 + Geschick |
| Besonderes | 3 Zauberladungen: Feuerball (D10, trifft immer), Schutzschild (+3 RK im Kampf), Arkane Sicht (+3 auf den nächsten Intelligenz-Check) | Hinterhalt: erster Treffer mit 2 Würfeln nach gelungenem Schleichen |

Gegner (HP/RK/Trefferbonus, Schaden immer D6): Skelett 6/9/+1, Gargoyle 10/11/+2, Der tote König 18/13/+2. Fallen: 1W6, Patzer 2W6, kleine Fallen beim Untersuchen 1W4. Heiltrank heilt 6 (in V1 genau einer, vor dem Endgegner). Erkenntnis +2 auf den nächsten Check im Raum. Inventar 5 Plätze, Ring des Lebens +3 maximale Lebenspunkte (hebt auch die Obergrenze 20 an). 10 % der Verstecke sind leer. Eine 1 misslingt immer, eine 20 gelingt immer (auch bei Angriffen).

**Balance-Messung:** Mit `tools/BalanceSim.java` (siehe unten) gewinnt ein einfacher Bot als Magier etwa 60–70 %, als Schurke etwa 90 %. Hauptgefahr: der Endgegner für den Magier. Der Autor findet die Schwierigkeit jetzt gut („ist besser“).

## 6. Tests

201 JUnit-Tests (JUnit 5), alle grün. Wichtige Hilfen in `src/test/java/dice/`: `FixedDice` (feste Würfelfolge), `MaxDice` (immer die höchste Zahl). Besondere Tests:
- `RandomPlaythroughTest`: 400 Spiele mit zufälligen Eingaben, prüft Abstürze und Endlosschleifen, dazu Speichern und Weiterspielen.
- `GameTest`: Räume, Fallen, Interaktionen, Hinweise, Speichern, ein kompletter Durchlauf bis zum Sieg (Schurke mit `MaxDice`).
- `RoomScriptTest`, `FlavourTest`: Vollständigkeit, keine Steuerzeichen, Länge der Texte.

Menünummern in Tests ändern sich, sobald ein Menüpunkt dazukommt. Die Tests rechnen die Nummern im Kommentar vor (z. B. Raum 0 als Magier: Runen 1, Innehalten 2, Fußspuren 3, Tor 4, Speichern 5, Zauber 6).

## 7. Balance-Werkzeug

`tools/BalanceSim.java` spielt den Dungeon mit einem Bot tausendfach durch (benutzt die echten Klassen, Zufalls-Seeds, Raumstruktur). Übersetzen und starten (nach `mvn compile`):

```powershell
$jdk = "C:\Users\Baltrom\.jdks\openjdk-27\bin"; $cp = (Resolve-Path "target\classes").Path
& "$jdk\javac.exe" -encoding UTF-8 -cp $cp -d "$env:TEMP\sim" tools\BalanceSim.java
& "$jdk\java.exe" -cp "$cp;$env:TEMP\sim" BalanceSim
```
Die Werte (Gegner, Falle, Lebenspunkte) stehen in der Datei als Zahlen und müssen bei Änderungen am Spiel von Hand nachgezogen werden.

## 8. Git-Stand und Dateien

- Alles committet und gepusht, nur der Branch `main` existiert (lokal und auf GitHub).
- Im Repository: `src/` (Code, Texte, Tests), `pom.xml`, `.gitignore` (ignoriert `target/`, `.idea/`, `out/`, `savegame.txt`, `highscores.txt`), `tools/`.
- Doku: `Readme.md`, `manual.md` (Bedienung), `features.md` (Häkchen), `Ideen.md` (Ideen, Entscheidungen, Feinschliff), `agents.md` (Regeln), `PromptDiary.md`, diese Datei.
- Vom Autor: `Prompt Diary.pdf`, `Ideen.zip` (älterer Stand der Dokumente). Nicht anfassen.
- Alte Spielstände aus der Zeit vor dem Feinschliff sind ungültig (anderes Format, werden als beschädigt gemeldet).

## 9. Wie es weitergeht (offen)

**Aktuell (Sitzung nach V1):** Der Autor will **jeden einzelnen Raum mit ihm durchgehen und ihm Charakter geben** (Raum 0, acht Vorlagen, Thronsaal), danach die Räume **personalisieren**. Ein Raum pro Schritt, erst besprechen, dann in `story/rooms/NAME.properties` schreiben. Steht als TODO in `Ideen.md`.

**Farben (erledigt):** `game.Ansi` färbt Raumname, Rahmen, Menünummern, Erfolg, Schaden, Funde, Zauber, RK und Nebensächliches. Standardmäßig aus (Tests unverändert), `Main` schaltet ein und stellt die Ausgabe auf UTF-8, `--plain` schaltet ab. Die nebeneinander stehenden Klassenkarten sind nicht eingefärbt (Auffüllen mit `String.format` zählt die Farbcodes mit). Start im IntelliJ-Terminal: `java -cp target/classes game.Main` nach Build Project. Alle Bilder (Titel, Magier, Schurke, Tor) werden über `game.ArtPainter` eingefärbt, die Klassenkarten sind jetzt auch farbig (`Ansi.padRight` füllt nach sichtbarer Länge auf). `tools/AnsiDemo.java` blättert mit Enter durch die Bilder (`java -cp target/classes tools/AnsiDemo.java` nach Build Project). **Raumbilder:** Jeder Raum (Raum 0, acht Vorlagen, Thronsaal) hat ein Bild in `ascii/` (`gate`, `library`, `crypt`, `statue_hall`, `trap_corridor`, `alchemy_lab`, `prison`, `cistern`, `armory`, `throne`), verknüpft über `extra.art=NAME` in der Raumdatei und angezeigt in `Game.chooseAction` unter der Raumüberschrift. Alle Bilder stehen in der Demo. Die Bilder sind mit einem Hilfsskript erzeugt (Spiegelung für die Symmetrie), danach von Hand weiter anpassbar. Arbeit liegt auf dem Branch `raumbilder`, noch nicht in `main`. Mögliche nächste Schritte: Bilder nach Autor-Rückmeldung nachbessern, Hilfe-Befehl, Raum-Durchgang (Charakter). 206 Tests grün.

**Umgebung auf diesem Rechner (Stand Sitzung zu Farben):** Benutzer `PC`, Projektordner `C:\wi26\Strukturiertes Programmieren\BWISTPRO\DungeonCrawler`, JDK `C:\Java\jdk-26`, Maven von IntelliJ unter `C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd` (`JAVA_HOME` auf das JDK setzen). Die Pfade im Abschnitt 3 oben stammen vom alten Rechner.

**Schon erledigt in dieser Sitzung:** Die Anzeige ist umgedreht. Jeder Bildschirm zeigt zuerst Raumname und Beschreibung, darunter „Was zuletzt geschah“, dann Status und Auswahl. Technisch: `ConsoleIO.clearScreenOnly()` + `showRecentResults()` (in `Game.chooseAction` und `Combat.playerTurn`); `clearScreen()` zeigt die Ergebnisse weiter direkt nach dem Leeren (Hauptmenü usw.). 202 Tests grün.

Außerdem wollte der Autor **in einer früheren Sitzung** die folgenden Punkte besprechen:
1. **Feinschliff-Liste** aus seinem Durchspielen (er hat sie noch nicht geschickt).
2. Ideen für **Version 2**, aus der bisherigen Sammlung:
   - mehr Räume und Raumvorlagen, mehr Items und mehr Heiltränke (in V1 nur einer),
   - Endgegner mit Spezialangriffen oder Phasen (in V1 „nur zäher“),
   - Rangliste mit Zweitkriterium (Lebenspunkte oder Züge), weil bei 5 Räumen viele Gleichstände entstehen,
   - **Hilfe-Befehl im Spiel** (Feature 18 in `features.md` ist als einziges noch offen),
   - mehr Flavourtext-Varianten, falls sie sich abnutzen.
3. Kleinigkeiten: Der Thronsaal (Raum 5) hat nichts zum Untersuchen. Items könnten klassenabhängig gewichtet werden (der Schurke findet einen für ihn nutzlosen Ladungstrank).

Offene Rückfragen an den Autor aus der letzten Sitzung: Texte und Ton passen? Reichen drei Stellen pro Raum? Sollen Hinweise und Erkenntnisse häufiger oder seltener sein? Soll der Thronsaal etwas zum Untersuchen bekommen?

## 10. Checkliste für den Abschluss jeder Sitzung

1. Tests laufen lassen (201 müssen grün sein).
2. `Ideen.md` (Entscheidungen), `features.md` (Häkchen), `manual.md` (falls sich die Bedienung ändert), diese Datei und `PromptDiary.md` aktualisieren.
3. Auf Ansage committen und `main` pushen, danach lokal und auf GitHub vergleichen (`git rev-parse HEAD`, `git rev-parse origin/main`).
4. Offene Fragen als nummerierte Liste ans Ende der Antwort.
