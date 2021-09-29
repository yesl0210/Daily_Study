
public class Recursive_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		recursive(1);
		System.out.println("5! : "+factorial(5));
	}
	
	public static void recursive(int i) {
		if(i==100) return;
		
		// 호출된 재귀함수가 종료될 때까지 호출한 함수는 더이상 진행되지 않고 호출한 코드에서 멈춰있음.
        System.out.println(i + "번째 재귀 함수에서 " + (i + 1) + "번째 재귀함수를 호출합니다.");
        recursive(i + 1);
        
        // 호출된 재귀함수가 종료되면, 종료된 재귀함수를 호출한 함수가 다시 실행됨.
        System.out.println(i + "번째 재귀 함수를 종료합니다.");
	}
	
	public static int factorial(int n) {
		if(n == 1) return 1;
		return n * factorial(n-1);
		
	}

}
