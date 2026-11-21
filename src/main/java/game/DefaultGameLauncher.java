package game;

import character.Player;
import dice.Dice;
import dungeon.DungeonGenerator;
import java.util.Random;
import save.SaveGame;
import save.SaveManager;

/** Der echte Spielstart: ein frischer Dungeon aus einem zufaelligen Seed, Wuerfe ohne Seed. */
public class DefaultGameLauncher implements GameLauncher {

    private final ConsoleIO io;
    private final SaveManager saveManager;

    public DefaultGameLauncher(ConsoleIO io, SaveManager saveManager) {
        this.io = io;
        this.saveManager = saveManager;
    }

    @Override
    public GameResult play(Player player) {
        long seed = new Random().nextLong();
        return new Game(io, new Dice(), player, DungeonGenerator.generate(seed), saveManager).run();
    }

    @Override
    public GameResult resume(SaveGame save) {
        return Game.fromSave(io, new Dice(), save, saveManager).run();
    }
}
