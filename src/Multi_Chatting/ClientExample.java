package Multi_Chatting;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientExample extends Application{
	
	Socket socket;
	
	void startClient() {
		//스레드 생성
		//connect()와 receive()에서 블로킹이 일어나기 때문에 스레드 필요
		Thread thread = new Thread() {
			
			@Override
			public void run() {
				try {
					//소켓 생성 및 연결 요청
					socket = new Socket();
					//localhost 5001 포트로 연결 요청
					socket.connect(new InetSocketAddress("localhost", 5001));
					
					//작업 스레드는 UI를 변경하지 못하므로 Platform.runLater()가 사용
					Platform.runLater(()->{
						displayText("[연결 완료: " + socket.getRemoteSocketAddress() + "]");
						//start 버튼의 글자를 stop으로 변경
						btnConn.setText("stop");
						//send 버튼 활성화
						btnSend.setDisable(false);
					});
				} catch (Exception e) {
					//예외가 발생하면 서버 통신 안됨 출력
					Platform.runLater(()->displayText("[서버 통신 안됨]"));
					//socket가 닫혀있지 않으면 stopClient() 호출
					if(!socket.isClosed()) {
						stopClient();
					}
					return;
				}
				//예외가 발생하지 않으면 receive() 메소드를 호출
				receive();
			}
		};
		//작업 스레드 시작
		thread.start();
	}
	
	void stopClient() {
		
		try {
			//UI를 변경하기 위해서 Platform.runLater()가 사용
			Platform.runLater(()->{
				displayText("[연결 끊음]");
				//stop버튼의 글자를 start로 변경
				btnConn.setText("start");
				//send 버튼 비 활성화
				btnSend.setDisable(true);
			});
			
			//socket 필드가 null이 아니고, 현재 닫혀 있지 않을 경우
			if(socket != null && !socket.isClosed()) {
				//socket 닫는다
				socket.close();
			}
		} catch (IOException e) {
			
		}
	}
	
	void receive() {
		//반복적으로 읽기 위해 무한 루프 작성
		while(true) {
			try {
				//받은 데이터를 저장할 길이가 100인 바이트 배열 생성
				byte[] byteArr = new byte[100];
				//socket으로 부터 inputStream을 얻는다
				InputStream inputStream = socket.getInputStream();
				//서버가 비정상적으로 종료했을 경우 IOException 발생
				//서버가 데이터를 보내기 전까지 블로킹되며, 데이터를 받으면 byteArr에 저장하고
				//받은 바이트 개수를 readByteCount에 저장
				int readByteCount = inputStream.read(byteArr);
				
				//서버가 정상적으로 socket의 close()를 호출했을 경우
				if(readByteCount == -1) {
					throw new IOException();
				}
				
				//정상적으로 데이터를 받았을 경우 UTF-8로 디코딩한 문자열 얻기
				String data = new String(byteArr, 0, readByteCount, "UTF-8");
				
				Platform.runLater(()->displayText("[받기 완료]" + data));
			} catch (Exception e) {
				//서버가 비정상적으로 연결 끊기면 위에서 IOException 발생
				Platform.runLater(()->displayText("[서버 통신 안됨]"));
				stopClient();
				//무한 루프 빠져나가기
				break;
			}
		}
	}
	TextArea txtDisplay;
	TextField txtInput;
	Button btnConn, btnSend;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);
		
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0,0,2,0));
		root.setCenter(txtDisplay);
		
		BorderPane bottom = new BorderPane();
		txtInput = new TextField();
		txtInput.setPrefSize(60, 30);
		BorderPane.setMargin(txtInput, new Insets(0,1,1,1));
		
		btnConn = new Button("Start");
		btnConn.setPrefSize(60, 30);
		
		//start 와 stop 버튼을 클릭했을 때 이벤트 처리 코드
		btnConn.setOnAction(e->{
			if(btnConn.getText().equals("start")) {
				startClient();
			}
			else if(btnConn.getText().equals("stop")) {
				stopClient();
			}
		});
		
		btnSend = new Button("send");
		btnSend.setPrefSize(60, 30);
		btnSend.setDisable(true);
		//send 버튼을 클릭 했을 때 이벤트 처리 코드
		btnSend.setOnAction(e->send(txtInput.getText()));
		
		bottom.setCenter(txtInput);
		bottom.setLeft(btnConn);
		bottom.setRight(btnSend);
		
		root.setBottom(bottom);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client");
		primaryStage.setOnCloseRequest(event->stopClient());
		primaryStage.show();
		
	}
	
	// TextArea에 문자열을 추가하는 메소드
	void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
