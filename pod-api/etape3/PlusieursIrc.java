import java.util.ArrayList;

public class PlusieursIrc {
	static int			nbLecteurs;
	static int			nbEcrivains;
	static int			intervalleLecture;
	static int 			intervalleEcriture;
	static int			tempsTest;

	static ArrayList<Lecteur> lecteurs;
	static ArrayList<Ecrivain> ecrivains;

	public static void main(String argv[]) {

		lecteurs = new ArrayList<Lecteur>();
		ecrivains = new ArrayList<Ecrivain>();

		if (argv.length != 5) {
			System.out.println("java PlusieursIrc <nbLecteurs> <intervalleLectures> <nbEcrivains> <intervalleEcriture> <tempsTest>");
			return;
		}
		nbLecteurs = Integer.parseInt(argv[0]);
		intervalleLecture = Integer.parseInt(argv[1]);
		nbEcrivains = Integer.parseInt(argv[2]);
		intervalleEcriture = Integer.parseInt(argv[3]);
		tempsTest = Integer.parseInt(argv[4]);

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
		
		try {
			Thread.sleep(tempsTest * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("------ Nombre de lectures / ecritures ------");
		for (Lecteur l :lecteurs) {
			l.interrupt();
		}
		for (Ecrivain e : ecrivains) {
			e.interrupt();
		}
		
	}
}


class Lecteur extends Thread {

	private String nom;
	private int attente;
	private SharedObject s;
	private int nbLectures = 0;

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
		try {
			while (true) {
				/* Attente de [0, intervalleLecteur] s */
				sleep((long) (1000 * (attente + Math.random())));

				s.lock_write();

				nbLectures++;

				/* Attente de attente de  [0,1] s */
				sleep((long) (1000 * Math.random()));

				s.unlock();
			}
		} catch (InterruptedException e) {
			System.out.println(nom + " : " + nbLectures);
		}
	}
}

class Ecrivain extends Thread {

	private String nom;
	private int attente;
	private SharedObject s;
	private int nbEcritures = 0;

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
		try {
			while (true) {

				/* Attente de [0, intervalleEcriture] s */
				sleep((long) (1000 * Math.random() * attente));


				s.lock_write();

				nbEcritures++;


				/* Attente de attente de  [0,1] s */
				sleep((long) (3000 * Math.random()));


				long msg = Math.round(Math.random() * 100 + 1);
				((Sentence) s.obj).write(nom + " a écrit " + msg);

				s.unlock();
			}
		} catch (InterruptedException e) {
			System.out.println(nom + " : " + nbEcritures);
		}
	}
}


