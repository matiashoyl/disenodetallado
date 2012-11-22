
public class Mensaje implements java.io.Serializable{

	private String contenido;
	
	public Mensaje(String msg)
	{
		contenido = msg;
	}
	
	public String getContendido()
	{
		return contenido;
	}
	
}
