import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

interface RemoteInterface extends Remote {
    String greet(String name) throws RemoteException;
}

class RemoteImplementation extends UnicastRemoteObject implements RemoteInterface {
    protected RemoteImplementation() throws RemoteException {
        super();
    }

    @Override
    public String greet(String name) throws RemoteException {
        return "Greetings, " + name + "!";
    }
}

public class DistributedSystemApp {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java DistributedSystemApp <server/client>");
            return;
        }

        String role = args[0];

        if (role.equalsIgnoreCase("server")) {
            runServer();
        } else if (role.equalsIgnoreCase("client")) {
            runClient();
        } else {
            System.out.println("Invalid argument. Please specify 'server' or 'client'.");
        }
    }

    public static void runServer() {
        try {
            RemoteInterface service = new RemoteImplementation();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("GreetingService", service);

            System.out.println("Server is up and running.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runClient() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            RemoteInterface service = (RemoteInterface) registry.lookup("GreetingService");

            String response = service.greet("User");
            System.out.println("Server replied: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

