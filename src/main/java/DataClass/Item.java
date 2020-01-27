package DataClass;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Item implements Serializable {
	private String id;
	private String name;
	private String image;
	
	public Item(String id, String name, String image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}
	public String toString() {
		return (id + ";" + name + ";" + image);
	}
	
	public static void main(String[] args) {

		ObjectOutputStream objectWriter = null;
		ObjectInputStream objectReader = null;
		try {
			objectWriter = new ObjectOutputStream(new FileOutputStream("student.dat"));
			objectWriter.writeObject(new Item("id 1", "John", "Mayor"));
			objectWriter.writeObject(new Item("id 2", "Sam", "Abel"));
			objectWriter.writeObject(new Item("id 3", "Anita", "Motwani"));
 
			
			objectReader = new ObjectInputStream(new FileInputStream("student.dat"));
			for (int i = 0; i < 3; i++) {
				System.out.println(objectReader.readObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				objectWriter.close();
				objectReader.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
 
	}

}
