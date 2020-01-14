package Preparation;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressExample {

	public static void main(String[] args) {
		try {
			//자바는 IP주소를 InetAddress 객체로 표현한다
			//로컬 InetAddress 얻기
			InetAddress local = InetAddress.getLocalHost();
			//로컬 IP 주소 얻기
			System.out.println("내 컴퓨터 IP주소 = " + local.getHostAddress());
			
			//네이버 InetAddress 얻기
			InetAddress[] iaArr = InetAddress.getAllByName("www.naver.com");
			//네이버 IP 주소 얻기
			for(InetAddress remote : iaArr) {
				System.out.println("naver.com의 IP주소 = " + remote.getHostAddress());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
