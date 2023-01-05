import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static ArrayList<SharedObject> objects;

	public Client() throws RemoteException {
		super(); 
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		objects = new ArrayList<SharedObject>();
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		try {
			Server_itf s = (Server_itf) Naming.lookup("//localhost:4000/server");
			int id = s.lookup(name);
			if (id == -1) {
				return null;
			} else {
				return objects.get(id);
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return null;
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			Server_itf s = (Server_itf) Naming.lookup("//localhost:4000/server");
			objects.add((SharedObject) so);
			s.register(name, s.create(((SharedObject) so).obj));
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		return new SharedObject(o);
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		return null;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		return null;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		return null;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		return null;
	}
}
