package lab1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProxyThread implements Runnable {
  // ����http�˿ںţ�Ĭ��Ϊ80
  static int HttpPort = 80;
  static int size=100000;

  // ���峬ʱʱ��
  static int timeout = 500000;

  //�ͻ��˺ʹ����������socket
  private Socket ClientSocket = null;
  private Socket SerSocket = null;

  //socket��д��д��������װ
  /*
  �ͻ����ϵ�ʹ��
  1.getInputStream�������Եõ�һ�����������ͻ��˵�Socket�����ϵ�getInputStream�����õ���������ʵ���Ǵӷ������˷��ص����ݡ�
  2.getOutputStream�����õ�����һ����������ͻ��˵�Socket�����ϵ�getOutputStream�����õ����������ʵ���Ƿ��͸��������˵����ݡ�
  ���������ϵ�ʹ��
  1.getInputStream�����õ�����һ��������������˵�Socket�����ϵ�getInputStream�����õ�����������ʵ���Ǵӿͻ��˷��͸��������˵���������
  2.getOutputStream�����õ�����һ�������������˵�Socket�����ϵ�getOutputStream�����õ����������ʵ���Ƿ��͸��ͻ��˵����ݡ�
   */
  private InputStream ClientInputStream = null;//�ͻ���������
  private InputStream SeverInputStream = null;//Ŀ�ķ�����������
  private BufferedReader ClientBufferReader = null;//�ͻ��������ֽ���
  private BufferedReader SeverBufferReader = null;//Ŀ�����������ֽ���
  private OutputStream ClientOutputStream = null;//�ͻ��������
  private OutputStream SeverOutputStream = null;//Ŀ�ķ����������
  private PrintWriter ClientPrintWriter = null;//�ͻ�������ֽ���
  private PrintWriter SeverPrintWriter = null;//Ŀ����������ֽ���

  // ���ü�ֵ�Խ��л���
  // ���󱻻���ľ���ʱ��
  static Map<String, String> timecache = new HashMap<>();

  // ���󱻻���ľ�������
  static Map<String, List<Byte>> bytescache = new HashMap<>();

  //������վ���˵��б�
  static List<String> WebsiteFilter=new ArrayList<>();
  
  //���ڵ���
  static Map<String,String> fishguide=new HashMap<>();
  
  
  public ProxyThread(Socket clientsocket) throws IOException {
    super();
    this.ClientSocket = clientsocket;
    this.ClientInputStream = clientsocket.getInputStream();
    this.ClientBufferReader = new BufferedReader(new InputStreamReader(ClientInputStream));
    this.ClientOutputStream = clientsocket.getOutputStream();
    this.ClientPrintWriter = new PrintWriter(ClientOutputStream);
    
    WebsiteFilter.add("jwts.hit.edu.cn");
    fishguide.put("jwes.hit.edu.cn", "http://www.hit.edu.cn/");
    
  }

  /**
   * ����httpͷ����Ϣ����ȡmethod��url��host��cookie
   */
  public HttpHeader ParseHeader(List<String> header) {
    String firstLine = header.get(0);
    String method = null;
    String url = null;
    String host = null;
    String cookie = null;
    if (firstLine.charAt(0) == 'G') {
      method = "GET";
      url = firstLine.substring(4, firstLine.length() - 9);
    } else if (firstLine.charAt(0) == 'P') {
      method = "POST";
      url = firstLine.substring(5, firstLine.length() - 9);
    } else {
      method = "CONNECT";//https����������
    }
    //�г�Host��Cookie����Ϣ
    for (int i = 0; i < header.size(); i++) {
      if (header.get(i).startsWith("Host")) {
        host = header.get(i).substring(6, header.get(i).length());
      } else if (header.get(i).startsWith("Cookie")) {
        cookie = header.get(i).substring(8, header.get(i).length());
      }
    }
    HttpHeader httpHeader = new HttpHeader(method, url, host, cookie);
    return httpHeader;
  }


  /**
   * ��ȡ������������������ͨ���׽���
   * @param host ������
   * @param port �˿�
   * @param times ���Ӵ���
   * @return �����������������Ĺ�ͨ���׽���
   * @throws UnknownHostException
   * @throws IOException
   */
  public Socket ConnectToServer(String host, int port, int times) throws UnknownHostException, IOException {
    for (int i = 0; i < times; i++) {
      SerSocket = new Socket(host, port);
      //���ö˿�����ʱ��
      SerSocket.setSoTimeout(timeout);
      SeverInputStream = SerSocket.getInputStream();
      SeverBufferReader = new BufferedReader(new InputStreamReader(SeverInputStream));
      SeverOutputStream = SerSocket.getOutputStream();
      SeverPrintWriter = new PrintWriter(SeverOutputStream);

      if (SerSocket != null) {
        return SerSocket;
      }
    }
    return null;
  }

  /**
   * ��������������������������Ϣ
   * @param lst ������Ϣ
   */
  public void SendToServer(List<String> lst) {
    System.out.println("\n=========��������������������������Ϣ=========");
    for (int i = 0; i < lst.size(); i++) {
      String line = lst.get(i);
      SeverPrintWriter.write(line + "\r\n");
      System.out.println(line);
    }
    SeverPrintWriter.write("\r\n");
    SeverPrintWriter.flush();
  }
  
  /**
   * û�л��������£�����������ӷ�����ת����Ӧ��Ϣ���ͻ���
   * @param url
   * @return
   */
  public boolean SendBackToClient(String url) {
    /*
     * �������bytes���飬��������ASCII����unicode����Ĳ��죬�޷�ʶ��
     */
    
    System.out.println("\n=========ת����Ӧ��Ϣ���ͻ���=========\n");
    
    List<Byte> lst=new ArrayList<>();
    
    try {
      // String time = null;
      byte bytes[] = new byte[size];
      int len;
      while (true) {
        if ((len = SeverInputStream.read(bytes)) >= 0) {
          ClientOutputStream.write(bytes, 0, len);
          for (int i=0;i<len;i++) {
            lst.add(bytes[i]);
          }
        } else if (len < 0) {
          break;
        }
      }
      
      byte b[]=new byte[lst.size()];
      for (int i=0;i<lst.size();i++) {
        b[i]=lst.get(i);
      }
      //������������תΪ�ַ���
      String s=new String(b);
      String time=findTime(s);
      timecache.put(url, time);
      bytescache.put(url, lst);
      
      ClientPrintWriter.write("\r\n");
      ClientPrintWriter.flush();
      ClientOutputStream.close();
    } catch (IOException e) {
    } catch (Exception e) {
    }
    return true;
  }
  
  /**
   * �����ַ�����ȡ���е�Dateʱ��
   * @param s
   * @return
   */
  public String findTime(String s) {
    int begin=s.indexOf("Date");
    int end=s.indexOf("GMT");
    //System.out.println(s.substring(begin+6, end+3));
    return s.substring(begin+6, end+3);
  }

  /**
   * �л��������£����ͻ�����Ҫ����Ϣ
   * 1.�ڻ���ʱ��������û���޸Ķ����򽫻���ֱ�ӷ��͸��ͻ���
   * 2.�ڻ���ʱ���������޸Ķ����ˣ���Sever���¶����͸��ͻ���
   * @param header
   * @param host
   * @param url
   * @return
   */
  public boolean SendBackToClientWithCache(List<String> header,String host, String url) {
    String modifiTime=timecache.get(url);
    // ����������GET������������
    SeverPrintWriter.write(header.get(0) + "\r\n");
    SeverPrintWriter.write("Host: "+host + "\r\n");
    
    System.out.println("Modified Time:"+modifiTime);
    String str = "If-modified-since: " + modifiTime + "\r\n";
    SeverPrintWriter.write(str);
    SeverPrintWriter.write("\r\n");
    SeverPrintWriter.flush();

    try {
      String ServerMessage = SeverBufferReader.readLine();
      //System.out.println(ServerMessage);
      if (ServerMessage == null) {
        return false;
      }
      System.err.println("\n��������Ӧ��Ϣ�ײ���"+ServerMessage);
      // ����������ڻ���ʱ���δ�޸Ķ���ֱ��ת�����ͻ��˻���
      if (ServerMessage.contains("Not Modified")) 
      {
        System.err.println("\n=======����δ����======");
        List<Byte> lst=bytescache.get(url);
        byte bytes[]=new byte[lst.size()];
        for (int i=0;i<lst.size();i++) {
          bytes[i]=lst.get(i);
        }
        ClientOutputStream.write(bytes);
        ClientPrintWriter.write("\r\n");
        ClientPrintWriter.flush();
        ClientPrintWriter.close();
      }
      //����޸Ĺ��������µĶ����ֽڷ����ͻ���
      else if (ServerMessage.contains("OK")) 
      {
        System.err.println("\n=======�����Ѹ���======");
        DataOutputStream d=new DataOutputStream(ClientOutputStream);
        byte[] b=(ServerMessage+"\r\n").getBytes();
        d.write(b);
        //ClientPrintWriter.write("\r\n");
        byte bytes[] = new byte[size];
        int len;
        while (true) {
          if ((len = SeverInputStream.read(bytes)) > 0) {
            ClientOutputStream.write(bytes, 0, len);
          } else if (len < 0) {
            break;
          }
        }
        //������Ч��ȥ��.
        timecache.remove(url);
        bytescache.remove(url);

        
        ClientPrintWriter.write("\r\n");
        ClientPrintWriter.flush();
        ClientOutputStream.close();
      }
      else {  //http��Ϣ��Ч�����
        bytescache.remove(url);
        timecache.remove(url);
        while (!SeverBufferReader.readLine().equals("")) 
        {
          //ˢ����Ч��Ϣ
        }
        byte bytes[] = new byte[size];
        int len;
        while (true) {
          if ((len = SeverInputStream.read(bytes)) >= 0) {
            //ClientOutputStream.write(bytes, 0, len);
          } else if (len < 0) {
            break;
          }
        }
        run();//������һ��
      }
    } catch (IOException e1) {
    }

    return true;
  }
  
  public void Filter() {
    for (int i=1;i<419;i++) {
      ClientPrintWriter.write("�Ƿ���վ��FBI WARNING!\t\t");
      if (i%11==0) {
        ClientPrintWriter.write("\r\n");
      }
    }
    ClientPrintWriter.write("\r\n");
    ClientPrintWriter.flush();
    ClientPrintWriter.close();
  }
  
  public void fishing(List<String> header){

    header.clear();
    header.add("GET http://www.hit.edu.cn/ HTTP/1.1");
    header.add("Host: www.hit.edu.cn");
    header.add("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
    header.add("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    header.add("Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
    header.add("Accept-Encoding: gzip, deflate");
    header.add("Connection: keep-alive");
    header.add("Upgrade-Insecure-Requests: 1");
    HttpHeader httpHeader=ParseHeader(header);
    
    String host=httpHeader.host;
    String url=httpHeader.url;
    /*
     * ��ȡ������������������Socket
     */
    try {
      if (ConnectToServer(host, HttpPort, 5) == null) {
        return;
      }
    } catch (UnknownHostException e) {
    } catch (IOException e) {
    }
    System.out.println("url="+url);
    System.out.println("host="+host);
    
    boolean flag=timecache.containsKey(url)&&bytescache.containsKey(url);
    if (!flag) {
      /*
       * û��Cache�����
       */
      System.err.println("\n=========�޻���=========");
      SendToServer(header);
      SendBackToClient(url);
    } else {
      /*
       * ��Cache�����
       */
      System.err.println("\n=========�л���=========");
      SendBackToClientWithCache(header, host,url);
    }


    
}   


  @Override
  public void run() {
    try {
      ClientSocket.setSoTimeout(timeout);
      String line = null;
      List<String> header = new ArrayList<>();
      // ��ȡ�ӿͻ��˷��͵�������Ϣ
      line = ClientBufferReader.readLine();
      if (line == null) {
        return;
      }
      header.add(line);
      System.out.println("\n=========�ͻ�������=========");
      System.out.println(line);
      while (!(line = ClientBufferReader.readLine()).equals("")) {
        header.add(line);
        System.out.println(line);
      }
      
      //����������Ϣ��ȡhttp��Ϣ
      HttpHeader httpHeader = ParseHeader(header);
      String url = httpHeader.url;
      String host = httpHeader.host;

     //��վ���������㣩 
      for (String h:fishguide.keySet()) {
        if (host.contains(h)) {
          fishing(header);
          return;
        }
      }
      //������https���CONNECT����
      if (httpHeader.method.equals("CONNECT")) {
        return;
      }

     //��վ����
      for (int i=0;i<WebsiteFilter.size();i++) {
        if (host.contains(WebsiteFilter.get(i))) {
          Filter();
          System.err.println("��ֹ���ʣ�"+url);
          return;
        }
      }
      
            
      //��ȡ������������������Socket

      if (ConnectToServer(host, HttpPort, 5) == null) {
        return;
      }
      System.out.println("url="+url);
      System.out.println("host="+host);
      
      boolean flag= timecache.containsKey(url)&&bytescache.containsKey(url);
      if (!flag) {
       // û��Cache�����
        System.err.println("\n=========�޻���=========");
        SendToServer(header);
        SendBackToClient(url);
      } else {
       // ��cache�����
        System.err.println("\n=========�л���=========");
        SendBackToClientWithCache(header, host,url);
      }

    } catch (SocketException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      //e.printStackTrace();
    }catch (Exception e){
      //e.printStackTrace();
    }

  }
}