package lab1;

import java.io.*;
import java.net.*;
import java.util.*;
 
//�޷����httpsЭ��
public class Proxy2 extends Thread {
  public static int CONNECT_RETRIES = 5; // ������Ŀ���������Ӵ���
  public static int CONNECT_PAUSE = 5; // ÿ�ν������ӵļ��ʱ��
  public static int TIMEOUT = 8000; // ÿ�γ������ӵ����ʱ��
  public static int BUFSIZ = 1024; // ����������ֽ���
  public static boolean logging = false; // �Ƿ��¼��־
  public static OutputStream log_S = null; // ��־�����
  public static OutputStream log_C = null; // ��־�����
  public static OutputStream log_D = null; // ��Ӧ������־
  public static int count = -1;
  public static List<String> requestInfo = new ArrayList<String>();
  public static List<String> cacheInfo;
  public static Map<String, String> cache = new HashMap<String, String>();
  Socket ssocket = null;
  // cisΪ�ͻ�����������sisΪĿ����������������������
  InputStream cis = null, sis = null;
  BufferedReader cbr = null, sbr = null; // ת��Ϊ�ַ�����ȡ���ڱȽ�
  // cosΪ�ͻ����������sosΪĿ�����������
  OutputStream cos = null, sos = null;
  PrintWriter cpw = null, spw = null;// ת��Ϊ�ַ���
  String buffer = ""; // ��ȡ����ͷ
  String URL = ""; // ��ȡ����URL
  String host = ""; // ��ȡĿ������host
  int port = 80; // �˿ں�Ϊ80
  // ��ͻ���������Socket
  protected Socket csocket;
 
  //��ĳ�ʼ��
  public Proxy2(Socket cs) {
    try {
      csocket = cs;
      cis = csocket.getInputStream(); // �����������Ϊ���������ܿͻ��˵�����
      cbr = new BufferedReader(new InputStreamReader(cis));
      cos = csocket.getOutputStream(); // �����������Ϊ��������ͻ��˷�����Ӧ
      cpw = new PrintWriter(cos);
      start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
 
  //����д��־�ĺ���
  public void writeLog(int c, int browser) throws IOException {
    if (browser == 1)
      log_C.write((char) c);
    else if (browser == 2)
      log_S.write((char) c);
    else
      log_D.write((char) c);
  }
 
  //����д��־�ĺ���
  public void writeLog(byte[] bytes, int offset, int len, int browser)
      throws IOException {
    for (int i = 0; i < len; i++)
      writeLog((int) bytes[offset + i], browser);
  }
 
  public void run() {  
      try { 
        csocket.setSoTimeout(TIMEOUT);
        buffer = cbr.readLine(); // ��ȡ�ײ���
        System.out.println("�ײ��У�" + buffer);
        if(buffer.contains("CONNECT"))
        {
          return;
        }
        URL = getRequestURL(buffer);
        System.out.println(URL);
        //��վ���������㣩
        if(URL.equals("http://www.sogou.com/")){
          URL = "http://www.taobao.com/";
          buffer = "GET "+URL+" HTTP/1.1"; 
          requestInfo.add("Accept: text/html, application/xhtml+xml, */*"); 
          requestInfo.add("Accept-Language: zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3"); 
          requestInfo.add("User-Agent: Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.2; WOW64; Trident/6.0)");
          requestInfo.add("Accept-Encoding: gzip, deflate");
          requestInfo.add("Proxy-Connection: Keep-Alive");
          requestInfo.add("DNT: 1");
          requestInfo.add("Host: www.taobao.com");
          requestInfo.add("Cookie: thw=cn; isg=0BC4B5EFD7C7FCFEB73317770EA7F3F5; l=AeVoHE44ZTsle7DjpW8fBSV7pbSl-2U7; cna=GCHeDZQAVwkCAdvZ9Apwg8rH; t=1a1386bec550ab78d1aaf5ad5b90e044; mt=ci%3D-1_0; _med=dw:1366&dh:768&pw:1366&ph:768&ist:0");
        }
        else if(URL.equals("http://www.qq.com/")) {
          URL = "";//Ϊ�˺������εĲ���
        }
        int n;
        // ��ȡhost
        n = URL.indexOf("//");
        if (n != -1)
          host = URL.substring(n + 2); // �г�www.xxxxx.com/��ʽ
        n = host.indexOf('/');
        if (n != -1)
          host = host.substring(0, n);//�г�www.xxxxx.com��ʽ

 
        // �������ܴ��ڵĶ˿ں�
        n = host.indexOf(':');
        if (n != -1) {
          port = Integer.parseInt(host.substring(n + 1));//�г��˿ں���
          host = host.substring(0, n);//�е��˿ںŵĲ���
        }
        //���޶������ӳ��Դ�����������
        int retry = CONNECT_RETRIES;
        while (retry-- != 0 && !host.equals("")) {
          try {
            System.out.println("�˿ںţ�" + port + "������" + host);
            System.out.println("ʣ�����Ӵ����� " + retry);
            ssocket = new Socket(host, port); // ���Խ�����Ŀ������������
            break;
          } catch (Exception e) {
            //e.printStackTrace();
          }
          // δ�ɹ�����������ȴ�
          Thread.sleep(CONNECT_PAUSE);
        }
        if (ssocket != null) 
        {
          //�趨�����ʱ��
          ssocket.setSoTimeout(TIMEOUT);
          sis = ssocket.getInputStream(); // �����������Ϊ�ͻ��˽�����Ӧ
          sbr = new BufferedReader(new InputStreamReader(sis));
          sos = ssocket.getOutputStream(); // �����������Ϊ�ͻ��˷�������
          spw = new PrintWriter(sos);
          
          String modifTime = findCache(URL);// �ڻ�����Ѱ���Ƿ�֮ǰ�Ѿ���������url����Ϣ
          System.out.println("��һ���޸ĵ�ʱ��Ϊ��" + modifTime);
          writeLog(buffer.getBytes(), 0, buffer.length(), 1);
          writeLog(buffer.getBytes(), 0, buffer.length(), 3);
          writeLog("\r\n".getBytes(), 0, 2, 3);
          int i = 0;
          // ֮ǰû�л���
          if (modifTime == null) 
          {
            while (!buffer.equals("")) //�����趨����վ��δ���ε���վͨ����ѭ����
            {
              buffer += "\r\n";
              if(i == 0)
                System.out.println("��ͻ������������������");
              if(buffer.contains("www.taobao.com")) //����ǵ�����վ�ͷ��͵�����վ�ı���
              { 
                int k = 0;
                while(requestInfo.size() > k)
                {
                  spw.write(buffer);//������Ӧ�ı���
                  buffer = requestInfo.get(k++);
                  buffer += "\r\n";
                  cache.replace(URL, cache.get(URL) + buffer);
                }
                break;
              }
              else
              { 
                //��������
                spw.write(buffer); 
                writeLog(buffer.getBytes(), 0, buffer.length(), 1);
                System.out.print(buffer);
                buffer = cbr.readLine();
                
                
              }
              i++;
            }
            spw.write("\r\n");
            writeLog("\r\n".getBytes(), 0, 2, 1);
            spw.flush();
            
            
            // ��ȡ����������Ӧ��Ϣ
//            int length;
//            byte bytes[] = new byte[BUFSIZ];
            int k = 0;
            String info = sbr.readLine();
            System.out.println("��������Ӧ��");
            while (!info.equals("")) 
            {
              try 
              {
                info += "\r\n";
                if(k == 0)
                {
                  cache.put(URL,info);
                }
                else
                  cache.replace(URL, cache.get(URL) + info);
                System.out.print(info);
                cpw.write(info);
                info = sbr.readLine();
                k++;
                System.out.println(k);
                
//                if ((length = sis.read(bytes)) > 0) 
//                { // ��ȡ�ͻ��˵�����ת����������ֱ��������ת������Ч
//                  cos.write(bytes, 0, length);
//                  if (logging) 
//                  {
//                    writeLog(bytes, 0, length, 1);
//                    writeLog(bytes,0,length,3);
//                  }
//                } else if (length < 0)
//                {
//                    break;//���������ѭ��
//                }
              } catch (Exception e) 
              {
                break;
              } 
            } 
            
//            if(count == 0) 
//            {
//              System.out.println(cbr.readLine());
//            }
            cpw.write("\r\n");
            
            if(logging)
            {
              writeLog("\r\n".getBytes(), 0, 2, 3);
              writeLog("\r\n".getBytes(), 0, 2, 2);
            }
            
            cpw.flush();
            
            
            
            try 
            {
              File file = new File(URL.replace(":", "").replace("/", "")+ ".txt");
//              if(!file.exists())
//              {
//                file.createNewFile();
//              }
              System.out.println(1);
              FileWriter fw = new FileWriter(file,false);
              System.out.println("�ɹ��������棺"+URL + ".txt" );
              System.out.println(cache.get(URL));
              fw.write(cache.get(URL));
              
            } catch (Exception e) {
              e.printStackTrace();
            }
          } 
          
          
          else //�л�������
          {
            buffer += "\r\n";
            //���첢��������˷���������get����
            spw.write(buffer);
            System.out.print("�����������ȷ���޸�ʱ������:");
            
            String str1 = "Host: " + host + "\r\n";
            spw.write(str1);
            System.out.println(str1);
            String str = "If-modified-since: " + modifTime + "\r\n";
            spw.write("\r\n");
            spw.flush();
            System.out.println(buffer);
            System.out.print(str1);
            System.out.print(str);
 
            String info = sbr.readLine();
            System.out.println("���������ص���Ϣ�ǣ�"+info);//��Ҫ�Ϳ���һ����Ϣ
            if (info.contains("304")) //����Ϊ����
            {
              int j = 0;
              System.out.println("����Ϊ���£�ʹ�û����е����ݡ�");
              while (j < cacheInfo.size())
              {
                info = cacheInfo.get(j++);
                info += "\r\n";
                System.out.print(info);
                cpw.write(info);
              }
              cpw.write("\r\n");
              cpw.flush();
            } 
            else 
            {
              System.out.println("�и��£�ʹ���µ�����");
              int j = 0;
              while (!info.equals("")) 
              {
                info += "\r\n";
                if(j == 0)
                {
                  System.out.println("�µ������ǣ�" );
                  cache.replace(URL, info);
                }
                else
                  cache.replace(URL, cache.get(URL) + info);
                j++;
                
                System.out.print(info);
                cpw.write(info);
                info = sbr.readLine();
              }
              cpw.write("\r\n");
              cpw.flush();
              File file = new File(URL + ".txt");
              try {
                if(!file.exists())
                {
                  file.createNewFile();
                }
                FileWriter fw = new FileWriter(file,false);
                System.out.println("�ɹ����»��棺"+URL + ".txt" );
                System.out.println(cache.get(URL));
                fw.write(cache.get(URL));
                
              } catch (Exception e) {
                // TODO: handle exception
              }
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } 
  }
 
  //�ӻ���������ȡ��URL�ַ����ķ���
  public String getRequestURL(String buffer) 
  {
    String[] tokens = buffer.split(" ");
    String URL = "";
    if (tokens[0].equals("GET"))
      for (int index = 0; index < tokens.length; index++) 
      {
        if (tokens[index].startsWith("http://")) 
        {
          URL = tokens[index];
          break;
        }
      }
    return URL;
  }
 

 
  public static void startProxy(int port, Class clobj) {
    try {
      ServerSocket ssock = new ServerSocket(port);
      while (true) 
      {
        Class[] sarg = new Class[1];
        Object[] arg = new Object[1];
        sarg[0] = Socket.class;
        try 
        {
          java.lang.reflect.Constructor cons = clobj.getDeclaredConstructor(sarg);
          //���ն�����ĵ�һ��
          arg[0] = ssock.accept();
          System.out.println("�����̣߳�"+count++);
          cons.newInstance(arg); // ����HttpProxy�����������ʵ��
        } catch (Exception e) 
        {
          Socket esock = (Socket) arg[0];
          try 
          {
            esock.close();
          } catch (Exception ec) 
          {
          }
        }
      }
    } catch (IOException e) {
      System.out.println("\nStartProxy Exception:");
      e.printStackTrace();
    }
  }
 
  // �����õļ�main����
  static public void main(String args[]) throws FileNotFoundException {
    System.out.println("�ڶ˿�10240�������������\n");
    OutputStream file_S = new FileOutputStream(new File("log_s.txt"));
    OutputStream file_C = new FileOutputStream(new File("log_c.txt"));
    OutputStream file_D = new FileOutputStream(new File("log_d.txt"));
    Proxy2.log_S = file_S;
    Proxy2.log_C = file_C;
    Proxy2.log_D = file_D; // ֱ�Ӵ洢���URl��Ӧ����Ӧ����
    Proxy2.logging = true;
    Proxy2.startProxy(10240, Proxy2.class);
  }
 
  public String findCache(String head) {
    cacheInfo = new ArrayList<String>();
//    String resul = null;
//    try {
//      // ֱ���ڴ���url����Ӧ��Ϣ���ļ��в���
//      InputStream file_D = new FileInputStream("log_d.txt");
//      String info = "";
//      while (true) 
//      {
//        int c = file_D.read();
//        if (c == -1)
//          break; // -1Ϊ��β��־
//        if (c == '\r') 
//        {
//          file_D.read();
//          break;// ����ÿһ������
//        }
//        if (c == '\n')
//          break;
//        info = info + (char) c;
//      }
//      System.out.println("��һ�εõ���" + info);
//      System.out.println("Ҫ�ҵ��ǣ�" + head);
//      int m = 0;
//      while ((m = file_D.read()) != -1 && info!=null) {
//        //System.out.println("��Ѱ�ң�"+info);
//        // �ҵ���ͬ�ģ���ô������ľ�����Ӧ��Ϣ�����ϴ��޸ĵ�ʱ��
//        if (info.contains(head)) {
//          String info1;
//          do {
//            System.out.println("�ҵ���ͬ���ˣ�" + info);
//            info1 = "";
//            if(m!='\r' && m != '\n')
//              info1 += (char) m;
//            while (true) { 
//              m = file_D.read();
//              if (m == -1)
//                break;
//              if (m == '\r') {
//                file_D.read(); 
//                break;
//              }
//              if (m == '\n') { 
//                break; 
//              }
//              info1 += (char) m;
//            }
//            System.out.println("info1�ǣ�"+info1);
//            if (info1.contains("Last-Modified:")) {
//              resul = info1.substring(16); 
//            } 
//            cacheInfo.add(info1);
//            if(info1.equals("")){ 
//              System.out.print("���ǿ�");
//              return resul;
//            } 
//          } while (!info1.equals("") && info1 != null && m != -1);
//        }
//        info = "";
//        while (true) { 
//          if (m == -1)
//            break;
//          if (m == '\r') {
//            file_D.read();
//            break;
//          }
//          if (m == '\n')
//            break;
//          info += (char) m;
//          m = file_D.read();
//        }
//      }
//    } catch (FileNotFoundException e) { 
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
// 
//    return resul;
    return null;
  }
 
}
