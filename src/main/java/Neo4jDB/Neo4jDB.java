package Neo4jDB;

import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.LogManager;

import com.google.common.collect.ArrayListMultimap;

public class Neo4jDB {
	final private Driver driver;
	final private Session session;

	public Neo4jDB(String uri, String user, String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		session = driver.session();
	}

	public void addlabel(String type) {
		session.run("MERGE (l:Label {type:$t})", parameters("t", type));
	}

	public void addUser(String name) {
		if (!userExist(name)) {
			session.run("CREATE (u:User {name:$n})", parameters("n", name));

			session.run("MATCH (s:Label {type:'Sports'}) " + "MATCH (o:Label {type:'Digital Product'}) "
					+ "MATCH (d:Label {type:'Office Product'}) " + "MATCH (u:User {name:$n})"
					+ "CREATE (u)-[:Rate{value:0}]->(s) " + "CREATE (u)-[:Rate{value:0}]->(o) "
					+ "CREATE (u)-[:Rate{value:0}]->(d)", parameters("n", name));
		}
	}

	public void increaseRateOne(String name, String type) {
		Result result = session.run(
				"MATCH (u:User {name:$n})" + "MATCH (u)-[r:Rate]->(s:Label{type:$t})" + "RETURN r.value",
				parameters("n", name, "t", type));

		if (result.hasNext()) {
			Record record = result.next();
			int newRate = record.get("r.value").asInt() + 1;

			session.run("MATCH (u:User {name:$n}) " + "MATCH (u)-[r:Rate]->(l:Label{type:$t})" + "SET r.value = $v",
					parameters("n", name, "t", type, "v", newRate));
		}
	}

	public boolean userExist(String name) {
		Result result = session.run("MATCH (u:User {name:$n}) return u", parameters("n", name));
		if (result.hasNext()) {
			return true;
		} else {
			return false;
		}
	}

	public String getPreferenceByRate(String name) {
		Result result = session.run("MATCH (u:User {name:$n})" + "MATCH (u)-[sr:Rate]->(s:Label{type:'Sports'})"
				+ "MATCH (u)-[or:Rate]->(o:Label{type:'Office Product'})"
				+ "MATCH (u)-[dr:Rate]->(d:Label{type:'Digital Product'})" + "RETURN sr.value, or.value, dr.value",
				parameters("n", name));

		if (result.hasNext()) {
			Record record = result.next();
			int sr = record.get("sr.value").asInt();
			int or = record.get("or.value").asInt();
			int dr = record.get("dr.value").asInt();

			if (sr >= or) {
				if (sr >= dr) {
					return "Sports";
				} else {
					return "Digital Product";
				}
			} else {
				if (or >= dr) {
					return "Office Product";
				} else {
					return "Digital Product";
				}
			}
		}
		return "";
	}

	private static int sum(ArrayList<Integer> list) {
		int sum = 0;
		for (int j = 0; j < list.size(); j++) {
			int value = list.get(j);
			sum += value;
		}
		return sum;
	}

	// add
	public void addRefPrice(String user, String item, int price) {
		Result result = session.run("MATCH(i:item{name:$item}) MERGE (:user{name:$user})-[:REFPRICE{price:"+price+"}]->(i) ",
				parameters("user", user, "item", item));

	}

		// add
		public void addComment(String user, String item, String comment) {
			Result result = session.run("MATCH(i:item{name:$item}) MERGE (:user{name:$user})-[:COMMENT{comment:$comment}]->(i) ",
					parameters("user", user, "comment",comment,"item", item));
	
		}

	public static int getAverageRefPrice(ArrayList<Integer> price) {
		ArrayList<Integer> newList = new ArrayList<Integer>();
		int Threshold = 0;
		int result = 0;
		Threshold = sum(price) / price.size();
		for (int a = 0; a < price.size(); a++) {
			if ((price.get(a) > 0.5 * Threshold) && (price.get(a) < 1.5 * Threshold))
			newList.add(price.get(a));
		}
		result = sum(newList) / newList.size();
		System.out.println(result);
		return result;
	}

	// get price from relations
	public ArrayList<Integer> getRefPrice(String item) {
		Result result = session.run("MATCH ()-[r:REFPRICE]->(:item{name:$item})" + "return r.price",
				parameters("item", item));
		ArrayList<Integer> price = new ArrayList<Integer>();
		while (result.hasNext()) {
			price.add(result.next().get("r.price").asInt());
		}
		return price;
	}

	public void deleteAll() {
		session.writeTransaction(tx -> tx.run("MATCH (n) DETACH DELETE n"));
		System.out.println("CLEAR ALL!");
	}

	// Add item belongs to label
	public void addItem(String item, String label) {
		try {
			session.writeTransaction(
					tx -> tx.run("Match(b:Label {type:" + " $label})" + "MERGE (a:item { name: $item  })"
							+ "MERGE(a)-[:BELONGS_TO]->(b);", parameters("item", item, "label", label)

					));
		} catch (Exception e) {
			System.out.println("Fail to add relationship! plz check query!");
		}

	}

	// Add relationships
	public void addRs(String nodeOne, String keyOne, String mKeyOne, String nodeTwo, String keyTwo, String mKeyTwo,
			String relation) {
		try {
			session.writeTransaction(tx -> tx.run(
					"MATCH (a:" + nodeOne + "{ " + keyOne + ": $mKeyOne  })Match(b:" + nodeTwo + "{" + keyTwo
							+ ": $mKeyTwo}) MERGE(a)-[:" + relation + "]->(b);",
					parameters("mKeyOne", mKeyOne, "mKeyTwo", mKeyTwo)

			));
		} catch (Exception e) {
			System.out.println("Fail to add relationship! plz check query!");
		}
	}

	// delete relationships
	public void delRS(String nodeOne, String keyOne, String mKeyOne, String nodeTwo, String keyTwo, String mKeyTwo,
			String relation) {
		String anode, bnode;
		if (nodeOne != "")
			anode = "a:" + nodeOne + "{ " + keyOne + ": $mKeyOne  }";
		else
			anode = "";
		if (nodeTwo != "")
			bnode = "b:" + nodeTwo + "{" + keyTwo + ": $mKeyTwo}";
		else
			bnode = "";
		session.writeTransaction(tx -> tx.run("MATCH (" + anode + ")-[r:" + relation + "]->(" + bnode + ") delete r;",
				parameters("mKeyOne", mKeyOne, "mKeyTwo", mKeyTwo)));
		System.out.println("CLEAR ALL!");
	}

	public static void main(String... args) {
		LogManager.getLogManager().reset();
		Neo4jDB neo4jDB = new Neo4jDB("bolt://47.91.94.172:7687", "neo4j", "neo4j");
		// neo4jDB.deleteAll();

		
		neo4jDB.addlabel("Sports");
		neo4jDB.addlabel("Office Product");
		neo4jDB.addlabel("Digital Product");
		neo4jDB.addItem("Bike", "Sports");
		neo4jDB.addItem("Car", "Sports");
		neo4jDB.addItem("Ship", "Sports");

		//add label
		neo4jDB.addItem("CPU", "Digital Product");
		neo4jDB.addItem("RAM", "Digital Product");
		neo4jDB.addItem("Pencil", "Office Product");
		neo4jDB.addItem("Ruler", "Office Product");
		neo4jDB.addItem("Eraser", "Office Product");

		//add price relationships
		neo4jDB.addRefPrice("Tom", "Ship", 50000);
		neo4jDB.addRefPrice("Bill", "Ship", 50020);
		neo4jDB.addRefPrice("Lily", "Ship", 50100);
		neo4jDB.addRefPrice("Smith", "Ship", 49000);
		neo4jDB.addRefPrice("Chen", "Ship", 500);
		neo4jDB.addRefPrice("Jerry", "Ship", 50200);
		neo4jDB.addRefPrice("Ben", "Ship", 150000);
		neo4jDB.addRefPrice("Monica", "Ship", 50000);


		//get average price from neo4j relationships
		getAverageRefPrice(neo4jDB.getRefPrice("Ship"));

		//add comment to relationships
		neo4jDB.addComment("Tom", "Ship", "it is really good! I like it very much!");



		// neo4jDB.addUser("root");
		// neo4jDB.addUser("admin");
		// neo4jDB.addUser("user");
		//
		// neo4jDB.increaseRateOne("user", "Sports");
		// neo4jDB.increaseRateOne("user", "Office Product");
		// neo4jDB.increaseRateOne("user", "Digital Product");
		//
		//
		// String str = neo4jDB.getPreferenceByRate("user");
		// System.out.println(str);

		neo4jDB.session.close();
		neo4jDB.driver.close();

		System.out.println("End");
	}
}
