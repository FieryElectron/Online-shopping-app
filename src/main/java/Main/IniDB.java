package Main;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.logging.LogManager;

import javax.imageio.ImageIO;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import java.util.Base64;
import java.util.logging.LogManager;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.types.Binary;

public class IniDB {
	final private MongoClient mongoClient;
	
	public IniDB() {
		mongoClient = new MongoClient(new MongoClientURI("mongodb://www.chenyiyang.com.cn:27017"));
	}
	

	
	public static String ReadFileToBase64String(String path) throws IOException {
		BufferedImage bImage = ImageIO.read(new File(path));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(bImage, "png", bos );
		byte [] data = bos.toByteArray();

		String base64String = Base64.getEncoder().encodeToString(data);
		return base64String;
	}
	
	public void InsertItem(String name, String label, String price, String imagePath) throws IOException {
		
		String base64String = ReadFileToBase64String(imagePath);
		
		MongoDatabase mongoDatabase = mongoClient.getDatabase("JavaDB");
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("ItemCL");
     
        Document document = new Document("image", base64String)
        		.append("name", name)
        		.append("label", label)
        		.append("price", price);
        mongoCollection.insertOne(document);
	}

	public static void main(String[] args) throws IOException {
		LogManager.getLogManager().reset();
		
		IniDB IDB = new IniDB();
		final String basePath = "E:/SpringToolSuite-WorkSpace/WebSocket/src/main/resources/img/";
		
		IDB.InsertItem("Bike","Sports","123",basePath+"bike.png");
		IDB.InsertItem("Car","Sports","456",basePath+"car.png");
		IDB.InsertItem("Ship","Sports","789",basePath+"ship.png");
		
		IDB.InsertItem("CPU","Digital Product","147",basePath+"cpu.png");
		IDB.InsertItem("PC","Digital Product","258",basePath+"pc.png");
		IDB.InsertItem("RAM","Digital Product","369",basePath+"ram.png");
		
		IDB.InsertItem("Pencil","Office Product","321",basePath+"pencil.png");
		IDB.InsertItem("Ruler","Office Product","654",basePath+"ruler.png");
		IDB.InsertItem("Eraser","Office Product","987",basePath+"eraser.png");
		

		

		

    }

		
		

	

}
