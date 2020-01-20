package Multi_Chatting;

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
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					//소켓 생성 및 연결 요청
					socket = new Socket();
					socket.connect(new InetSocketAddress("localhost", 5001));
					
					Platform.runLater(()->{
						displayText("[연결 완료: " + socket.getRemoteSocketAddress() + "]");
						btnConn.setText("stop";)
						btnSend.setDisable(false);
					});
				} catch (Exception e) {
					Platform.runLater(()->displayText("[서버 통신 안됨]"));
					if(!socket.isClosed()) {
						stopClient();
					}
					return;
				}
				receive();
			}
		};
		thread.start();
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
