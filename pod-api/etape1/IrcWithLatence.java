import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class IrcWithLatence extends Frame {
	public TextArea		text;
	public TextField	data;
	Sentence_itf		sentence;
	static String		myName;
	static long readLatence, writeLatence;

	public static void main(String argv[]) {
		
		if (argv.length != 3) {
			System.out.println("java Irc <name> <read_latence> <write_latence>");
			return;
		}
		myName = argv[0];
		readLatence = Long.parseLong(argv[1]);
		writeLatence = Long.parseLong(argv[2]);
		
	
		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		Sentence_itf s = (Sentence_itf)Client.lookup("IRC");
		if (s == null) {
			s = (Sentence_itf)Client.create(new Sentence());
			Client.register("IRC", s);
		}
		// create the graphical part
		new IrcWithLatence(s, readLatence, writeLatence);
	}

	public IrcWithLatence(Sentence_itf s, long readLatence, long writeLatence) {
	
		setLayout(new FlowLayout());
	
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);
	
		data=new TextField(60);
		add(data);
	
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListenerLatence(this, writeLatence));
		add(write_button);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListenerLatence(this, readLatence));
		add(read_button);
		
		setSize(470,300);
		text.setBackground(Color.black); 
		show();		
		
		sentence = s;
	}
}



class readListenerLatence implements ActionListener {
	IrcWithLatence irc;
	long readLatence;
	public readListenerLatence (IrcWithLatence i, long readLatence) {
		irc = i;
		this.readLatence = readLatence;
	}
	public void actionPerformed (ActionEvent e) {
		
		// lock the object in read mode
		irc.sentence.lock_read();
		
		try {
			Thread.sleep(readLatence * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// invoke the method
		String s = irc.sentence.read();
		
		// unlock the object
		irc.sentence.unlock();
		
		// display the read value
		irc.text.append(s+"\n");
	}
}

class writeListenerLatence implements ActionListener {
	IrcWithLatence irc;
	long writeLatence;
	public writeListenerLatence (IrcWithLatence i, long writeLatence) {
        	irc = i;
        	this.writeLatence = writeLatence;
	}
	public void actionPerformed (ActionEvent e) {
		
		// get the value to be written from the buffer
        String s = irc.data.getText();
        	
        	// lock the object in write mode
		irc.sentence.lock_write();
		
		try {
			Thread.sleep(writeLatence * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// invoke the method
		irc.sentence.write(IrcWithLatence.myName+" wrote "+s);
		irc.data.setText("");
		
		// unlock the object
		irc.sentence.unlock();
	}
}



