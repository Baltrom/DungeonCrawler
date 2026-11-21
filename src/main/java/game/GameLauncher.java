package game;

import character.Player;

/** Startet ein Spiel fuer einen frisch erstellten Spieler. Erlaubt es, das Menue ohne echtes Spiel zu testen. */
@FunctionalInterface
public interface GameLauncher {

    GameResult play(Player player);
}
