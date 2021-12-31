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

public class StopWaitS implements Runnable{

	private static int seqnum=16;
	
	private static int N = 1;

	
	public StopWaitS() {
		super();

	}

	@Override
  public void run() {

  		Send();

  }

  public static void main(String[] args) {
  	new Thread(new StopWaitS()).start();

  }

  // �������ݲ���
	private static int SendDataPort = 10241;//���շ��˿ں�
	private static int ReceiveAckPort = 10240;//���ͷ��˿ں�
	private static DatagramSocket SenderSocket;
	private static DatagramPacket SendDataPacket;
	private static DatagramSocket ReceiveAckSocket;
	private static DatagramPacket ReceiverAckPacket;
	private static int send_base = 0;
	private static int nextseqnum = 0;
	private static boolean flag = false;//�Ƿ�ʼ��ʱ�ı�־
	private static int timeout = 2;//��ʱʱ�䶨Ϊ5s
	private static String filestr = new String();
	private static byte[] B;
	private static int team;
	private static int times = 1;
	private ScheduledExecutorService executor;
	
	
	/**
	 * ��ʼ��ʱ�������¼�ʱ����ʱʱ��Ϊ5s
	 */
	public void TimeBegin() 
	{
		TimerTask task=new TimerTask() 
		{
			@Override
			public void run() 
			{
			  //UDP���ݰ�ÿ���ܹ��������󳤶� = MTU(1500B) - IPͷ(20B) -UDPͷ��8B��= 1472Bytes
			  //û����Ϣ����ʱ�ͷ��ء�
				if (send_base>= Math.ceil(B.length / 1469)-1) //ȡ���ڵ��ڵ��������
				{
				  executor.shutdown();
				  return;
				}
				try 
				{
				  //���¿�ʼ��ʱ������
						byte[] tempb = GetPart(send_base);
						String temp = new String(tempb);
//						String s = new String(send_base%seqnum + ":" + temp);
						String s;
		        if(send_base%seqnum < 10)
		        {
		          s = new String("0" + send_base%seqnum + ":" + temp);
		        }
		        else
		        {
		          s = new String(send_base%seqnum + ":" + temp);
		        }
						byte[] data = s.getBytes();
						DatagramPacket SenderPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),SendDataPort);
						//���ն˵�ַҲ�Ǳ���
						SenderSocket.send(SenderPacket);
						System.out.println("�ط�����:" + send_base%seqnum+" �ط����ǵ�"+send_base+"����");
						TimeBegin();
				} catch (Exception e) {
				}
			}
		};
		
		if (!flag) {
			flag = true;
		} else {
		  executor.shutdown();
		}
		executor=Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(task, timeout, timeout, TimeUnit.SECONDS);
		
	}

	/**
	 * ������ʱ
	 */
	public void TimeEnd() 
	{
		if (flag) 
		{
			executor.shutdown();
			flag = false;
		}
	}

	/**
   * ���ü�ʱ��
   */
  public void TimeReset() 
  {
  	if (send_base == nextseqnum)
  	{
  		TimeEnd();
  	} 
  	else 
  	{
  		TimeBegin();
  	}
  }

  /**
	 * ��ȡ�ļ�������filestr���ֽ�����B��
	 * 
	 * @param filename
	 */
	public static void ReadFilebyLines(String filename) 
	{
	  filename = "src\\lab2\\"+filename;
	  File file = new File(filename);
		BufferedReader reader = null;
		try 
		{
			reader = new BufferedReader(new FileReader(file));
			String tempstr = null;
			while ((tempstr = reader.readLine()) != null) 
			{
				filestr += tempstr + "\r\n";
			}
			reader.close();
			B = filestr.getBytes();

		} catch (IOException e) 
		{
			e.printStackTrace();
		} finally 
		{
			if (reader != null) 
			{
				try 
				{
					reader.close();
				} catch (IOException e1) 
				{
				}
			}
		}
	}

	/**
	 * ����nextseqnum���Ҫ������ֽ���Ƭ(�ֿ鴫�䣩
	 * 
	 * @param nextseqnum
	 *            Ҫ����ķ������
	 * @return �ֽ����飬Ҫ������ֽ�
	 */
	public byte[] GetPart(int nextseqnum) 
	{
		byte[] temp = new byte[1469];
		for (int i = 0; i < 1469; i++) 
		{
			if (nextseqnum * 1469 + i >= B.length) 
			{
				break;
			}
			temp[i] = B[nextseqnum * 1469 + i];
		}
		return temp;
	}

	/**
	 * ��������
	 */
	public void Send() 
	{
		ReadFilebyLines("data.txt");
		team=(int) Math.ceil(B.length/1469);
		try 
		{
			SenderSocket = new DatagramSocket();
			ReceiveAckSocket = new DatagramSocket(ReceiveAckPort);
			while (true) 
			{
				SendtoReciver();
				ReceiveACK();
				if (send_base >= team) 
				{
				  break;
				}
			}
			executor.shutdown();
			System.out.println("���ͽ���.");
		} catch (Exception e) {
		}
	}

	/**
	 * �������ݸ����շ�
	 */
	public void SendtoReciver() 
	{
		try 
		{
			//�Դ��ڳ���Ϊ�ж�����.
		  while (nextseqnum < send_base + N) 
			{
				if (send_base>= team||nextseqnum>=team) 
				{
					break;
				}
				byte[] tempb = GetPart(nextseqnum);
				String temp = new String(tempb);
				//�����ݶ�����ϱ�����кŵ��ײ�
//				String s = new String(nextseqnum%seqnum + ":" + temp);
				String s;
        if(nextseqnum%seqnum < 10)
        {
          s = new String("0" + nextseqnum%seqnum + ":" + temp);
        }
        else
        {
          s = new String(nextseqnum%seqnum + ":" + temp);
        }
				byte[] data = s.getBytes();
				SendDataPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SendDataPort);
				// ģ�����ݰ���ʧ
				if (nextseqnum % 5 != 0) 
				{
					SenderSocket.send(SendDataPacket);
					System.out.println("���ͷ���:" + nextseqnum%seqnum+" ���͵��ǵ�"+nextseqnum+"����");
				} 
				else 
				{
					System.out.println("ģ�����" + nextseqnum%seqnum + "��ʧ"+" ��ʧ���ǵ�"+nextseqnum+"����");
				}
				if (send_base == nextseqnum) 
				{
					TimeBegin();
				}
				nextseqnum++;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ACK
	 * @throws InterruptedException 
	 */
	public void ReceiveACK() throws InterruptedException 
	{
		try {
		  //���������ݵ����
			if (send_base>=team) 
			{
				return;
			}
			//ACK��Ϣ�Ĵ�СΪ10bytes.
			byte[] bytes = new byte[10];
			ReceiverAckPacket = new DatagramPacket(bytes, bytes.length);
			ReceiveAckSocket.receive(ReceiverAckPacket);
			String ackString = new String(bytes, 0, bytes.length);
			String acknum = new String();
			for (int i = 0; i < ackString.length(); i++) 
			{
				//ȡ��ACK��Ϣ�е����ACK���к�
			  if (ackString.charAt(i) >= '0' && ackString.charAt(i) <= '9') 
				{
					acknum += ackString.charAt(i);
				} 
				else 
				{
					break;
				}
			}
			int ack = Integer.parseInt(acknum);
			if (ack % 6 == 0 && times == 1) 
      {
        System.out.println("ģ��ACK" + ack + "��ʧ");
        times++;
      } 
      else 
      {
        System.out.println("���յ�"
            + "ACK" + ack);
        int m;
        //Խ����е����.
        if (((send_base%seqnum)>ack)&&((nextseqnum/seqnum)>(send_base/seqnum))&&(ack<=((send_base+N)%N))) 
        {
          m=send_base/seqnum*seqnum+ack+seqnum+1;
        }
        else 
        {
          m=send_base/seqnum*seqnum+ack+1;
        }
        send_base = Math.max(send_base, m);
      }
			TimeReset();
		} catch (IOException e) {
		}
	}

	
}

