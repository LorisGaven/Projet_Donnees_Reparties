import java.io.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class SharedObject implements Serializable, SharedObject_itf {

	public ReentrantLock moniteur; 
	public Condition accesPossible;

	public enum lockType {NL, RLC, WLC, RLT, WLT, RLT_WLC};

	private boolean attendre;

	private lockType lock;

	public Object obj;

	private int id;

	public SharedObject(Object o, int id) {
		this.moniteur = new ReentrantLock();
		this.accesPossible = moniteur.newCondition();
		this.obj = o;
		this.lock = lockType.NL;
		this.id = id;
		this.attendre = false;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		moniteur.lock();

		while (attendre) {
			try {
				accesPossible.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		boolean modifier = false;
		switch(lock) {
		case NL :
			lock = lockType.RLT;
			modifier = true;
			break;
		case RLC:
			lock = lockType.RLT;
			break;
		case WLC:
			lock = lockType.RLT_WLC;
			break;
		default:
			break;
		}
		moniteur.unlock();
		if (modifier) {
			obj = Client.lock_read(id);
		}

	}

	// invoked by the user program on the client node
	public void lock_write() {
		moniteur.lock();

		while (attendre) {
			try {
				accesPossible.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		boolean modifier = lock == lockType.NL || lock == lockType.RLC;
		if (modifier || lock == lockType.WLC) {
			lock = lockType.WLT;
		}
		moniteur.unlock();
		if (modifier) {
			obj = Client.lock_write(id);
		}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		moniteur.lock();
		if (lock == lockType.RLT) {
			lock = lockType.RLC;
		} else if (lock == lockType.WLT || lock == lockType.RLT_WLC) {
			lock = lockType.WLC;
		}
		//accesPossible.signal();
		notify();
		moniteur.unlock();
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		//moniteur.lock();

		attendre = true;

		while (lock == lockType.WLT || lock == lockType.RLT_WLC) {
			try {
				/*accesPossible.a*/wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (lock == lockType.WLC) {
			lock = lockType.RLC;
		} else if (lock == lockType.RLT_WLC) {
			lock = lockType.RLT;
		}
		
		attendre = false;
		//accesPossible.signal();
		notify();
		//moniteur.unlock();
		return obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		//moniteur.lock();
		
		attendre = true;
		
		while (lock == lockType.RLT) {
			try {
				/*accesPossible.a*/wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (lock == lockType.RLC) {
			lock = lockType.NL;
		}
		
		attendre = false;
		//accesPossible.signal();
		notify();
		//moniteur.unlock();
	}

	public synchronized Object invalidate_writer() {
		//moniteur.lock();
		
		attendre = true;
		
		while (lock == lockType.WLT || lock == lockType.RLT_WLC) {
			try {
				/*accesPossible.a*/wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (lock == lockType.WLC) {
			lock = lockType.NL;	
		}
		
		attendre = false;
		//accesPossible.signal();
		notify();
		//moniteur.unlock();
		return obj;
	}

	public int getId() {
		return id;
	}
}
