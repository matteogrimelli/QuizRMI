package server;

import common.remote.QuizService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//Implementazione concreta del servizio remoto.
public class QuizServiceImpl extends UnicastRemoteObject implements QuizService {

	public QuizServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String ping(String name) throws RemoteException {
        System.out.println("Ricevuta chiamata ping dal client con nome: " + name);
        return "Ciao " + name + ", il server RMI funziona correttamente!";
    }
	
}
