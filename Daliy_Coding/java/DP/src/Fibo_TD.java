
public class Fibo_TD {
	// Top Down : ����Լ� �̿�
	// �ð����⵵ O(N) : �� �� ���� ����� �ٽ� �������� ������, ���� ��꿡 ���Ǳ� ����.

	public static long[] memory = new long[100];
	
	public static long fibo(int x) {
		if (x == 1 || x == 2) return 1;
		if (memory[x] != 0) return memory[x];
		memory[x] = fibo(x-1) + fibo(x-2);
		return memory[x];
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		System.out.println("fibo(50) : "+fibo(50));		

	}

}
