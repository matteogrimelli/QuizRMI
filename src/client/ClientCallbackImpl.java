package client;

import common.model.LobbyState;
import common.model.PlayerInfo;
import common.remote.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
    private static final long serialVersionUID = 1L;

    private volatile LobbyState currentLobbyState;

    public ClientCallbackImpl() throws RemoteException {
        super();
    }

   @Override
    public void onLobbyUpdated(LobbyState lobbyState) throws RemoteException {
        this.currentLobbyState = lobbyState;

        System.out.println();
        System.out.println("=== AGGIORNAMENTO LOBBY ===");
        System.out.println("Lobby: " + lobbyState.getLobbyId());
        System.out.println("Owner session: " + lobbyState.getOwnerSessionId());
        System.out.println("Started: " + lobbyState.isStarted());
        System.out.println("Giocatori:");

        for (PlayerInfo player : lobbyState.getPlayers()) {
            System.out.println("- " + player.getName() + " | score=" + player.getScore());
        }

        System.out.println("===========================");
        System.out.print("Scegli comando: create | join | state | start | ping | exit\n");
    }

    @Override
    public void onMessage(String message) throws RemoteException {
        System.out.println("\n[MESSAGGIO SERVER] " + message + "\n");
    }

    public LobbyState getCurrentLobbyState() {
        return currentLobbyState;
    }
}