package de.fhws.applab.restdemo;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.android.gcm.server.*;
import com.owlike.genson.Genson;

@Path("/sendMessage")
public class GCMServer {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( String text)
	{
		Genson genson = new Genson();
		UserDetail user = genson.deserialize(text, UserDetail.class);
		final String regid = user.getRegid();
		new Thread(){
			public void run(){
				try {
					Sender sender = new Sender(Config.GOOGLE_API_KEY);
					Message message = new Message.Builder()
					.collapseKey("message")
					.timeToLive(3)
					.delayWhileIdle(true)
					.addData("message", "Welcome to your Push Notifications")
					.build();
					
					Result result = sender.send(message, regid, 1);
					System.out.println(result.toString());
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
		return Response.created(null).build();
	}


}
