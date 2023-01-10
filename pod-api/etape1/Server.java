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

    private ArrayList<ServerObject> objects;

    private HashMap<String, Integer> registre;

    protected Server() throws RemoteException {
        objects = new ArrayList<ServerObject>();
        registre = new HashMap<String, Integer>();
    }

    @Override
    public int lookup(String name) throws RemoteException {
        if (registre.containsKey(name)) {
            return registre.get(name);
        } else {
            return -1;
        }
    }

    @Override
    public void register(String name, int id) throws RemoteException {
        if (registre.containsKey(name)) {
            System.out.println("L'objet existe déjà");
        } else {
            registre.put(name, id);
        }
    }

    @Override
    public int create(Object o) throws RemoteException {
        objects.add(new ServerObject(o));
        return objects.size()-1;
    }

    @Override
    public Object lock_read(int id, Client_itf client) throws RemoteException {
        // A compléter
        return objects.get(id).obj;
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
        Naming.rebind("//localhost:4000/server", new Server());
    }

    public ArrayList<ServerObject> getObjects() {
        return objects;
    }

}
