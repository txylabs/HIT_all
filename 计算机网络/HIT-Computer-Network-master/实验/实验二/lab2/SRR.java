package lab2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SRR implements Runnable{
	

	public SRR() {
		super();

	}
	
	@Override
  public void run() {

  		Receive();

  }

  public static void main(String[] args) {
  	new Thread(new SRR()).start();

  }

  private static int seqnum=16;
	
	 // �������ݲ���
  private static int SendAckPort = 10240;
  private static int ReceiveDataPort = 10241;
  private static DatagramSocket ReciverSocket;
  private static DatagramPacket SendAckPacket;
  private static int RecN=5;
  private static int N = 8;
  private static int rev_base=0;
  private static boolean ack[]=new boolean[RecN];
  
  /**
   * �������ݲ�����ACK
   */
  public void Receive() {
    try {
      ReciverSocket = new DatagramSocket(ReceiveDataPort);
      while (true) 
      {
        byte[] data = new byte[1472];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        ReciverSocket.receive(packet);
        byte[] d = packet.getData();
        String message = new String(d);
        String num = new String();
        for (int i = 0; i < message.length(); i++)
        {
          if (message.charAt(i) <= '9' && message.charAt(i) >= '0') 
          {
            num = num + message.charAt(i);
          } 
          else 
          {
            break;
          }
        }
        int rev_num=Integer.valueOf(num);
      //�ҵ����ţ�ͨ�����Ž���������������ŵĹ�ϵ
        int a=rev_num,b=rev_num+seqnum;
        while (!(rev_base>=a&&rev_base<=b)) 
        {
          a+=seqnum;
          b+=seqnum;
        }
        if (b-rev_base>rev_base-a) 
        {
          rev_num=a;
        }
        else 
        {
          rev_num=b;
        }
        if (rev_num>=rev_base&&rev_num<rev_base+RecN) 
        {
          SendACK(rev_num);
          if (rev_num==rev_base) //���ڻ���
          {
            ack[0]=true;
            int cnt=0;
            for (int i=0;i<RecN;i++) 
            {
              if (ack[i])
              {
                cnt++;
              }
              else 
              {
                break;
              }
            }
            for (int i=0;i<RecN-cnt;i++)
            {
              ack[i]=ack[i+cnt];
            }
            for (int i=RecN-cnt;i<RecN;i++) 
            {
              ack[i]=false;
            }
            rev_base=rev_base+cnt;
            System.out.println("������󻬶�" + cnt);
          }
          else 
          {
            ack[rev_num-rev_base]=true;//��¼���յ��ļ�
          }
        }
        else if (rev_num<rev_base&&rev_num>=rev_base-N) //�ڴ˷�Χ��һ�ɷ���ACK
        {
          SendACK(rev_num);
        }
      }
    } catch (Exception e) {
    }
  }

  /**
   * ����ACK
   * 
   * @param ack ���ص�ACK��ţ�Ϊ0��N-1
   *            
   */
  public void SendACK(int ack) {
    try {
      ACK ACK = new ACK(ack);
      SendAckPacket = new DatagramPacket(ACK.ackByte, ACK.ackByte.length, InetAddress.getLocalHost(),SendAckPort);
      ReciverSocket.send(SendAckPacket);
      System.out.println("����ACK" + ack%seqnum);
    } catch (Exception e) {
    }
  }

	
	
	


}
