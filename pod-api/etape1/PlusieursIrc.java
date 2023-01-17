import java.util.ArrayList;

public class PlusieursIrc {
	static int			nbLecteurs;
	static int			nbEcrivains;
	static int			intervalleLecture;
	static int 			intervalleEcriture;

	static ArrayList<Lecteur> lecteurs;
	static ArrayList<Ecrivain> ecrivains;

	public static void main(String argv[]) {
		
		lecteurs = new ArrayList<Lecteur>();
		ecrivains = new ArrayList<Ecrivain>();

		if (argv.length != 4) {
			System.out.println("java PlusieursIrc <nbLecteurs> <intervalleLectures> <nbEcrivains> <intervalleEcriture>");
			return;
		}
		nbLecteurs = Integer.parseInt(argv[0]);
		intervalleLecture = Integer.parseInt(argv[1]);
		nbEcrivains = Integer.parseInt(argv[2]);
		intervalleEcriture = Integer.parseInt(argv[3]);
		
		for (int i = 1; i <= nbLecteurs; i++) {
			Lecteur l = new Lecteur("Lecteur" + i, intervalleLecture);
			lecteurs.add(l);
			l.start();
		}

		for (int i = 1; i <= nbEcrivains; i++) {
			Ecrivain e = new Ecrivain("Ecrivain" + i, intervalleEcriture);
			ecrivains.add(e);
			e.start();
		}
	}
}


class Lecteur extends Thread {

	private String nom;
	private int attente;
	private SharedObject s;

	public Lecteur(String nom, int attente) {
		super();
		this.nom = nom;
		this.attente = attente;
		Client.init();
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		s = Client.lookup("IRC");
		if (s == null) {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				/* Attente de [0, intervalleLecteur] s */
				sleep((long) (1000 * (attente + Math.random())));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(nom + " -> demande lockRead");
			s.lock_write();
			System.out.println(nom + " -> lockRead accepté");

			try {
				/* Attente de attente de  [0,1] s */
				sleep((long) (1000 * Math.random()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(nom + " -> lit : \"" + ((Sentence) s.obj).read() + "\"");
			 

			s.unlock();
			System.out.println(nom + " -> unlock");
		}
	}
}

class Ecrivain extends Thread {

	private String nom;
	private int attente;
	private SharedObject s;

	public Ecrivain(String nom, int attente) {
		this.nom = nom;
		this.attente = attente;
		Client.init();
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		s = Client.lookup("IRC");
		if (s == null) {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				/* Attente de [0, intervalleEcriture] s */
				sleep((long) (1000 * Math.random() * attente));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(nom + " -> demande lockWrite");
			s.lock_write();
			System.out.println(nom + " -> lockWrite accepté");

			try {
				/* Attente de attente de  [0,1] s */
				sleep((long) (1000 * Math.random()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long msg = Math.round(Math.random() * 100 + 1);
			((Sentence) s.obj).write(nom + " a écrit " + msg);
			System.out.println(nom + " -> écrit : \"" + msg + "\"");

			s.unlock();
			System.out.println(nom + " -> unlock");
		}
	}
}



