package server;

import common.model.GameState;
import common.model.LobbyState;
import common.model.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class LobbyData {
    private final String lobbyId;
    private final String ownerSessionId;
    private final List<String> playerSessionIds;
    private boolean started;
    private GameState gameState;

    public LobbyData(String lobbyId, String ownerSessionId) {
        this.lobbyId = lobbyId;
        this.ownerSessionId = ownerSessionId;
        this.playerSessionIds = new ArrayList<>();
        this.playerSessionIds.add(ownerSessionId);
        this.started = false;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getOwnerSessionId() {
        return ownerSessionId;
    }

    public List<String> getPlayerSessionIds() {
        return playerSessionIds;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean containsPlayer(String sessionId) {
        return playerSessionIds.contains(sessionId);
    }

    public void addPlayer(String sessionId) {
        if (!playerSessionIds.contains(sessionId)) {
            playerSessionIds.add(sessionId);
        }
    }

    public LobbyState toLobbyState(List<PlayerInfo> players) {
        return new LobbyState(lobbyId, ownerSessionId, players, started);
    }

    public GameState getGameState() {
    return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    
    public void removePlayer(String sessionId) {
        playerSessionIds.remove(sessionId);
    }
}