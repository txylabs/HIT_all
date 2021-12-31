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

public class SRS implements Runnable{

private static int seqnum = 16;


	public SRS() {
		super();

	}

	@Override
  public void run()
  {
  		Send();
  }

  public static void main(String[] args) {
  	new Thread(new SRS()).start();
//  	new Thread(new SRS(1)).start();
  }

  // �������ݲ���
	private static int N = 8;
	private static int SendDataPort = 10241;
	private static int ReceiveAckPort = 10240;
	private static DatagramSocket SenderSocket;
	private static DatagramPacket SendDataPacket;
	private static DatagramSocket ReceiveAckSocket;
	private static DatagramPacket ReceiverAckPacket;
	private static int send_base = 0;
	private static int nextseqnum = 0;
	private static int timeout = 4;
	 private static int times = 1;
	private static String filestr = new String();
	private static byte[] B;
	private static int team;
	private static boolean[] ackarray = new boolean[N];//�ж�ÿ�����к��Ƿ񱻽��շ����յ��˵ı�־����
	private static boolean[] flags = new boolean[N];//�ж�ÿ�����кŵļ�ʱ���Ƿ����ı�־����
	private static ScheduledExecutorService[] executors = new ScheduledExecutorService[N];

	/**
	 * ��ʼ��ʱ�������¼�ʱ����ʱʱ��Ϊ2s
	 */
	public void TimeBegin(int q, int x) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (send_base >= Math.ceil(B.length / 1469)-1) {
					return;
				}
				try {
					byte[] tempb = GetPart(x);
					String temp = new String(tempb);
					String s;
					if(nextseqnum%seqnum < 10)
	        {
	          s = new String("0" + x%seqnum + ":" + temp);
	        }
	        else
	        {
	          s = new String(x%seqnum + ":" + temp);
	        }
					byte[] data = s.getBytes();
					DatagramPacket SenderPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),SendDataPort);
					SenderSocket.send(SenderPacket);
					System.out.println("�ط�����:" + x % seqnum + ";�ط����ǵ�" + x + "����");
				} catch (Exception e) {
				}
			}
		};
		if (!flags[q]) {
			flags[q] = true;
		} else {
			executors[q].shutdown();
		}
		executors[q] = Executors.newSingleThreadScheduledExecutor();
		executors[q].scheduleWithFixedDelay(task, timeout, timeout, TimeUnit.SECONDS);
	}

	/**
	 * ������ʱ
	 */
	public void TimeEnd(int q, int x) {
		if (flags[q]) {
			flags[q] = false;
			executors[q].shutdown();//��ֹ��ʱ��
		}
	}

	/**
	 * ��ȡ�ļ�������filestring���ֽ�����B��
	 * 
	 * @param filename
	 */
	public static void ReadFilebyLines(String filename) {
		filename = "src\\lab2\\"+filename;
	  File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempstr = null;
			while ((tempstr = reader.readLine()) != null) {
				filestr = filestr + tempstr + "\r\n";
			}
			reader.close();
		//����������ת��Ϊbyte�浽B������
			B = filestr.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * ����nextseqnum���Ҫ������ֽ�
	 * 
	 * @param nextseqnum Ҫ����ķ������
	 *            
	 * @return �ֽ����飬Ҫ������ֽ�
	 */
	public byte[] GetPart(int nextseqnum) {
		byte[] temp = new byte[1469];
		for (int i = 0; i < 1469; i++) {
			if (nextseqnum * 1469 + i >= B.length) {
				break;
			}
			temp[i] = B[nextseqnum * 1469 + i];
		}
		return temp;
	}

	/**
	 * ��������
	 */
	public void Send() {
		ReadFilebyLines("data.txt");
		team = (int) Math.ceil(B.length / 1469);
		try {
			SenderSocket = new DatagramSocket();
			ReceiveAckSocket = new DatagramSocket(ReceiveAckPort);
			while (true) {
				SendToReciver();
				ReceiveACK();
//				System.out.println("send_base=" + send_base);
				if (send_base >= team) {
					break;
				}
			}
		  //������Ϻ�Ҫ���˹ض�ʱ��
			for(int i = 0 ; i < N ; i++)
			{
			  executors[i].shutdown();
			}
			System.out.println("���ͽ���.");
		} catch (Exception e) {
		  System.out.println("���ͽ���.");
		}
	}

	/**
	 * �������ݸ����շ�
	 */
	public void SendToReciver() {
		try {
			while (nextseqnum < send_base + N) {
				if (send_base >= team || nextseqnum >= team) {
					break;
				}
				byte[] tempb = GetPart(nextseqnum);
				String temp = new String(tempb);
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
				if (nextseqnum % 5 != 0) {
					SenderSocket.send(SendDataPacket);
					System.out.println("���ͷ���:" + nextseqnum % seqnum + ";���͵��ǵ�" + nextseqnum + "����");
				} else {
					System.out.println("ģ�����" + nextseqnum % seqnum + "��ʧ" + ";��ʧ���ǵ�" + nextseqnum + "����");
				}
//				System.out.println("nextseqnum=" + nextseqnum);
//				System.out.println("send_base=" + send_base);
				TimeBegin(nextseqnum - send_base, nextseqnum);
				nextseqnum++;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ACK
	 * 
	 * @throws InterruptedException
	 */
	public void ReceiveACK() throws InterruptedException {
		try 
		{
			if (send_base >= team) 
			{
				return;
			}
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
			if ( times == 1 && ack == 15) 
      {
        System.out.println("ģ��ACK" + ack + "��ʧ");
        times++;
      } 
			else
			{
  			System.out.println("���յ�ACK" + ack%seqnum);
  			int a = ack, b = ack + seqnum;
  			//�ҵ�ACK��Ϣ����İ��ţ�ͨ�����Ž���������������ŵĹ�ϵ
  			while (!(send_base >= a && send_base <= b)) 
  			{
  				a += seqnum;
  				b += seqnum;
  			}
  			//�ж��Ƿ���յ��ظ�ACK��send_base֮ǰ��ACK�����д���
  			if (b - send_base > send_base - a) 
  			{
  				ack = a;
  			} 
  			else 
  			{
  				ack = b;
  			}
  //			System.out.println("ack=" + ack);
  			if (ack >= send_base && ack < send_base + N) 
  			{
  				ackarray[ack - send_base] = true;//��ʾ�յ�ack��
  				TimeEnd(ack - send_base, ack);
  				if (ack == send_base) //���ڻ���
  				{
  					ackarray[0] = true;
  					int cnt = 0;
  					for (int i = 0; i < N; i++)
  					{
  						if (ackarray[i])
  						{
  							cnt++;
  						}
  						else 
  						{
  							break;
  						}
  					}
  					
  					for (int i = 0; i < N - cnt; i++) 
  					{
  						ackarray[i] = ackarray[i + cnt];
  						flags[i] = flags[i + cnt];
  						executors[i] = executors[i + cnt];
  					}
  					for (int i = N - cnt; i < N; i++) 
  					{
  						ackarray[i] = false;
  						executors[i] = null;
  						flags[i] = false;
  
  					}
  					System.out.println("������󻬶�" + cnt);
  					send_base = send_base + cnt;
  				}
  			}
			}
		} catch (IOException e) {
		}
		
		
		
	}

	

}
