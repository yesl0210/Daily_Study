import java.io.*;

public class TimeEx {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int n = Integer.parseInt(br.readLine());
		int cnt = 0;
		
		for(int h=0;h<=n;h++) { // hour
			for(int m=0;m<60;m++) { // minute
				for(int s=0;s<60;s++) { // second
					
					if(String.valueOf(h).contains("3")||
							String.valueOf(m).contains("3") ||
							String.valueOf(s).contains("3")) cnt++;
					
				}
			}
		}
		System.out.println("cnt : "+cnt);
	}

}
