package server;

import common.remote.QuizService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

//Classe main del server.
//Avvia il registry RMI e pubblica il servizio remoto.
public class QuizServerMain {

	 public static void main(String[] args) {
	        try {
	            // Avvia il registro RMI sulla porta standard 1099
	            LocateRegistry.createRegistry(1099);

	            System.out.println("Registry RMI avviato.");

	            // Crea il servizio remoto
	            QuizService service = new QuizServiceImpl();

	            // Pubblica il servizio
	            Naming.rebind("rmi://localhost/QuizService", service);

	            System.out.println("Server RMI avviato correttamente.");
	            System.out.println("Servizio disponibile su: rmi://localhost/QuizService");

	        } catch (Exception e) {
	            System.err.println("Errore nell'avvio del server:");
	            e.printStackTrace();
	        }
	    }
	
}
