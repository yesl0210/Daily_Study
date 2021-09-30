
public class Fibo_TD {
	// Top Down : 재귀함수 이용
	// 시간복잡도 O(N) : 한 번 구한 결과는 다시 구해지지 않으며, 이후 계산에 사용되기 때문.

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
