import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) throws Exception {
		
		//Creamos un nuevo comunicador pasándole como parámetro su id
		Comunication c = new Comunication(6005);
		//Comenzamos el thread que levantará el servidor
		new Thread(c).start();
		//Intentamos conectarnos a la sesión
		c.connectToSession();
		
		//Loop infinito que recibe input del teclado y lo envía al nodo indicado
		while(true)
		{
			BufferedReader inFromUser = 
					new BufferedReader(new InputStreamReader(System.in));

			String sentenceConsole = inFromUser.readLine();
			
			//Ejemplo de uso del metodo para enviar mensaje. El número dos indica el caracter de mensaje normal
			c.sendMessage("2_"+sentenceConsole, 6003);
		}

	}
}