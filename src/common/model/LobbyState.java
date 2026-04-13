package common.model;

import java.io.Serializable;
import java.util.List;

public class LobbyState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String lobbyId;
    private final String ownerSessionId;
    private final List<PlayerInfo> players;
    private final boolean started;

    public LobbyState(String lobbyId, String ownerSessionId, List<PlayerInfo> players, boolean started) {
        this.lobbyId = lobbyId;
        this.ownerSessionId = ownerSessionId;
        this.players = players;
        this.started = started;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getOwnerSessionId() {
        return ownerSessionId;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public boolean isStarted() {
        return started;
    }
}