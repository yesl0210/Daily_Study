import java.io.*;
public class ChangeCoin {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int money = Integer.parseInt(br.readLine());
		int ans = 0; // ������ �ּ� ����
		int[] coinTypes = {500, 100, 50, 10};
		
		for(int coin : coinTypes) { // ȭ���� ������ŭ �ݺ� -> O(N = ȭ���� ����)
			ans += money / coin;
			money %= coin;
		}
		System.out.println("Money : "+money+", Ans : "+ans);
				
				
		

	}

}
