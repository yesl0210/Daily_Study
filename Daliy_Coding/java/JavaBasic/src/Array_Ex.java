import java.util.Arrays;

public class Array_Ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Array ¼±¾ð
		System.out.println("* Initialize *");
		String[] str1 = {"abcdefg"};
		System.out.print("str1 : ");
		print(str1);
		
		String[] str2 = new String[10];
		for (int i=0;i<str2.length;i++) {
			str2[i] = "a";
		}
		System.out.print("str2 : ");
		print(str2);
		
		
		

	}
	
	public static void print(String[] str) {
		for (String i : str) {
			System.out.print(i+" ");
		}
		System.out.println("");
	}

}
