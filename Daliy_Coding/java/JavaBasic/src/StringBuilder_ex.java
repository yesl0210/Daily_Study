
public class StringBuilder_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		
		// 추가
		System.out.println("\n* Add *");
		System.out.println("sb : "+sb);
		sb.append('a');
		sb.append("ppl");
		sb.append('e');
		System.out.println("sb : "+sb);
		
		// 제거
		System.out.println("\n* Delete *");
		StringBuilder sb2 = new StringBuilder("fabacd");
		System.out.println("sb2 : "+sb2);
		sb2.deleteCharAt(0);
		System.out.println("sb2 : "+sb2);
		sb2.deleteCharAt(sb2.indexOf("a",sb2.indexOf("a")+1));
		System.out.println("sb2 : "+sb2);
		
		// 값 변경
		System.out.println("\n* Change *");
		sb.setCharAt(3, 'P');
		System.out.println("sb : "+sb);
		

	}

}
