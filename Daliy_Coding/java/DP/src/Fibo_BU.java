
public class Fibo_BU {
	// Bottom Up : 반복문 이용
	public static long[] memory = new long[100];

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		memory[1] = 1;
		memory[2] = 1;
		
		int size = 50;
		for (int i=3; i<=size ; i++) {
			memory[i] = memory[i-1] + memory[i-2];
		}
		System.out.println("fibo(50) : "+memory[50]);

	}

}
