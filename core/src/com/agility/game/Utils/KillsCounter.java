package com.agility.game.Utils;

public class KillsCounter {

    @Deprecated
    private KillsCounter() {
    }

    private static int killsInCurrentGame;
    private static int totalKills;

    public static void addKill() {
        killsInCurrentGame++;
        totalKills++;
    }

    public static int getKillsInCurrentGame() {
        return killsInCurrentGame;
    }

    public static void setKillsInCurrentGame(int killsInCurrentGame) {
        KillsCounter.killsInCurrentGame = killsInCurrentGame;
    }

    public static void setTotalKills(int totalKills) {
        KillsCounter.totalKills = totalKills;
    }

    public static void refreshGameKills() {
        killsInCurrentGame = 0;
    }
}
