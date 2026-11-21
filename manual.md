# Handbuch – Dungeon Crawler

Ein textbasiertes Abenteuer in der Gruft eines toten Königs. Du wählst eine Klasse, erkundest fünf Räume und besiegst am Ende den Endgegner. Würfel entscheiden über Erfolg und Misserfolg.

## 1. Starten

**In IntelliJ (empfohlen):**
1. Projektordner öffnen. Beim ersten Mal rechts oben im Editor oder an der `pom.xml` **„Load Maven Project“** klicken, damit die Testbibliothek geladen wird.
2. `src/main/java/game/Main.java` öffnen.
3. Den grünen Pfeil neben `public static void main` klicken und **Run 'Main.main()'** wählen.
4. Unten im Fenster **Run** in die Konsole klicken und die Eingaben tippen.

**Tests ausführen:** In IntelliJ Rechtsklick auf `src/test/java` und **Run All Tests**, oder mit Maven `mvn test`.

**Voraussetzung:** Java 21 oder neuer (getestet mit JDK 27).

Beim Spielen entstehen im Arbeitsordner zwei Dateien: `savegame.txt` (Spielstand) und `highscores.txt` (Rangliste).

## 2. Bedienung

- Du steuerst alles mit **Zahlen und Enter**. Zu jedem Menü gehören nummerierte Punkte, z. B. `[1] Neues Spiel`. Tippe die Zahl und drücke Enter.
- Eine ungültige Eingabe wird einfach erneut abgefragt („Bitte eine Zahl von 1 bis 4 eingeben.“).
- Vor jedem neuen Bildschirm werden viele Leerzeilen ausgegeben, damit der alte Text aus dem Blick verschwindet. Du kannst nach oben scrollen, wenn du etwas nachlesen willst.
- **Ganz oben auf jedem neuen Bildschirm** steht der Block **„Was zuletzt geschah“**: Würfe, Treffer, Funde und Texte deiner letzten Aktion.
- Es gibt keine Sonderzeichen und keine Umlaute in der Ausgabe (ae, oe, ue), damit die Windows-Konsole nichts verstümmelt.

## 3. Hauptmenü

| Punkt | Bedeutung |
|-------|-----------|
| 1 Neues Spiel | Klasse wählen, Namen eingeben, in die Gruft gehen |
| 2 Spiel laden | Setzt das gespeicherte Spiel fort (siehe Abschnitt 10) |
| 3 Rangliste anzeigen | Die Top 10 mit den meisten erreichten Räumen |
| 4 Beenden | Programm beenden |

## 4. Neues Spiel: Klasse und Name

1. **Klasse wählen.** Beide Klassen stehen nebeneinander mit Bild, Attributen, Lebenspunkten und Rüstungsklasse. Tippe `1` für den Magier oder `2` für den Schurken.
2. **Bestätigen.** „Bist du sicher?“ – `1` Ja, `2` Nein (zurück zur Auswahl).
3. **Namen eingeben.** 1 bis 15 Zeichen, nur Buchstaben (ohne Umlaute) und Zahlen. Keine Leerzeichen, keine Sonderzeichen, nicht leer.

Danach folgt ein kurzer Einleitungstext, der je nach Klasse anders ist (Schurke: ein Auftrag, Magier: Neugier).

### Die Klassen

| | Magier | Schurke |
|---|---|---|
| Stärke | 1 | 2 |
| Geschick | 2 | 5 |
| Intelligenz | 5 | 1 |
| Lebenspunkte | 14 | 16 |
| Rüstungsklasse | 11 | 13 |
| Schaden | D6 | D10 |
| Trefferwurf mit | Intelligenz | Geschick |
| Besonderes | 3 Zauberladungen | Hinterhalt |

## 5. Würfel und Checks

Fast alles entscheidet ein **Check**: Du würfelst einen D20, addierst das passende Attribut und vergleichst mit dem Zielwert.

`Wurf: 14 + 5 Geschick = 19 gegen 12 -> ERFOLG`

- Erreicht oder übertrifft die Summe den Zielwert, ist es ein **Erfolg**.
- Eine **1** misslingt **immer** (Patzer), eine **20** gelingt **immer**.
- Ein Check lässt sich **nicht wiederholen**.

| Schwierigkeit | Zielwert |
|---|---|
| leicht | 6–8 |
| mittel | 10–12 |
| schwer | 14–18 |

Bei jeder Wahl mit Check steht in Klammern das Attribut und die Schwierigkeit, z. B. `(Intelligenz, leicht)`.

## 6. Spielablauf

Der Dungeon wird bei jedem neuen Spiel zufällig erzeugt. Du beginnst in **Raum 0** vor dem Tor der Gruft. Danach folgen **Raum 1 bis 4** und in **Raum 5** der Endgegner. Raum 0 zählt nicht als erreichter Raum.

### Der Bildschirm

Jeder Bildschirm zeigt: Raumname und Beschreibung, deine Werte (Lebenspunkte, Rüstungsklasse, bei Magiern die Zauberladungen), dein Inventar und die Auswahl.

### Im Raum: Dinge untersuchen

In jedem Raum gibt es einige Stellen, die du untersuchen kannst (z. B. ein Regal, einen Toten, eine Schatulle). Jede Stelle machst du nur **einmal**. Was dabei herauskommt:

- **Ein Fund** (ein Item, in etwa jedem zehnten Versteck ist nichts).
- **Ein Hinweis:** Er verrät, was im nächsten Raum wartet (ein Gegner, eine Statue, der Endgegner oder der Name des Raums) und ob der Durchgang dort versperrt ist.
- **Eine Erkenntnis:** Dein nächster Check in diesem Raum bekommt **+2**.
- **Nur Stimmung.**
- Manche Stellen haben eine kleine Falle: Bei einem Fehlschlag verlierst du **1W4** Lebenspunkte.
- Manche Stellen führen zu einer **zweiten Wahl** (z. B. „Die Kratzspuren genauer ansehen“). Sie erscheint erst, wenn du die erste Stelle untersucht hast.

### Der Weg weiter

- **Freier Durchgang** (Raum 1 ist immer frei, dazu Raum 2 oder 3): Du wählst „Weitergehen“, es gibt keinen Check.
- **Versperrter Durchgang:** Wähle zuerst „Das Hindernis untersuchen“ (kein Wurf, nur Text). Dann erscheinen zwei Ansätze mit Attribut und Schwierigkeit. Bei Erfolg kommst du durch. Bei einem **Fehlschlag löst eine Falle aus**: **1W6** Schaden, bei einem Patzer (1) **2W6**. Du kommst trotzdem weiter, solange du die Falle überlebst.
- Hast du einen **Eisernen Schlüssel**, kannst du ein Hindernis ohne Wurf öffnen. Der Schlüssel ist danach weg.

## 7. Gegner und Kampf

Kämpfe sind selten. Es gibt höchstens drei:

- Ein **Skelett** in einem der Räume 2 bis 4 steht beim Betreten im Raum.
- Eine **Gargoyle-Statue**, die du freiwillig weckst (Menüpunkt „Die Statue in der Nische untersuchen“). Sie gibt dir immer ein Item.
- Der **Endgegner** in Raum 5 („Der tote König“). Das ist der einzige Pflichtkampf.

### Wer beginnt?

Bei einem Gegner im Raum wählst du:

1. **Schleichen** (Geschick) – bei Erfolg beginnst du. Als Schurke macht dein **erster Treffer doppelten Schaden** (Hinterhalt).
2. **Spähen** (Intelligenz) – bei Erfolg beginnst du.
3. **Sofort angreifen** – ein Münzwurf entscheidet, wer beginnt.

Misslingt Schleichen oder Spähen, entscheidet der Münzwurf.

### Ein Kampfzug

Du wählst zwischen:

- **Angreifen:** Du würfelst D20 + Trefferattribut (Magier: Intelligenz, Schurke: Geschick) gegen die **Rüstungsklasse** (RK) des Gegners. Bei einem Treffer würfelst du Schaden (Magier D6, Schurke D10).
- **Trank trinken** (nur, wenn du einen Trank hast).
- **Zauber wirken** (nur Magier).

Trank und Zauber kosten deinen Zug. **Fliehen ist nicht möglich.** Die Gegner greifen jede Runde an: D20 + ihr Trefferbonus gegen **deine** Rüstungsklasse. Auch hier misslingt eine 1 immer und eine 20 gelingt immer.

Fallen deine Lebenspunkte auf 0, ist das Spiel zu Ende.

| Gegner | Lebenspunkte | RK | Trefferbonus | Schaden |
|---|---|---|---|---|
| Skelett | 6 | 9 | +1 | D6 |
| Gargoyle | 10 | 11 | +2 | D6 |
| Der tote König | 18 | 13 | +2 | D6 |

Vor dem Endgegner liegt am Eingang von Raum 5 ein **Heiltrank** bereit.

## 8. Zauber (nur Magier)

Der Magier hat **3 gemeinsame Zauberladungen** pro Spiel. Ein normaler Angriff mit dem Zauberstab kostet nichts, jeder Zauber eine Ladung. Im Menü steht „Zauber wirken“ mit einem Untermenü.

| Zauber | Wirkung | Wann |
|---|---|---|
| Feuerball | D10 Schaden, trifft immer | im Kampf |
| Schutzschild | +3 Rüstungsklasse bis zum Ende des Kampfes | im Kampf |
| Arkane Sicht | +3 auf deinen nächsten Intelligenz-Check | außerhalb des Kampfes |

Ladungen füllt nur der **Ladungstrank** wieder auf.

## 9. Items und Inventar

Du kannst **5 Items** tragen. Jedes Item belegt einen Platz, auch Tränke und Schlüssel. Passive Items wirken, sobald du sie hast. Man muss sie nicht anlegen, und ihre Boni addieren sich.

| Item | Wirkung |
|---|---|
| Heiltrank | Heilt 6 Lebenspunkte (aktiv) |
| Ladungstrank | Füllt die Zauberladungen des Magiers auf (aktiv) |
| Ring des Lebens | +3 maximale Lebenspunkte (auch über 20), selten |
| Ring der Klugheit | +1 Intelligenz |
| Stiefel der Schatten | +1 Geschick |
| Kraftgürtel | +1 Stärke |
| Schutzamulett | +1 Rüstungsklasse |
| Magischer Dolch | +1 auf Treffer und +1 Schaden |
| Eiserner Schlüssel | Öffnet ein Hindernis ohne Wurf, danach weg |

- **Trank trinken** steht in **jedem Menü**, sobald du einen Trank hast, den du brauchen kannst.
- Ist dein Inventar voll, wählst du, ob und welches Item wegfällt. Ein weggeworfenes Item ist endgültig weg.
- In Raum 0 findest du je nach Klasse eine Starthilfe: Der Magier einen Ladungstrank, der Schurke ein Schutzamulett.
- Items bekommst du beim Untersuchen und von der Gargoyle. Es gibt in Version 1 nur einen Heiltrank (vor dem Endgegner).

## 10. Speichern und Laden

- Im Menü eines Raums (Raum 0 bis 4) gibt es **„Speichern und zum Hauptmenue“**. Gespeichert werden Dungeon, Raum, Lebenspunkte, Ladungen, Items, Name, Klasse und was im Raum schon passiert ist. Im Kampf und im Endgegner-Raum kannst du nicht speichern.
- **Spiel laden** im Hauptmenü setzt das Spiel fort. Dabei wird der alte Spielstand **gelöscht** (kein Zurückspulen). Wer weiterspielen will, speichert neu.
- Es gibt nur **einen** Spielstand. Wer neu speichert, überschreibt den alten.
- Die Würfe fallen nach dem Laden neu aus.

## 11. Rangliste

- Gewertet wird die **Anzahl erreichter Räume** (Raum 0 zählt nicht, höchstens 5).
- Der Eintrag erfolgt **nur nach Tod oder Sieg**, nicht beim Speichern.
- Bei Gleichstand steht der **frühere** Eintrag höher.
- Es werden die **Top 10** gespeichert. Spieler mit gleichem Namen bekommen getrennte Einträge.
- Nach Tod oder Sieg wird zuerst die Rangliste ergänzt, dann der Spielstand gelöscht.

## 12. Tipps

- Untersuche die Räume gründlich. Hinweise sagen dir, was als Nächstes kommt, Erkenntnisse und Arkane Sicht verbessern deine Chancen bei schweren Checks.
- Wähle bei einem versperrten Durchgang den Ansatz, der zu deinem Attribut passt (Magier: Intelligenz, Schurke: Geschick). Der Stärke-Ansatz ist meist schwerer.
- Hebe den Heiltrank für den Endgegner auf.
- Als Magier sparst du Feuerbälle für den Endkampf.
- Als Schurke lohnt sich Schleichen vor jedem Kampf.

## 13. Probleme

| Problem | Lösung |
|---|---|
| Der grüne Pfeil fehlt | Maven-Projekt laden: Rechtsklick auf `pom.xml`, Maven, Reload project |
| „Der Spielstand ist beschaedigt“ | Der Spielstand stammt aus einer älteren Version. `savegame.txt` im Projektordner löschen |
| Sonderzeichen in der Konsole | Das Spiel gibt nur ASCII aus. Die Dateien sollten als UTF-8 gespeichert sein |
| Rangliste zurücksetzen | `highscores.txt` im Arbeitsordner löschen |
