import java.util.*;

public class Map_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String,Integer> map1 = new HashMap<>();
		map1.put("apple",1);
		map1.put("banana",2);
		System.out.println("map1 : "+map1);
		System.out.println("get from map1 : "+map1.get("apple"));
		System.out.println("Contains banana in map1 ? : " +
		map1.containsKey("banana"));
		
		// Map °ª Á¶È¸
		System.out.println("keySet : "+map1.keySet());
		Iterator<String> iter = map1.keySet().iterator();
		
		while(iter.hasNext()) {
			String key = iter.next();
			int value = map1.get(key);
			System.out.println(value);
		}


	}

}
