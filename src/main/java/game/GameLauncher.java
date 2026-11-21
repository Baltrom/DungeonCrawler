package game;

import character.Player;
import save.SaveGame;

/** Startet ein neues oder gespeichertes Spiel. Erlaubt es, das Menue ohne echtes Spiel zu testen. */
public interface GameLauncher {

    GameResult play(Player player);

    GameResult resume(SaveGame save);
}
