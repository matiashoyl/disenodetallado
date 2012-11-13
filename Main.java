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
			/*BufferedReader inFromUser = 
					new BufferedReader(new InputStreamReader(System.in));

			String sentenceConsole = inFromUser.readLine();
			
			c.sendMessage("2_"+sentenceConsole, 6003);
			*/
			
			/* Prueba para probar como se envian los objetos
			 *  (Para probar se debe descomentar y comentar el resto)
			 */
			BufferedReader inFromUser = 
					new BufferedReader(new InputStreamReader(System.in));

			String sentenceConsole = inFromUser.readLine();
			
			/* Posibles tests:
			 * enviarLarry
			 * enviarMaria
			 * recibirNombre
			 * recibirEdad
			 * recibirSexo
			 */
			if(sentenceConsole.equalsIgnoreCase("enviarLarry"))
			{
				Persona persona = new Persona("Larry",34,"Masculino");
				c.sendObject(persona, 6003);
			}
			else if(sentenceConsole.equalsIgnoreCase("enviarMaria"))
			{
				Persona persona = new Persona("Maria",29,"Femenino");
				c.sendObject(persona, 6003);
			}
			else if(sentenceConsole.equalsIgnoreCase("recibirNombre"))
			{
				Persona persona = (Persona) c.getObject();
				System.out.println("Nombre: "+persona.getNombre());
			}
			else if(sentenceConsole.equalsIgnoreCase("recibirEdad"))
			{
				Persona persona = (Persona) c.getObject();
				System.out.println("Edad: "+persona.getEdad());
			}
			else if(sentenceConsole.equalsIgnoreCase("recibirSexo"))
			{
				Persona persona = (Persona) c.getObject();
				System.out.println("Sexo: "+persona.getSexo());
			}
			
		}

	}
}