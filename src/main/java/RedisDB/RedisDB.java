package RedisDB;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisDB {
	final private Jedis jedis;
	
	public RedisDB(){
		jedis = new Jedis("localhost");
	}
	
	public void delAllKeys() {
		Set<String> keys = jedis.keys("*");
		for (String key : keys) {
			jedis.del(key);
		} 
	}
	
	public void overWriteSessionId(String userName, String sessionId) {
		System.out.print("123");
	}

	public static void main(String[] args) {
		RedisDB redisDB = new RedisDB();
		
		redisDB.jedis.set("key1", "val1");
		
		redisDB.delAllKeys();

		String val = redisDB.jedis.get("key1");
		System.out.println(val);
		
		System.out.println("end");
	}

}
