package space.mcplay.lobby.options;

public class PlayerLobbyOptions {

  private boolean playerVisible, serverStartInfo, doubleJump, lobbyAtDay;

  public PlayerLobbyOptions(boolean playerVisible, boolean serverStartInfo, boolean doubleJump, boolean lobbyAtDay) {
    this.playerVisible = playerVisible;
    this.serverStartInfo = serverStartInfo;
    this.doubleJump = doubleJump;
    this.lobbyAtDay = lobbyAtDay;
  }

  public PlayerLobbyOptions withPlayerVisibility(boolean playerVisible) {
    this.playerVisible = playerVisible;
    return this;
  }

  public PlayerLobbyOptions withServerStartInfo(boolean serverStartInfo) {
    this.serverStartInfo = serverStartInfo;
    return this;
  }

  public PlayerLobbyOptions withDoubleJump(boolean doubleJump) {
    this.doubleJump = doubleJump;
    return this;
  }

  public PlayerLobbyOptions withLobbyAtDay(boolean lobbyAtDay) {
    this.lobbyAtDay = lobbyAtDay;
    return this;
  }

  public boolean isPlayerVisible() {
    return this.playerVisible;
  }

  public boolean isServerStartInfo() {
    return this.serverStartInfo;
  }

  public boolean isDoubleJump() {
    return this.doubleJump;
  }

  public boolean isLobbyAtDay() {
    return this.lobbyAtDay;
  }
}
