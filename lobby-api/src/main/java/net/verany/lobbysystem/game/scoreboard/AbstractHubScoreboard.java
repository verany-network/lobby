package net.verany.lobbysystem.game.scoreboard;

public abstract class AbstractHubScoreboard {

    public static final String[] DISPLAY_NAME = {"$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$f§lV$s§lerany", "$f§lVe$s§lrany", "$f§lVer$s§lany", "$f§lVera$s§lny", "$f§lVeran$s§ly", "$f§lVerany", "$f§lVerany", "$f§lVerany", "$s§lVerany", "$s§lVerany", "$f§lVerany", "$f§lVerany", "$s§lVerany", "$s§lVerany", "$f§lVerany", "$f§lVerany"};

    public abstract void load();

    public abstract void setScores();

    public abstract  void setDisplayName(int currentSlot);

    public abstract void addCurrentSide();

}
