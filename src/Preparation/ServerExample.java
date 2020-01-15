package Preparation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerExample {

	public static void main(String[] args) {
		//마지막에 close 하기 위해서  상단에 생성
		ServerSocket serverSocket = null;
		try {
			//서버 소켓 객체 생성
			serverSocket = new ServerSocket();
			//서버 소켓 포트 지정
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
			
			while(true) {
				System.out.println("[연결 기다림]");
				
				//클라이언트 연결 수락
				Socket socket = serverSocket.accept();
				//클라이언트 IP 정보 얻기 위해서 InetSocketAddress 객체 생성
				InetSocketAddress isa = (InetSocketAddress)socket.getRemoteSocketAddress();
				System.out.println("[연결 수락함] " + isa.getHostName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//ServerSocket 가 닫혀있지 않을 경우
		if(!serverSocket.isClosed()) {
			try {
				//serverSocket 닫기
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
