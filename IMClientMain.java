import javax.swing.*;

public class IMClientMain {
	
	public static void main(String[] args){
		IMClient user = new IMClient("127.0.0.1");				//127.0.0.1 = Local Host
		  
		user.startRunning();
		
	}

}
