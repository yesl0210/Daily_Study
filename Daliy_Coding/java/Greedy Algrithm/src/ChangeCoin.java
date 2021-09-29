import java.io.*;
public class ChangeCoin {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int money = Integer.parseInt(br.readLine());
		int ans = 0; // 동전의 최소 개수
		int[] coinTypes = {500, 100, 50, 10};
		
		for(int coin : coinTypes) { // 화폐의 종류만큼 반복 -> O(N = 화폐의 종류)
			ans += money / coin;
			money %= coin;
		}
		System.out.println("Money : "+money+", Ans : "+ans);
				
				
		

	}

}
