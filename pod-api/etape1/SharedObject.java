import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	public enum lockType {NL, RLC, WLC, RLT, WLT, RLT_WLC};

	private lockType lock;

	public Object obj;

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
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
	}

	public synchronized Object invalidate_writer() {
	}
}
