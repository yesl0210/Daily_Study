import java.util.*;
import java.io.*;

public class Basic {

	public static void main(String[] args) throws Exception {
		ArrayList<ArrayList<Integer>> problem = new ArrayList<ArrayList<Integer>>();
		// TODO Auto-generated method stub
//		4
//		12 0
//		10 14
//		4 20
//		5 2147483648

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int size = Integer.parseInt(br.readLine());
	
		String[] tmp;	
		
		for(int i=0;i<4;i++) {
			problem.add(new ArrayList<Integer>());
			tmp = br.readLine().split(" ");
			
			for(String j : tmp) {
//				problem.get(i).add(Float.parseFloat(j));
				problem.get(i).add(Integer.parseInt(j));
			}
		}
		
		System.out.println(problem);
		
	}

}
