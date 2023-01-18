public class Sentence1_stub extends SharedObject implements Sentence1_itf, java.io.Serializable {

	public Sentence1_stub(Object o, int id) {
		super(o, id);
	}
	public void write(String arg0) {
		Sentence1 s = (Sentence1) obj;
		s.write(arg0);
	}

	public String read() {
		Sentence1 s = (Sentence1) obj;
		return s.read();
	}

}