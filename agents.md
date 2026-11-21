# agents.md – Anweisungen für KI-Agenten

## Projekt
Textbasierter Dungeon Crawler in Java. Der Spieler wählt eine Klasse (z. B. Mage, Rogue) und erlebt eine Geschichte mit RNG-basierten Skillchecks (D20, D10, D6).

Siehe auch:
- `Uebergabe.md` – **zuerst lesen**: Projektstand, Arbeitsweise des Autors, Umgebung, Architektur, offene Punkte
- `Ideen.md` – erste Ideen, getroffene Entscheidungen und offene Fragen
- `features.md` – nummerierte Feature-Liste mit Häkchen
- `manual.md` – Bedienung des Spiels, `PromptDiary.md` – Protokoll der Zusammenarbeit

Am Ende jeder Sitzung `Uebergabe.md`, `PromptDiary.md`, `Ideen.md` und `features.md` aktualisieren, damit ein neuer Chat nahtlos weiterarbeiten kann.

## Grundregeln
- Sprache: Java, Konsolenanwendung, keine GUI.
- Code-Bezeichner auf Englisch, Story-Texte und Dokumentation auf Deutsch.
- Einfach und lesbar halten, das Projekt ist ein Studienprojekt (Einführung Software Engineering).
- Nur das umsetzen, was gefragt ist. Keine ungefragten Refactorings oder neuen Abhängigkeiten.
- Vor größeren Änderungen an der Struktur kurz nachfragen.
- Konsolenausgabe nur in ASCII (ae/oe/ue statt Umlauten).
- Story-Ton: düster (Gruft eines toten Königs). Raumvorlagen und Details stehen in `Ideen.md`.
- Bildschirm „leeren“ nur über eine zentrale Methode `clearScreen()`, die viele Leerzeilen ausgibt.

## Code-Konventionen
- Klassen in `PascalCase`, Methoden und Variablen in `camelCase`, Konstanten in `UPPER_SNAKE_CASE`.
- Eine Klasse pro Datei, sinnvolle Pakete (z. B. `game`, `character`, `dice`, `story`).
- Story-Text und Spiellogik trennen. Texte nicht tief in der Logik verstecken.
- Würfel zentral in einer eigenen Klasse (z. B. `Dice`), damit Würfe testbar sind (z. B. mit festem Seed).
- Zwei getrennte Zufallsgeneratoren (`Random`): einer für die Dungeon-Erzeugung (Seed, wird gespeichert), einer für die Würfe. Die Würfe im Spiel werden nicht mit dem Seed erzeugt, sondern fallen jedes Mal neu aus. Ein fester Seed für Würfe ist nur für Tests gedacht.
- Kurze Kommentare nur dort, wo die Absicht nicht offensichtlich ist.

## Projektstruktur
Maven-Projekt (Java 21, JUnit 5). Pakete in `src/main/java`:
- `character` – Klassen (Magier, Schurke), Spieler, Attribute, Zauber
- `check` – Skillchecks (D20 + Bonus), Schwierigkeitsstufen
- `combat` – Gegnerwerte (der Kampfablauf selbst ist `game.Combat`)
- `dice` – `Dice`, zentrale Würfelklasse
- `dungeon` – Raumvorlagen, Dungeon-Erzeugung aus dem Seed
- `game` – Spielablauf (`Game`), Hauptmenü, Klassenauswahl, Konsolen-Ein-/Ausgabe
- `highscore` – Rangliste (Top 10), `save` – Spielstand
- `item` – Items und Inventar
- `story` – lädt ASCII-Bilder und Story-Texte aus `src/main/resources/` (`ascii/`, `story/`); `Flavour` wählt zufällig eine Textvariante

Räume und Texte:
- Jede Raumvorlage ist eine Datei `src/main/resources/story/rooms/NAME.properties` (`RoomScript` liest sie). Dort stehen Beschreibung, Hindernis, Ansätze und die Dinge zum Untersuchen (`poi.N.*`, optional mit `follow`). Neue Räume oder Texte gehören in diese Dateien, nicht in den Code.
- Allgemeine Texte (Kampf, Funde, Fallen, Hinweise) stehen in `story/flavour.properties` mit Varianten `key.1`, `key.2`, ...
- Alle Texte nur in ASCII (ae/oe/ue/ss). Die Tests `RoomScriptTest` und `FlavourTest` prüfen, dass nichts fehlt und alles ASCII ist.

Zum Spielen `game.Main` starten. Beim Spielen entstehen `savegame.txt` und `highscores.txt` im Arbeitsordner (stehen in `.gitignore`).

## Tests
- Neue Logik (Würfel, Skillchecks, Kampf) mit JUnit-Tests abdecken.
- Tests müssen vor dem Abschluss einer Aufgabe laufen.
- Ausführen: in IntelliJ über den grünen Pfeil, oder mit dem von IntelliJ mitgebrachten Maven (`mvn test`). Auf der Kommandozeile ist weder `java` noch `mvn` im PATH, das JDK liegt unter `~/.jdks/openjdk-27`.
- Für vorhersagbare Würfe in Tests gibt es `FixedDice` (feste Folge) und `MaxDice` (immer die höchste Zahl) in `src/test/java/dice/`.

## Arbeitsweise für Agenten
1. Relevante Dateien lesen, bevor etwas geändert wird.
2. Kleine, nachvollziehbare Änderungen machen.
3. Bei Unklarheiten (z. B. Eingabesystem: Menü oder Freitext) nachfragen, statt zu raten.
4. Nach Änderungen kurz zusammenfassen, was geändert wurde.
5. Keine Dateien löschen oder überschreiben, ohne es vorher anzusprechen.

## Stand und Entscheidungen
Version 1 ist umgesetzt (Details in `Ideen.md`, `features.md`, Bedienung in `manual.md`):
- Eingabesystem: nummerierte Auswahl (1–3, 1–5), kein Freitext
- Zwei Klassen: Magier und Schurke, Attribute 1–5, Magier 3 Zauberladungen
- 5 Räume (Raum 0 = draußen) aus einem Seed, freie und versperrte Räume, Untersuchen mit Verzweigungen, Hinweise und Erkenntnis
- Würfelmechanik mit angezeigtem Wurfergebnis, Skillchecks, Kampf, Items (5 Plätze), Rangliste, Speichern und Laden
- Flavourtext zu jeder Aktion; Ergebnisse stehen im nächsten Textblock nach dem Leeren des Bildschirms
- Balance nach dem Feinschliff: Magier 14 Lebenspunkte, Falle 1W6 (Patzer 2W6), schwächere Gegner

Noch offen (nächste Besprechung):
- Feinschliff-Liste aus dem nächsten Durchspielen
- Spätere Versionen: mehr Räume und Raumvorlagen, mehr Items und Heiltränke, Endgegner mit Spezialangriffen, Zweitkriterium in der Rangliste (Lebenspunkte/Züge), Hilfe-Befehl im Spiel
