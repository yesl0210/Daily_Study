
public class Recursive_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		recursive(1);
		System.out.println("5! : "+factorial(5));
	}
	
	public static void recursive(int i) {
		if(i==100) return;
		
		// ȣ��� ����Լ��� ����� ������ ȣ���� �Լ��� ���̻� ������� �ʰ� ȣ���� �ڵ忡�� ��������.
        System.out.println(i + "��° ��� �Լ����� " + (i + 1) + "��° ����Լ��� ȣ���մϴ�.");
        recursive(i + 1);
        
        // ȣ��� ����Լ��� ����Ǹ�, ����� ����Լ��� ȣ���� �Լ��� �ٽ� �����.
        System.out.println(i + "��° ��� �Լ��� �����մϴ�.");
	}
	
	public static int factorial(int n) {
		if(n == 1) return 1;
		return n * factorial(n-1);
		
	}

}
