package WebSocket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import MongoDB.MongoDB;
import Neo4jDB.Neo4jDB;
import RedisDB.RedisDB;
import redis.clients.jedis.Jedis;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.*;

import MongoDB.MongoDB;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.logging.LogManager;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.types.Binary;

public class WebsocketServer extends WebSocketServer {
	private final MongoDB mongoDB = new MongoDB("mongodb://www.chenyiyang.com.cn:27017");
	private final Neo4jDB neo4jDB = new Neo4jDB("bolt://www.chenyiyang.com.cn:7687", "neo4j", "jmp");
	private final RedisDB redisDB = new RedisDB("www.chenyiyang.com.cn");
	
	static public List<WebSocket> clients;
	
	public WebsocketServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		System.out.println( conn + " connected!" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		System.out.println( conn + " disconnected!" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		JSONObject obj = new JSONObject(message);
		processMessage(conn, obj.get("para0").toString(), obj.get("para1").toString(), obj.get("para2").toString(), obj.get("para3").toString(), obj.get("para4").toString());
	}
	
	public void processMessage(WebSocket conn, String para0, String para1, String para2, String para3, String para4) {
		
//		System.out.println(para0+"|"+para1+"|"+para2+"|"+para3+"|"+para4);
		switch(para0) {
		case "signup":
			if(para1.length() == 0 || para2.length() == 0) {
				sendMessage(conn, "alert","Invalid username or password!","","");
			}else if(mongoDB.hasUser(para1)) {
				sendMessage(conn, "alert","User exist!","","");
			}else {
				mongoDB.insertUser(para1, para2);
				neo4jDB.addUser(para1);
				sendMessage(conn, "alert","Sign up succeed!","","");
			}
			break;
		case "signin":
			if(para1.length() == 0 || para2.length() == 0) {
				sendMessage(conn, "alert","Invalid username or password!","","");
			}else if(mongoDB.matchPassword(para1, para2)) {
				sendMessage(conn, "alert","User "+para1+" signed in!","","");
				sendMessage(conn, "signin","","","");
				
				sendMessage(conn, "userinfo",para1,"","");
				
				ArrayList<String> allitem = mongoDB.getAllAvailableItems();

				for(int i =0;i<allitem.size();++i) {
					String pair = allitem.get(i);
					sendMessage(conn, "additem","item_container",pair,"");
				}
				
				sendMessage(conn, "cleantransaction","","","");
				String str = mongoDB.getAllTransactions(para1);

				sendMessage(conn, "alltransactions",str,"","");
				
			}else {
				sendMessage(conn, "alert","Sign in failed!","","");
			}
			
			break;
		case "getiteminfo":
			String info = mongoDB.getItemInfo(para1);
			
			int refPrice = Neo4jDB.getAverageRefPrice(neo4jDB.getRefPrice(mongoDB.getItemNameById(para1)));
			info = info +";"+refPrice;

			sendMessage(conn, "getiteminfo",info,"","");
			
			String label = mongoDB.getItemLabel(para1);
			neo4jDB.increaseRateOne(para2, label);
			
			String preference = neo4jDB.getPreferenceByRate(para2);
			
			String suggestedItemId = mongoDB.getRandomItemIdByLabel(preference);
			String suggestinfo = mongoDB.getItemInfo(suggestedItemId);
			
			refPrice = Neo4jDB.getAverageRefPrice(neo4jDB.getRefPrice(mongoDB.getItemNameById(suggestedItemId)));
			suggestinfo = suggestinfo +";"+refPrice;
			
			sendMessage(conn, "getsuggesttiteminfo",suggestedItemId+";"+suggestinfo,"","");
			
			
			break;
		case "buyitem":
			mongoDB.buyItem(para1,para2,para3);

			neo4jDB.addRefPrice(para1,mongoDB.getItemNameById(para2), Integer.parseInt(para4));
			
			
			sendMessage(conn, "cleantransaction","","","");
			String str = mongoDB.getAllTransactions(para1);

			sendMessage(conn, "alltransactions",str,"","");

			break;
		}
	}
	
	public void sendMessage(WebSocket conn, String para0, String para1, String para2, String para3) {
		JSONObject obj = new JSONObject();
		obj.put("para0",para0);
		obj.put("para1",para1);
		obj.put("para2",para2);
		obj.put("para3",para3);
		
		conn.send(obj.toString());
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {	
	}

}