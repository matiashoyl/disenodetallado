package communication;
//import usermanager.*;

import java.io.Serializable;

public class UMMessage implements Serializable{
	public int sender_id;
	public String action;
	public Object pack;

	public UMMessage(UserManager um, String act, Object obj)
	{
		this.sender_id = um.getCurrentUser().getId();
		this.action = act;
		this.pack = obj;
	}

}