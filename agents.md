# agents.md – Anweisungen für KI-Agenten

## Projekt
Textbasierter Dungeon Crawler in Java. Der Spieler wählt eine Klasse (z. B. Mage, Rogue) und erlebt eine Geschichte mit RNG-basierten Skillchecks (D20, D10, D6).

Siehe auch:
- `Ideen.md` – erste Ideen, getroffene Entscheidungen und offene Fragen
- `features.md` – nummerierte Feature-Liste

## Grundregeln
- Sprache: Java, Konsolenanwendung, keine GUI.
- Code-Bezeichner auf Englisch, Story-Texte und Dokumentation auf Deutsch.
- Einfach und lesbar halten, das Projekt ist ein Studienprojekt (Einführung Software Engineering).
- Nur das umsetzen, was gefragt ist. Keine ungefragten Refactorings oder neuen Abhängigkeiten.
- Vor größeren Änderungen an der Struktur kurz nachfragen.
- Konsolenausgabe nur in ASCII (ae/oe/ue statt Umlauten).
- Bildschirm „leeren“ nur über eine zentrale Methode `clearScreen()`, die viele Leerzeilen ausgibt.

## Code-Konventionen
- Klassen in `PascalCase`, Methoden und Variablen in `camelCase`, Konstanten in `UPPER_SNAKE_CASE`.
- Eine Klasse pro Datei, sinnvolle Pakete (z. B. `game`, `character`, `dice`, `story`).
- Story-Text und Spiellogik trennen. Texte nicht tief in der Logik verstecken.
- Würfel zentral in einer eigenen Klasse (z. B. `Dice`), damit Würfe testbar sind (z. B. mit festem Seed).
- Kurze Kommentare nur dort, wo die Absicht nicht offensichtlich ist.

## Tests
- Neue Logik (Würfel, Skillchecks, Kampf) mit JUnit-Tests abdecken.
- Tests müssen vor dem Abschluss einer Aufgabe laufen.

## Arbeitsweise für Agenten
1. Relevante Dateien lesen, bevor etwas geändert wird.
2. Kleine, nachvollziehbare Änderungen machen.
3. Bei Unklarheiten (z. B. Eingabesystem: Menü oder Freitext) nachfragen, statt zu raten.
4. Nach Änderungen kurz zusammenfassen, was geändert wurde.
5. Keine Dateien löschen oder überschreiben, ohne es vorher anzusprechen.

## Offene Entscheidungen
- Eingabesystem: Auswahlmenü (a/b/c/d) oder Freitext mit Schlagwörtern
- Umfang des Dungeons (Anzahl Klassen ist entschieden: 2, Magier und Schurke)
- Welche Features gehören in die erste Version (MVP)
