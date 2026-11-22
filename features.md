# Features – Dungeon Crawler

Stand Version 1: **[x]** = umgesetzt und getestet, **[ ]** = noch nicht umgesetzt.

1. [x] **Hauptmenü** (Neues Spiel, Spiel laden, Rangliste anzeigen, Beenden)
2. [x] **Klassenauswahl** zu Spielbeginn (Magier, Schurke) mit ASCII-Figur, Attributen und Lebenspunkten, Bestätigung („Bist du sicher?“) und anschließender Namenseingabe (1–15 Zeichen, nicht leer, nur Buchstaben und Zahlen)
3. [x] **Charakterwerte / Attribute** pro Klasse (Stärke, Geschick, Intelligenz, Skala 1–5) als Grundlage für Skillchecks
4. [x] **Storytelling-Ausgabe** (düstere Beschreibung von Räumen und Ereignissen in der Gruft eines toten Königs; eigener Intro-Text pro Klasse: Schurke wegen eines Auftrags, Magier aus Neugier)
5. [x] **Eingabesystem** (Auswahl über nummerierte Optionen, z. B. 1–3 oder 1–5)
6. [x] **Würfelsystem (RNG)** mit D20, D10 und D6, Wurfergebnis wird angezeigt
7. [x] **Skillchecks** (D20 + Attribut gegen Schwierigkeitswert; 1 = immer Fehlschlag, 20 = immer Erfolg; nicht wiederholbar)
8. [x] **Klassenspezifische Aktionen** (Magier: Runen lesen, 3 gemeinsame Zauberladungen für Feuerball, Schutzschild und Arkane Sicht, „Zauber wirken“ in jedem Auswahlmenü mit Untermenü; Schurke: Schloss knacken, Falle entschärfen, Akrobatik)
9. [x] **Raum- und Dungeon-Struktur** (zufällig aus einem Seed erzeugt; Raum 0 liegt draußen vor dem Tor und zählt nicht, Raum 5 ist der Endgegner)
10. [x] **Lebenspunkte und Statusanzeige** des Spielers (Start Magier 14, Schurke 16; durch den Ring erhöhbar; Trank trinken in jedem Auswahlmenü)
11. [x] **Kampfsystem** (rundenbasiert, selten; Treffer = D20 + Attribut gegen Rüstungsklasse; Münzwurf für den Beginn, ein gelungener Schleichen- (Geschick) oder Spähen-Check (Intelligenz) lässt den Spieler beginnen; kein Fliehen; Schaden D6 Magier, D10 Schurke; Hinterhalt des Schurken nach gelungenem Schleichen: doppelter Schaden beim ersten Treffer; höchstens drei Kämpfe in Version 1)
12. [x] **Gegner** mit eigenen Werten (Lebenspunkte, Rüstungsklasse, Trefferbonus, Schaden): gewöhnlicher Gegner, besonderer Gegner (z. B. Statue oder Gargoyle, wird durch Untersuchen aktiviert, freiwilliger Kampf), **Endgegner** (in Version 1 nur zäher); Magier hat Rüstungsklasse 11, Schurke 13
13. [x] **Inventar** (5 Plätze, passive Wirkung, kein Anlegen; Tränke und Schlüssel zählen mit, bei vollem Inventar wählt der Spieler, was wegfällt; ein abgeworfenes Item ist endgültig weg)
14. [x] **Gegenstände und Loot** (Tränke, Ringe und andere passive Items, Magischer Dolch, Schlüssel; Beute-Checks mit kleiner Chance auf kein Item; besondere Gegner geben immer ein Item; Heiltrank heilt 6 Lebenspunkte)
15. [x] **Fallen und Rätsel** (Weg-Checks mit 2 wählbaren Ansätzen in versperrten Räumen; bei Fehlschlag 1W6 Schaden, bei einer 1 2W6; Untersuchen mit kleiner Falle 1W4; Schwierigkeit steigt mit der Raumnummer; nichts blockiert den Weg dauerhaft)
16. [x] **Mehrere Enden** (Sieg und Tod; beim Tod endet das Spiel mit Eintrag in die Rangliste)
17. [x] **Speichern und Laden** des Spielstands (Seed, Raum, Lebenspunkte, Items, Name, Klasse und Raumfortschritt; beim Laden wird der alte Spielstand gelöscht)
18. [ ] **Hilfe-Befehl** im Spiel (Erklärung der Steuerung und Regeln). Die Bedienung steht bisher nur in [manual.md](manual.md).
19. [x] **Rangliste (Top 10)** nach Anzahl erreichter Räume, bei Gleichstand steht der frühere Eintrag höher (später zusätzlich Lebenspunkte/Züge), Eintrag nur nach Tod oder Sieg, gespeichert in einer Datei
20. [x] **Untersuchen mit Verzweigungen** (freie und versperrte Räume, Hindernis untersuchen, Folge-Interaktionen, Hinweise auf den nächsten Raum, Erkenntnis +2; Flavourtext zu jeder Aktion, auch Kampf, Funde und Tränke)

21. [x] **Farbige Ausgabe (ANSI)** im Terminal-Tab von IntelliJ (Text mit Farben nach fester Palette, `--plain` schaltet sie ab, Klasse `game.Ansi`)
22. [x] **Farbige ASCII-Bilder** (Titel, Magier, Schurke) und **ein Bild für jeden Raum** (Raum 0, acht Vorlagen, Thronsaal), erscheint beim Betreten des Raums; `tools/AnsiDemo.java` blättert mit Enter durch alle Bilder

## Erste lauffähige Version (MVP) – umgesetzt
Menü, Klassenwahl mit ASCII-Bildern, 5 Räume, Würfelmechanik (Wurfergebnis wird angezeigt), Skillchecks, Kampf, Items, Rangliste, Speichern und Laden.

Alles andere (z. B. Hilfe-Befehl im Spiel, mehr Räume, mehr Items, Endgegner mit Spezialangriffen) kommt später.
