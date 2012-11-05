import java.io.*; 
import java.net.*;

public class Nodo implements Runnable
{
	private int id;
	private boolean first;
	private int nextID;
	private int previousID;
	private ServerSocket welcomeSocket;

	public Nodo(int id) throws Exception
	{
		first = false;
		previousID = -1;
		nextID = -1;
		this.id = id;		
		welcomeSocket = new ServerSocket(id);	

		System.out.println("Buscando señal de broadcast");
		for(int i=6000 ; i<=6020 ;i++)
		{				
			System.out.println("Buscando en puerto: "+i);
			mandarSenial(i, "SolicitarLlegada");
			if(checkResponse())
				break;
		}
		//Si no encuentra ningun nodo en los 20 puertos, se pone como primero
		setFirstOn();
		System.out.println("Nodo "+id+" te has conectado exitosamente a la red");

	}
	
	public void escribirMensaje() throws Exception
	{
		while(true)
		{
			try
			{
				String sentence; 

				BufferedReader inFromUser = 
						new BufferedReader(new InputStreamReader(System.in));

				String sentenceConsole = inFromUser.readLine();

				sentence = id +"_"+sentenceConsole + '\n';

				Socket clientSocketPrevious = null;				
				DataOutputStream outToServerPrevious = null;

				Socket clientSocketNext = null;
				DataOutputStream outToServerNext = null;

				if(previousID != -1)
					clientSocketPrevious = new Socket("127.0.0.1", previousID);
				if(nextID != -1)
					clientSocketNext = new Socket("127.0.0.1", nextID);

				if(clientSocketPrevious != null)
					outToServerPrevious = 
					new DataOutputStream(clientSocketPrevious.getOutputStream());

				if(clientSocketNext != null)
					outToServerNext = 
					new DataOutputStream(clientSocketNext.getOutputStream());

				/////Para Desconectarse///////////////////////////////////////////////////////////
				if(sentenceConsole.compareTo("Desconexion")==0)
				{					
					if(clientSocketPrevious != null)
					{
						//Al anterior le mando el next
						outToServerPrevious.writeBytes("NuevoNext_"+nextID);
						clientSocketPrevious.close(); 
					}

					if(clientSocketNext != null)
					{
						//Al next le mando el anterior
						if(first)
							outToServerNext.writeBytes("TuEresElNuevoFirst");
						else
							outToServerNext.writeBytes("NuevoPrevious_"+previousID);

						clientSocketNext.close(); 
					}
					previousID = -1;
					nextID = -1;
					setFirstOff();
					//Liberar Puerto!!!
					welcomeSocket.close();
					break;					
				}
				/////////////////////////////////////////////////////////////////////////////

				if(clientSocketPrevious != null)
				{
					outToServerPrevious.writeBytes(sentence);
					clientSocketPrevious.close(); 
				}

				if(clientSocketNext != null)
				{
					outToServerNext.writeBytes(sentence);
					clientSocketNext.close();
				}					

				System.out.println("Me: " + sentenceConsole + '\n');					
			}
			catch(ConnectException e)
			{
				System.err.println("Error de Conexion 2");
			}
		}
	}
	public void mandarSenial(int idDestinatario, String senial) throws Exception
	{
		try
		{
			Socket clientSocket = new Socket("127.0.0.1", idDestinatario);

			DataOutputStream outToServer = 
					new DataOutputStream(clientSocket.getOutputStream());

			outToServer.writeBytes(id+"_"+senial);		

			clientSocket.close(); 
		}
		catch(ConnectException e)
		{

		}
	}
	public void mandarASiguiente(int idDestinatario, String mensaje) throws Exception
	{
		try
		{
			Socket clientSocket = new Socket("127.0.0.1", idDestinatario);

			DataOutputStream outToServer = 
					new DataOutputStream(clientSocket.getOutputStream());

			outToServer.writeBytes(mensaje);		

			clientSocket.close(); 
		}
		catch(ConnectException e)
		{

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
					////Legada de un nuevo Nodo
					if(clientSentence.substring(5).compareTo("SolicitarLlegada") == 0)
					{			
						if(first)
						{
							first = false;
							int nuevoID = Integer.parseInt(clientSentence.substring(0,4));
							previousID = nuevoID;
							mandarSenial(nuevoID, "LlegadaAceptada");
							System.out.println("Se ha agregado el nodo "+nuevoID+" a la red");
						}
					}

					////Reconstruir Red al haber desconexion
					else if(clientSentence.length()>=9 && clientSentence.substring(0, 9).compareTo("NuevoNext") == 0)
					{
						int nuevoNextID = Integer.parseInt(clientSentence.substring(10));
						nextID = nuevoNextID;
					}
					else if(clientSentence.length()>=13 && clientSentence.substring(0, 13).compareTo("NuevoPrevious") == 0)
					{
						int nuevoPreviousID = Integer.parseInt(clientSentence.substring(14));
						previousID = nuevoPreviousID;				}

					else if(clientSentence.compareTo("TuEresElNuevoFirst") == 0)
					{
						setFirstOn();
						previousID = -1;
					}

					//Si el id de destinatario corresponde al mio, imprimir.
					else if(clientSentence.length()>=9 && Integer.parseInt(clientSentence.substring(5,9))==id)
					{
						System.out.println("From "+clientSentence.substring(0,4)+": "+clientSentence.substring(10));
					}
					//Si no es para mi, enviar al siguiente en la fila, dependiendo de donde vino el mensaje
					else if(Integer.parseInt(clientSentence.substring(0,4))==nextID)
					{
						mandarASiguiente(previousID, clientSentence);
					}
					else if(Integer.parseInt(clientSentence.substring(0,4))==previousID)
					{
						mandarASiguiente(nextID, clientSentence);
					}
					else
					{
						System.out.println("Potocolo de mensaje erróneo, por favor pruebe denuevo");
					}
				}
				catch(IllegalArgumentException e)
				{
					if(Integer.parseInt(clientSentence.substring(0,4))==previousID)
					{
						mandarSenial(previousID, previousID+"_Protocolo de mensaje erróneo, por favor pruebe denuevo");
					}
					else if(Integer.parseInt(clientSentence.substring(0,4))==nextID)
					{
						mandarSenial(nextID, nextID+"_Protocolo de mensaje erróneo, por favor pruebe denuevo");
					}
				}
			} 
		}
		catch (IOException e) 
		{
			System.out.println("Te has desconectado existosamente");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

			if(clientSentence.compareTo("") != 0 && clientSentence.substring(5).compareTo("LlegadaAceptada") == 0)
			{
				first = true;
				int exFirstID = Integer.parseInt(clientSentence.substring(0,4));
				nextID = exFirstID;	
				return true;
			}
			System.out.println(clientSentence);

		}
		catch (IOException e) 
		{
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public long getId()
	{
		return id;
	}
	public boolean isFirst()
	{
		return first;
	}
	public int getNext()
	{
		return nextID;
	}
	public void setNextID(int n)
	{
		nextID = n;
	}
	public void setFirstOn()
	{
		first = true;
	}
	public void setFirstOff()
	{
		first = false;
	}
}