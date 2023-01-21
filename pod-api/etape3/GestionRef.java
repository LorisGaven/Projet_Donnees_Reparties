public class GestionRef {
	
	private static boolean client = false;
	
	public static boolean isClient() {
		return client;
	}
	
	public static void setClient(boolean client) {
		GestionRef.client = client;
	}
}
