import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static HashMap<Integer,SharedObject> objects;

	private static Client instance;

	private static Server_itf s;

	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		if (Client.instance == null) {
			try {
				Client.instance = new Client();
				s = (Server_itf) Naming.lookup("//localhost:4000/server");
				objects = new HashMap<Integer,SharedObject>();
			} catch (RemoteException | MalformedURLException | NotBoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		int id;
		try {
			id = s.lookup(name);
			if (id == -1) {
				return null;
			} else {
				SharedObject object = new SharedObject(s.lock_read(id, Client.instance), id);
				objects.put(id, object);
				return object;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			s.register(name, ((SharedObject) so).getId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		int id;
		try {
			id = s.create(o);
			SharedObject object = new SharedObject(o, id);
			objects.put(id, object);
			return object;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		try {
			s.lock_read(id, instance);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return objects.get(id).obj;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		try {
			s.lock_write(id, instance);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return objects.get(id).obj;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		objects.get(id).reduce_lock();
		return objects.get(id).obj;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		objects.get(id).invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		objects.get(id).invalidate_writer();
		return objects.get(id).obj;
	}
}
