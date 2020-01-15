package Preparation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientExample {

	public static void main(String[] args) {
		//마지막에 close 하기 위해서 상단에 생성
		Socket socket = null;
		try {
			//Socket 생성
			socket = new Socket();
			System.out.println("[연결 요청]");
			
			//서버쪽에 연결 요청
			socket.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[연결 성공]");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//소켓이 닫혀 있지 않은 경우 연결 끊기
			if(!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

	}

}
