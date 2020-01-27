package Main;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.LogManager;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.json.JSONObject;

public class mongoDBImage {
//	private final static MongoDB mgdb = new MongoDB("mongodb://localhost:27017");

	public static void main(String[] args) throws Exception  {
		LogManager.getLogManager().reset();

		

//	BufferedImage bImage = ImageIO.read(new File("E:\\SpringToolSuite-WorkSpace\\WebSocket\\src\\main\\resources\\bike.jpg"));
//	ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	ImageIO.write(bImage, "jpg", bos );
//	byte [] data = bos.toByteArray();
//
//	String base64String = Base64.getEncoder().encodeToString(data);
//
//
//
//	      
//
//		
//		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
//		
//		MongoDatabase mongoDatabase = mongoClient.getDatabase("BinaryDB");
//		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("IMG");
//     
//        Document document = new Document("base64String", base64String);
//        mongoCollection.insertOne(document);
		
//		Document doc = mongoCollection.find().first();
//		
//		Binary bin = doc.get("data", org.bson.types.Binary.class);
//		byte[] data = bin.getData();
//		
//		ByteArrayInputStream bis = new ByteArrayInputStream(data);
//		BufferedImage bImage2 = ImageIO.read(bis);
//		ImageIO.write(bImage2, "jpg", new File("E:\\SpringToolSuite-WorkSpace\\WebSocket\\src\\main\\resources\\2.jpg") );
//		System.out.println("image created");

//		String res = mgdb.getItemInfo("5e2ca865d2aa9e35134ddce5");
//		System.out.println(res);
//		
//		byte[] data = Base64.getDecoder().decode(res);
//		
//		ByteArrayInputStream bis = new ByteArrayInputStream(data);
//		BufferedImage bImage2 = ImageIO.read(bis);
//		ImageIO.write(bImage2, "jpg", new File("E:\\SpringToolSuite-WorkSpace\\WebSocket\\src\\main\\resources\\66666.jpg") );
//		System.out.println("image created");


		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		
		MongoDatabase mongoDatabase = mongoClient.getDatabase("testUser");
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("UserCL");
		
		Document bike = new Document("id", "123465").append("name", "bike").append("num", "100");
		Document car = new Document("id", "654321").append("name", "car").append("num", "900");

//		Document doc = new Document("name", "root")
//        .append("password", "root");
////        .append("items", Arrays.asList(bike,car));
//        mongoCollection.insertOne(doc);
		
//		BasicDBObject findFilter = new BasicDBObject();
//		findFilter.put("name", "root");
//		
//		List<Document> arr = (List<Document>) mongoCollection.find(findFilter).first().get("items");
//		
//		for (Document element : arr) {
//			System.out.println(element.get("id")+"|"+element.get("name")+"|"+element.get("num"));
//		}
		
		BasicDBObject updateQuery = new BasicDBObject("name", "root");
		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("transactions", bike));
		mongoCollection.updateOne(updateQuery, updateCommand);

		


		

	

		
    }


}