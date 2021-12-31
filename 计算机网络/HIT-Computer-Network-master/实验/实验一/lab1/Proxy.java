package lab1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Proxy {
  private static ServerSocket ProxyServerSocket;
  private static List<String> UserFilter=new ArrayList<>();

  static boolean InitSocket(int port) {
    try {
      //���ö˿ں�
      ProxyServerSocket=new ServerSocket(port);
      //�趨�����ʱ��
      ProxyServerSocket.setSoTimeout(1000000);
    } catch (IOException e) {
      System.out.println("��ʼ��ʧ��");
      return false;
    }
    return true;
    
  }

  static boolean UserFilterInit() {
    UserFilter.add("127.0.0.1");
    UserFilter.add("1.1.1.1");
    UserFilter.add("5.6.7.8");
    UserFilter.add("0.0.0.0");
    return UserFilter.size()>0;
  }
  
  
  public static void main(String[] args) {
    int ProxyPort=10240;
    System.out.println("���������׼����.....");
    if (InitSocket(ProxyPort)) {
      System.out.println("��ʼ�����˿ڣ�"+ProxyPort);
    }
    else//������������ڼ������׽��ִ���ʧ�ܾ��˳�����
    {
      System.exit(0);
    }
    
    //UserFilterInit();
    while (true) {
      try {
        //����
        Socket socket=ProxyServerSocket.accept();
          
        String address=socket.getInetAddress().getHostAddress();
        for (int i=0;i<UserFilter.size();i++) {
          if (address.equals((UserFilter.get(i)))) {
            System.err.println("�û�IP:"+address+"������");
            System.exit(0);
          }
        }
        
        //�������߳�������ͻ��˵��������˵�ͨ��
        new Thread(new ProxyThread(socket)).start();
        
      } catch (IOException e) {
        System.err.println("���ӳ�ʱ");
      }
    
    }
  }
}
