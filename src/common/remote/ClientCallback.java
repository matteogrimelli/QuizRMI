package common.remote;

import common.model.LobbyState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {

    void onLobbyUpdated(LobbyState lobbyState) throws RemoteException;

    void onMessage(String message) throws RemoteException;
}