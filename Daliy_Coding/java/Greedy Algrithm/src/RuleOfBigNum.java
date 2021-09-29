import java.io.*;
import java.util.*;

public class RuleOfBigNum {

	public static void main(String[] args) throws Exception {
		// Input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String[] input = br.readLine().split(" ");
		
		int n = Integer.parseInt(input[0]); // number 개수
		int m = Integer.parseInt(input[0]); // 더하는 횟수
		int k = Integer.parseInt(input[0]); // 초과 불가능 횟수
		int cnt = 0; // 연속된 수 개수
		int pre = 0; // 이전 수
		
		ArrayList<Integer> num = new ArrayList<Integer>();
		String[] input2 = br.readLine().split(" ");
		
		for(String str : input2) {
			num.add(Integer.parseInt(str));
		}
		
		for(int i=0; i<m;i++) {
			int max = Collections.max(num);
			int index = num.indexOf(max);
			
			if(pre != max) {
				cnt = 0;
			}
			else {
				if(cnt >= k) {
					i--;
				}
			}
		}
		
		
	}

}
