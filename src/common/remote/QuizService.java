package common.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

//Interfaccia remota minima per testare che RMI funzioni.
public interface QuizService extends Remote {
	
    //Metodo remoto di test.
    //Il client lo invocherà sul server.
    String ping(String name) throws RemoteException;
    
}
