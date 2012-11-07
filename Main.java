import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) throws Exception {
		
		Comunication c = new Comunication(6005);
		new Thread(c).start();
		c.connectToSession();
		
		while(true)
		{
			BufferedReader inFromUser = 
					new BufferedReader(new InputStreamReader(System.in));

			String sentenceConsole = inFromUser.readLine();
			
			c.sendMessage("2_"+sentenceConsole, 6003);
		}

	}
}