public class Sentence1 implements java.io.Serializable {
	String 		data;
	public Sentence1() {
		data = new String("");
	}
	
	public void write(String text) {
		data = text;
	}
	public String read() {
		return data;	
	}
	
}