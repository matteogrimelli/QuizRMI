package common.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.model.LobbyState;

//Interfaccia remota minima per testare che RMI funzioni.
public interface QuizService extends Remote {
	
    //Metodo remoto di test.
    //Il client lo invocherà sul server.
    //String ping(String name) throws RemoteException;

    String createSession(String playerName, ClientCallback callback) throws RemoteException;

    String createLobby(String sessionId) throws RemoteException;

    LobbyState joinLobby(String sessionId, String lobbyId) throws RemoteException;

    LobbyState getLobbyState(String sessionId) throws RemoteException;

    void startLobby(String sessionId) throws RemoteException;

    String ping(String name) throws RemoteException;
    
    void selectQuestion(String sessionId, String category, int value) throws RemoteException;

    void disconnect(String sessionId) throws RemoteException;

    String leaveLobby(String sessionId) throws RemoteException;

}
