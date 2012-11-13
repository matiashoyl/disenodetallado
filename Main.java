import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) throws Exception {
		
		Communication c = new Communication(6005);
		new Thread(c).start();
		c.connectToSession();
		
		while(true)
		{
			/* Prueba para probar como se envian los mensajes
			 * El Command Line esta constantemente esperando input
			 * y lo envia al nodo especificado a traves del metodo
			 * senMessage (Para probar se debe descomentar y comentar el resto)
			 */
			BufferedReader inFromUser = 
					new BufferedReader(new InputStreamReader(System.in));

			String sentenceConsole = inFromUser.readLine();
			
			c.sendMessage("2_"+sentenceConsole, 6003);
			
			
			/* Prueba para probar como se envian los objetos
			 *  (Para probar se debe descomentar y comentar el resto)
			 */
			/*
			BufferedReader inFromUser = 
					new BufferedReader(new InputStreamReader(System.in));

			String sentenceConsole = inFromUser.readLine();
			
			if(sentenceConsole.equalsIgnoreCase("enviar"))
			{
				MyTest test = new MyTest();
				c.sendObject(test, 6003);
			}
			else if(sentenceConsole.equalsIgnoreCase("recibir"))
			{
				MyTest test = (MyTest)c.getObject();
				System.out.println("Si el numero es 60, entonces esta funcionando.");
				System.out.println("Numero: "+test.getNumero());
			}
			*/
		}

	}
}