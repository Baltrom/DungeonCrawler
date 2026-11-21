# Features – Dungeon Crawler

1. **Hauptmenü** (Neues Spiel, Spiel laden, Rangliste anzeigen, Beenden)
2. **Klassenauswahl** zu Spielbeginn (Magier, Schurke) mit ASCII-Figur, Attributen und Lebenspunkten, Bestätigung („Bist du sicher?“) und anschließender Namenseingabe (1–15 Zeichen, nicht leer, nur Buchstaben und Zahlen)
3. **Charakterwerte / Attribute** pro Klasse (Stärke, Geschick, Intelligenz, Skala 1–5) als Grundlage für Skillchecks
4. **Storytelling-Ausgabe** (Beschreibung von Räumen und Ereignissen)
5. **Eingabesystem** (Auswahl über nummerierte Optionen, z. B. 1–3 oder 1–5)
6. **Würfelsystem (RNG)** mit D20, D10 und D6, Wurfergebnis wird angezeigt
7. **Skillchecks** (D20 + Attribut gegen Schwierigkeitswert; 1 = immer Fehlschlag, 20 = immer Erfolg; nicht wiederholbar)
8. **Klassenspezifische Aktionen** (Magier: Runen lesen, 3 gemeinsame Zauberladungen für Feuerball, Schutzschild und Arkane Sicht, „Zauber wirken“ in jedem Auswahlmenü mit Untermenü; Schurke: Schloss knacken, Falle entschärfen, Akrobatik)
9. **Raum- und Dungeon-Struktur** (zufällig aus einem Seed erzeugt; Raum 0 liegt draußen vor dem Tor und zählt nicht, Raum 5 ist der Endgegner)
10. **Lebenspunkte und Statusanzeige** des Spielers (max. 20, durch den Ring erhöhbar; Trank trinken in jedem Auswahlmenü)
11. **Kampfsystem** (rundenbasiert, selten; Treffer = D20 + Attribut gegen Rüstungsklasse; Münzwurf für den Beginn, ein gelungener Schleichen- (Geschick) oder Spähen-Check (Intelligenz) lässt den Spieler beginnen; kein Fliehen; Schaden D6 Magier, D10 Schurke)
12. **Gegner** mit eigenen Werten (u. a. Rüstungsklasse), inklusive **Endgegner**; auch Magier und Schurke haben eine Rüstungsklasse
13. **Inventar** (5 Plätze, passive Wirkung, kein Anlegen; Tränke und Schlüssel zählen mit, bei vollem Inventar wählt der Spieler, was wegfällt; ein abgeworfenes Item ist endgültig weg)
14. **Gegenstände und Loot** (Waffen, Tränke, Schlüssel)
15. **Fallen und Rätsel** (ein Check, der für den Weg in den nächsten Raum nötig ist, ist eine Falle, die bei Fehlschlag tödlich sein kann; nichts blockiert den Weg dauerhaft)
16. **Mehrere Enden** (Sieg, Tod, Game Over)
17. **Speichern und Laden** des Spielstands (Seed, Raum, Lebenspunkte, Items, Name, Klasse; beim Laden wird der alte Spielstand gelöscht)
18. **Hilfe-Befehl** (Erklärung der Steuerung und Regeln)
19. **Rangliste (Top 10)** nach Anzahl erreichter Räume, bei Gleichstand steht der frühere Eintrag höher (später zusätzlich Lebenspunkte/Züge), Eintrag nur nach Tod oder Sieg, gespeichert in einer Datei

## Erste lauffähige Version (MVP)
Menü, Klassenwahl mit ASCII-Bildern, 5 Räume, Würfelmechanik (Wurfergebnis wird angezeigt), Skillchecks, Kampf, Items, Rangliste, Speichern und Laden.

Alles andere (z. B. Hilfe-Befehl, Fallen und Rätsel in größerem Umfang, mehr Räume) kommt später.
