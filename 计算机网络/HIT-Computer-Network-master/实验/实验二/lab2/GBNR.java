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

public class GBNR implements Runnable{
	private static int N = 8;


	public GBNR() {
		super();

	}
	 @Override
	  public void run() {

	      Receive();
	    
	  }

	  public static void main(String[] args) {
	    new Thread(new GBNR()).start();
	  }

    private static int seqnum=16;
  // �������ݲ���
	private static int SendAckPort = 10240;
	private static int ReceiveDataPort = 10241;
	private static DatagramSocket ReciverSocket;
	private static DatagramPacket SendAckPacket;
	private static int expectedSeqNum = 0;
	
	private static int last=-1;
	/**
   * �������ݲ�����ACK
   */
  public void Receive() {
    try {
      ReciverSocket = new DatagramSocket(ReceiveDataPort);
      while (true) 
      {
        byte[] data= new byte[1472];

        DatagramPacket packet = new DatagramPacket(data, data.length);
        ReciverSocket.receive(packet);
        byte[] d = packet.getData();
        String message = new String(d);
        String num = new String();
        //ȡ���ð������к�
        for (int i = 0; i < message.length(); i++) 
        {
          if (message.charAt(i) <= '9' && message.charAt(i) >= '0') 
          {
            num = num + message.charAt(i);
          } else {
            break;
          }
        }
        // �����ۻ�ȷ�ϣ�������Ҫ����ŶεĻ�ֱ�Ӷ���
        if (expectedSeqNum == Integer.valueOf(num)) 
        {
          int ack = expectedSeqNum;
          SendACK(ack);
          expectedSeqNum = (expectedSeqNum + 1)%seqnum;
          last=ack;
        }
        else 
        {
          if (last>=0) 
          {
            SendACK(last);
          }
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
    try 
    {
      ACK ACK = new ACK(ack);
      SendAckPacket = new DatagramPacket(ACK.ackByte, ACK.ackByte.length, InetAddress.getLocalHost(),SendAckPort);
      ReciverSocket.send(SendAckPacket);
      System.out.println("����ACK" + ack);
    } catch (Exception e) 
    {
    }
  }


}

