
public class String_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str1 = "ABC";
		System.out.println(str1.toLowerCase());
		System.out.println(str1.toUpperCase());
		
		// String -> Array
		String[] arr = str1.split("");
		System.out.println("arr : "+arr);
		
		// Reverse
		String str2 = "abcde";
		String str3 = new StringBuilder(str2).reverse().toString();
		System.out.println("str3 : "+str3);
		
		// String -> 정수 or Integer
		String num1 = "123";
		System.out.println("num1 : "+num1+"("+num1.getClass().getName()+")");
		Integer num2 = Integer.parseInt(num1);
		System.out.println("num2 : "+num2+"("+num2.getClass().getName()+")");
		num2 = 500;
		num1 = Integer.toString(num2);
		System.out.println("num1 : "+num1+"("+num1.getClass().getName()+")");
		System.out.println("num2 : "+String.valueOf(num2)+
				"("+String.valueOf(num2).getClass().getName()+")");
		
		// 길이
		System.out.println("str3 length : "+ str3.length());
		
		
	}

}
