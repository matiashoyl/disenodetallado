import java.io.*; 
import java.net.*;
import java.util.*;

public class Comunication implements Runnable
{
	private int id;
	private ArrayList<Integer> nodos;
	private ServerSocket welcomeSocket;

	public Comunication(int id) throws Exception
	{
		nodos = new ArrayList<Integer>();
		this.id = id;		
		welcomeSocket = new ServerSocket(id);
	}

	//Metodo que retorna true si logra conectarse a la red y false si no es posible
	public void connectToSession() throws Exception
	{
		boolean flag = false;
		//Buscamos la senal de broadcast en los 20 puertos que soporta el sistema
		System.out.println("Buscando señal de broadcast");
		for(int i=6000 ; i<=6020 ;i++)
		{			
			if(i!=id)
			{
				System.out.println("Buscando en puerto: "+i);
				//sendMessage("0",i); //0 = Solicitar Llegada
				if(testPort("0",i))
				{
					flag=true;
					updateEveryonesList();
					break;
				}
			}
		}
		if(flag)
			System.out.println("Nodo "+id+" te has conectado exitosamente a la red");
		else
			System.out.println("Eres el primero en la red");
	}

	public void updateEveryonesList() throws Exception
	{
		Iterator<Integer> i = nodos.iterator();
		while(i.hasNext())
		{
			sendMessage("_3_"+listaNodosToString(),i.next());
		}
	}

	public void updateList(String listaNodos)
	{
		nodos.clear();
		getListaNodos(listaNodos);
	}

	public String listaNodosToString()
	{
		String listString = "";

		for (int s : nodos)
		{
			listString += Integer.toString(s) + ",";
		}

		return listString;
	}

	public void sendMessage(String Message, int idDestino) throws Exception
	{
		try
		{
			Socket clientSocket = new Socket("127.0.0.1", idDestino);

			DataOutputStream outToServer = 
					new DataOutputStream(clientSocket.getOutputStream());

			outToServer.writeBytes(id+"_"+Message);		

			clientSocket.close(); 
		}
		catch(ConnectException e)
		{
			System.out.println("No fue posible enviar el mensaje");
		}
	}

	public boolean testPort(String Message, int idDestino) throws Exception
	{
		try
		{
			Socket clientSocket = new Socket("127.0.0.1", idDestino);

			DataOutputStream outToServer = 
					new DataOutputStream(clientSocket.getOutputStream());

			outToServer.writeBytes(id+"_"+Message);		

			clientSocket.close();
			return true;
		}
		catch(ConnectException e)
		{
			return false;
		}
	}

	public boolean checkResponse()
	{
		try
		{				
			String clientSentence = "";		
			welcomeSocket.setSoTimeout(6);
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = 
					new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			if(inFromClient.ready())
				clientSentence = inFromClient.readLine();

			System.out.println(clientSentence);

			if(clientSentence.compareTo("") != 0 && clientSentence.substring(5, 6).compareTo("LlegadaAceptada") == 0)
			{
				System.out.println("Te has unido a "+clientSentence.substring(0,4));
				getListaNodos(clientSentence.substring(7));
				nodos.add(id);
				updateEveryonesList();
				return true;
			}
		}
		catch (IOException e) 
		{

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	//Recibe un string de nodos y los transforma en ArrayList
	public void getListaNodos(String listaString)
	{
		String[] split = listaString.split(",");
		for(int i=0; i<split.length;i++)
		{
			nodos.add(Integer.parseInt(split[i]));
		}
	}

	public void run()
	{
		try
		{
			String clientSentence;
			while(true) { 
				welcomeSocket.setSoTimeout(0);
				Socket connectionSocket = welcomeSocket.accept();
				BufferedReader inFromClient = 
						new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));				
				clientSentence = inFromClient.readLine();

				try
				{
					if(Integer.parseInt(clientSentence.substring(0,4))!=id)
					{
						//Llegada de un nuevo Nodo, debemos responder su solicitud y enviar la actual lista de nodos
						if(clientSentence.substring(5, 6).compareTo("0") == 0)
						{	
							int nuevoID = Integer.parseInt(clientSentence.substring(0,4));
							sendMessage("1"+"_"+listaNodosToString(), nuevoID); //1 = Llegada Aceptada
							System.out.println("Se ha agregado el nodo "+nuevoID+" a la red");
						}

						//Mensaje normal
						else if(clientSentence.substring(5, 6).compareTo("2") == 0)
						{	
							System.out.println(clientSentence.substring(7));
						}

						//Update de la lista
						else if(clientSentence.substring(5, 6).compareTo("3") == 0)
						{	
							updateList(clientSentence.substring(7));
						}
					}
				}

				catch(IllegalArgumentException e)
				{

				}
			} 
		}
		catch (IOException e) 
		{
			System.out.println("Te has desconectado existosamente");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
