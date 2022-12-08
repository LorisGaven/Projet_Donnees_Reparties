import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf {

    private HashMap<Integer,ServerObject> objects;

    protected Server() throws RemoteException {
        objects = new HashMap<Integer,ServerObject>();
    }

    @Override
    public int lookup(String name) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void register(String name, int id) throws RemoteException {
        
    }

    @Override
    public int create(Object o) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object lock_read(int id, Client_itf client) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object lock_write(int id, Client_itf client) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    

    public static void main(String[] args) throws MalformedURLException, RemoteException, AlreadyBoundException {
        try {
            Registry registre = LocateRegistry.createRegistry(4000);
        } catch (RemoteException e) {
        }
        Naming.rebind("//localhost:4000/" + args[0], new Server());
    }

}
