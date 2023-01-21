import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerObject {
    public enum lockType {NL, WL, RL};

	private Client_itf ecrivain;

	private ArrayList<Client_itf> lecteurs;

	private lockType lock;

	private int id;

	public Object obj;

	public ServerObject(Object o, int id) {
		this.obj = o;
		this.id = id;
		this.lock = lockType.NL;
		this.ecrivain = null;
		this.lecteurs = new ArrayList<Client_itf>();
	}

	// invoked by the user program on the client node
	public void lock_read(Client_itf client) {
		try {
			if (lock == lockType.WL) {
				obj = ecrivain.reduce_lock(id);
				lecteurs.add(ecrivain);
			}
			ecrivain = null;
			lecteurs.add(client);
			lock = lockType.RL;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// invoked by the user program on the client node
	public void lock_write(Client_itf client) {
		if (lock == lockType.RL) {
			lecteurs.remove(client);
			for (Client_itf lecteur : lecteurs) {
				try {
					lecteur.invalidate_reader(id);
				} catch (RemoteException e) {
					e.printStackTrace();
				}	
			}
		}

		if (lock == lockType.WL) {
			try {
				obj = ecrivain.invalidate_writer(id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		lecteurs.clear();
		ecrivain = client;
		lock = lockType.WL;
	}
}
