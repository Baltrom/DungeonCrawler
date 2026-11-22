# Dungeon Crawler

Ein textbasiertes Abenteuer in Java: Du erkundest die Gruft eines toten Königs, entscheidest über nummerierte Menüs und lässt den Würfeln (D20, D10, D6) das letzte Wort.

**Projektname (Idee):** Donjons and maybe Drajons
**Autor:** Nikolaus Koch wi26b166
**Entstanden mit:** Claude Code (siehe [PromptDiary.md](PromptDiary.md))

## Spielprinzip

- Du wählst eine Klasse: **Magier** (stark in Intelligenz, 3 Zauberladungen) oder **Schurke** (stark in Geschick, Hinterhalt).
- Du durchquerst fünf Räume. Jeder Dungeon wird beim Start zufällig aus einem Seed erzeugt. In Raum 5 wartet der Endgegner.
- Skillchecks (D20 + Attribut gegen einen Zielwert) entscheiden, ob du etwas findest, ein Hindernis überwindest oder in eine Falle läufst.
- Kämpfe sind selten, aber gefährlich. Schleichen und Spähen verschaffen dir den ersten Zug.
- Du trägst höchstens fünf Items. Gespeichert wird per Menüpunkt, die besten Läufe stehen in einer Rangliste.

Ausführliche Bedienung: [manual.md](manual.md)

## Schnellstart

1. Projekt in IntelliJ öffnen, Maven-Projekt laden („Load Maven Project“ an der `pom.xml`).
2. Übersetzen: **Build → Build Project** (Strg+F9).
3. Unten das **Terminal**-Tab öffnen und eingeben: `java -cp target/classes game.Main`
4. Zahlen eingeben und mit Enter bestätigen.

Das Spiel ist farbig (ANSI) und braucht dafür das **Terminal-Tab**, nicht die Run-Konsole. Ohne Farben: `java -cp target/classes game.Main --plain`.

**Einstellungen in IntelliJ, falls nötig:**
- *Settings → Editor → Color Scheme → Console Font*: „Line spacing“ auf **1.0** (sonst Lücken im Bild).
- Schrift mit fester Breite (Standard: JetBrains Mono).
- *Settings → Editor → File Encodings*: Global und Project Encoding auf **UTF-8**.

Voraussetzung: Java 21 oder neuer. Tests: `mvn test` oder in IntelliJ „Run All Tests“. Einzelheiten und Fehlersuche stehen in [manual.md](manual.md).

## Stand

Version 1 ist umgesetzt: Hauptmenü, Klassenauswahl mit ASCII-Bildern, 5 Räume, Würfelmechanik mit angezeigtem Wurfergebnis, Skillchecks, Kampf, Items, Rangliste, Speichern und Laden. Welche Features fertig sind, zeigt [features.md](features.md) mit Häkchen.

## Projektstruktur

```
src/main/java/
  character/   Klassen, Spieler, Attribute, Zauber
  check/       Skillchecks und Schwierigkeitsstufen
  combat/      Gegnerwerte
  dice/        zentrale Würfelklasse
  dungeon/     Raumvorlagen, Erzeugung aus dem Seed
  game/        Spielablauf, Kampf, Menüs, Konsole
  highscore/   Rangliste
  item/        Items und Inventar
  save/        Spielstand
  story/       Laden von Bildern und Texten
src/main/resources/
  ascii/       ASCII-Bilder der Klassen und das Titelbild (nur ASCII, Farbe kommt aus dem Code)
  story/       Intro- und Endtexte, Raumskripte (rooms/), Stimmungstexte
src/test/java/ JUnit-Tests (über 200)
```

Texte stehen in Dateien unter `src/main/resources/story/`, nicht im Code, und können dort angepasst werden.

## Dokumentation

| Datei | Inhalt |
|---|---|
| [manual.md](manual.md) | Bedienung des Spiels |
| [features.md](features.md) | Feature-Liste mit Stand |
| [Ideen.md](Ideen.md) | Ideen, Entscheidungen und Feinschliff |
| [agents.md](agents.md) | Anweisungen für KI-Agenten |
| [PromptDiary.md](PromptDiary.md) | Protokoll der Zusammenarbeit mit Claude Code |
| [Uebergabe.md](Uebergabe.md) | Projektstand und alles, was ein neuer Chat zum Weiterarbeiten braucht |

## Die ursprüngliche Projektidee

> Ich möchte einen textbasierten Dungeoncrawler bauen.
> Über die Ausmaße bin ich mir noch nicht sicher.
> Auch bin ich mir noch nicht sicher ob ich es über if/else statements das abenteuer baue :
> Sprich : text output(storytelling) dann prompt an den user : chose a b c d
> oder ob ich es über freitexteingabe mache und java nach schlagwörtern suchen lasse um intention des spielers zu "erraten"
> es wird einen RNG geben, mit DnDähnlichen größen: D20, D10, D6
> Verschiedene klassen möchte ich dem spieler am anfang zur auswahl geben, mit unterschiedlichen attributen für die RNG based "skillchecks"
> so kann ein "mage" vll alte runen lesen und ein "rogue" ein schloss knacken, eine falle entschärfen oder akkrobatik checks amchen die der "alte magier" niemals hinkriegt.

Entschieden wurde für nummerierte Auswahlmenüs (Freitext ist nicht geplant), zwei Klassen und eine Rangliste.
