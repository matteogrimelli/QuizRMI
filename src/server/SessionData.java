package server;

import common.remote.ClientCallback;

public class SessionData {
    private final String sessionId;
    private final String playerName;
    private final ClientCallback callback;
    private String lobbyId;
    private int score;

    public SessionData(String sessionId, String playerName, ClientCallback callback) {
        this.sessionId = sessionId;
        this.playerName = playerName;
        this.callback = callback;
        this.score = 0;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ClientCallback getCallback() {
        return callback;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getScore() {
        return score;
    }
}