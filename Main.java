public class Main {
	public static void main(String[] args) throws Exception {
		
		Nodo n1 = new Nodo(6009);
		new Thread(n1).start();
		n1.escribirMensaje();

	}
}