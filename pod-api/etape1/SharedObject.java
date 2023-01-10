import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class SharedObject implements Serializable, SharedObject_itf {

	public enum lockType {NL, RLC, WLC, RLT, WLT, RLT_WLC};

	private lockType lock;

	public Object obj;

	private int id;

	public SharedObject(Object o, int id) {
		this.obj = o;
		this.lock = lockType.NL;
		this.id = id;
	}

	// invoked by the user program on the client node
	public void lock_read() {
	}

	// invoked by the user program on the client node
	public void lock_write() {
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		lock = lockType.NL;
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		return null;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
	}

	public synchronized Object invalidate_writer() {
		return null;
	}

	public int getId() {
		return id;
	}
}
