import java.io.*;

public class UDLR {

	public static void main(String[] args) throws Exception {
		// 일련의 명령에 따라 개체를 이동시킴 --> '시뮬레이션' 유형
		// O(N = 명령어 개수) : 연산 횟수는 명령어 개수에 비례함.
		
		// R R R U D D
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int size = Integer.parseInt(br.readLine());
		String[] command = br.readLine().split(" ");
		int x = 1, y = 1;
		System.out.println("명령어 : ");
		for(String com : command) {
			System.out.print(com);
		}
		System.out.println();
		
		// L, R, U, D에 따른 이동 방향
		int[] dx = {0,0,-1,1};
		int[] dy = {-1,1,0,0};
		char[] moveTypes = {'L', 'R', 'U', 'D'};
		
		for(String com : command) {
			int nx = x, ny = y;
			System.out.println("명령어 : "+com+", 현재 위치 - x : "+x+", y : "+y);
			for(int j=0;j<moveTypes.length;j++) {
				if(com.charAt(0) == moveTypes[j]) {
					nx += dx[j];
					ny += dy[j];
				}
			}
			System.out.print("이동할 위치 - nx : "+nx+", ny : "+ny);
			if(nx<1 || nx > size || ny<1 || ny>size) {
				System.out.println("\t이동 실패\n");
				continue;
			}
			x = nx;
			y = ny;
			System.out.println("\t이동 성공\n");
		}
		
		System.out.println("도착 지점 : "+x+","+y);
	}

}
