#include <stdio.h>
#include <iostream>
#include <Windows.h>
#include <winsock.h>
#include <process.h>
#include <string.h>
#include <cstring>
#include <tchar.h>
#include <cstdlib>
#include<vector>
#pragma comment(lib,"Ws2_32.lib")

using namespace std;

#define MAXSIZE 65507 //发送数据报文的最大长度
#define HTTP_PORT 80 //http 服务器端口

//钓鱼网站引导表：将用户对前一个网站的访问引导至后一个网站
vector<vector<string>> Phishing_table = { { "http://seie.hit.edu.cn/", "http://cs.hit.edu.cn" } };

//禁止访问的网站表
vector<string>disaccess_website = { "yzb.hit.edu.cn" };

//禁止访问网站的用户表
vector<string>disaccess_usertable = { "127.0.0.0" };

//Http 重要头部数据
struct HttpHeader {
	char method[4]; // POST 或者 GET，注意有些为 CONNECT，本实验暂不考虑
	char url[1024]; // 请求的 url
	char host[1024]; // 目标主机
	char cookie[1024 * 10]; //cookie
	HttpHeader() {
		ZeroMemory(this, sizeof(HttpHeader));
	}
};
//缓存的网页
struct HttpCache {
	char url[1024]; // 请求的 url
	char host[1024]; // 目标主机
	string last_modified; //记录上次的修改时间戳
	string status; //状态字
	char buffer[MAXSIZE]; //数据
	HttpCache() {
		ZeroMemory(this, sizeof(HttpCache));
	}
};
//保存缓存网页的数据结构
vector<HttpCache>cache;

BOOL InitSocket();//初始化socket
int ParseHttpHead(char* buffer, HttpHeader* httpHeader, char sendBuffer[]);//解析http头部
BOOL ConnectToServer(SOCKET* serverSocket, char* host);//连接到服务器
//LPVOID是一个没有类型的指针，也就是说你可以将任意类型的指针赋值给LPVOID类型的变量（一般作为参数传递），然后在使用的时候在转换回来
unsigned int __stdcall ProxyThread(LPVOID lpParameter);//代理服务器线程
BOOL Unable_access_website(HttpHeader* httpHeader);//判断网站是否禁止访问
int Phishing_website(HttpHeader* httpHeader);//判断该网站是否为钓鱼网站
BOOL Unable_access_user(SOCKADDR_IN addr_conn);//判断该用户是否能够访问
int isCache(HttpHeader* httpHeader);
//代理相关参数
SOCKET ProxyServer;
sockaddr_in ProxyServerAddr;
const int ProxyPort = 12345;//代理服务器的端口

struct ProxyParam {
	SOCKET clientSocket;
	SOCKET serverSocket;
};
//_tmain()不过是unicode版本的的main()._TCHAR表示各个参数，字符串数组的每个单元是char*类型的，指向一个c风格字符串
//_TCHAR类型是宽字符型字符串，和我们一般常用的字符串不同，它是32位或者更 高的操作系统中所使用的类型.
int _tmain(int argc, _TCHAR* argv[])
{
	printf("代理服务器正在启动\n");
	printf("初始化...\n");
	if (!InitSocket()) {
		printf("socket 初始化失败\n");
		return -1;
	}
	printf("代理服务器正在运行，监听端口 %d\n", ProxyPort);
	SOCKET acceptSocket = INVALID_SOCKET;//先将接收socket置为无效
	ProxyParam* lpProxyParam;
	HANDLE hThread;
	DWORD dwThreadID;

	SOCKADDR_IN addr_conn;
	int nSize = sizeof(addr_conn);
	//通过memset函数初始化内存块
	memset((void*)&addr_conn, 0, sizeof(addr_conn));

	//代理服务器不断监听
	while (true) {
		//返回一个套接字来和客户端通信
		acceptSocket = accept(ProxyServer, NULL, NULL);
		getpeername(acceptSocket, (SOCKADDR*)&addr_conn, &nSize); //获取与addr_conn套接字关联的远程协议地址
		if (Unable_access_user(addr_conn))
		{
			printf("该用户禁止访问外部网站");
			closesocket(acceptSocket);
			continue;
		}
		lpProxyParam = new ProxyParam;
		if (lpProxyParam == NULL) {
			continue;
		}
		lpProxyParam->clientSocket = acceptSocket;
		hThread = (HANDLE)_beginthreadex(NULL, 0, &ProxyThread, (LPVOID)lpProxyParam, 0, 0);
		CloseHandle(hThread);
		Sleep(200);
	}
	closesocket(ProxyServer);
	WSACleanup();
	return 0;
}

//************************************
// Method: InitSocket
// FullName: InitSocket
// Access: public
// Returns: BOOL
// Qualifier: 初始化套接字
//************************************
BOOL InitSocket() {
	//加载套接字库（必须）
	WORD wVersionRequested;
	WSADATA wsaData;
	//套接字加载时错误提示
	int err;
	//版本 2.2
	wVersionRequested = MAKEWORD(2, 2);
	//加载 dll 文件 Scoket 库
	err = WSAStartup(wVersionRequested, &wsaData);
	if (err != 0) {
		//找不到 winsock.dll
		printf("加载 winsock 失败，错误代码为: %d\n", WSAGetLastError());
		return FALSE;
	}
	if (LOBYTE(wsaData.wVersion) != 2 || HIBYTE(wsaData.wVersion) != 2)
	{
		printf("不能找到正确的 winsock 版本\n");
		WSACleanup();
		return FALSE;
	}
	//创建套接字,SOCK_STREAM面向连接的服务，IPPRPTP_TCP:TCP连接
//SOCK_DGRAM无连接的服务，IPPRPTP_UDP:UDP连接
	ProxyServer = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (INVALID_SOCKET == ProxyServer) {
		printf("创建套接字失败，错误代码为：%d\n", WSAGetLastError());
		return FALSE;
	}
	ProxyServerAddr.sin_family = AF_INET;//使用IPV4地址
	ProxyServerAddr.sin_port = htons(ProxyPort);//绑定端口号
	ProxyServerAddr.sin_addr.S_un.S_addr = INADDR_ANY;
	if (bind(ProxyServer, (SOCKADDR*)&ProxyServerAddr, sizeof(SOCKADDR)) == SOCKET_ERROR) {
		printf("绑定套接字失败\n");
		return FALSE;
	}
	if (listen(ProxyServer, SOMAXCONN) == SOCKET_ERROR) {
		printf("监听端口%d 失败", ProxyPort);
		return FALSE;
	}
	return TRUE;
}

//************************************
// Method: ProxyThread
// FullName: ProxyThread
// Access: public
// Returns: unsigned int __stdcall
// Qualifier: 线程执行函数
// Parameter: LPVOID lpParameter
//************************************
unsigned int __stdcall ProxyThread(LPVOID lpParameter) {
	char Buffer[MAXSIZE];
	ZeroMemory(Buffer, MAXSIZE);
	int i;
	char* CacheBuffer;
	SOCKADDR_IN clientAddr;
	int length = sizeof(SOCKADDR_IN);
	int recvSize;
	int ret;
	HttpHeader* httpHeader = new HttpHeader();
	//接收客户端的请求
	recvSize = recv(((ProxyParam*)lpParameter)->clientSocket, Buffer, MAXSIZE, 0);

	printf("请求内容为：\n");
	printf(Buffer);
	CacheBuffer = new char[recvSize + 1];
	ZeroMemory(CacheBuffer, recvSize + 1);
	memcpy(CacheBuffer, Buffer, recvSize);
	ParseHttpHead(CacheBuffer, httpHeader, Buffer); //对请求报文的头部文件进行解析
	delete CacheBuffer;
	if (!ConnectToServer(&((ProxyParam*)lpParameter)->serverSocket, httpHeader->host)) {  //connect连接至目标服务器
		goto error;
	}
	printf("代理连接主机 %s成功\n", httpHeader->host);

	if (!strcmp(httpHeader->method, "GET"))
	{
		//将客户端发送的请求报文的url和本地缓存的url进行对比，
		//若相等，则缓存命中，然后构造If-modified-since: 头，并将last_modified写入报文
		string str = string(httpHeader->url);
		for ( i = 0; i < cache.size(); ++i)
		{
			if (cache[i].url == str)
			{
			printf("\ncache命中\n");
			int len = cache[i].last_modified.size();
			recvSize -= 2;
			memcpy(Buffer+recvSize,"If-modified-since: ",19);
			recvSize += 19;
			memcpy(Buffer+recvSize,cache[i].last_modified.c_str(), cache[i].last_modified.size());
			recvSize += len;
			Buffer[recvSize++] = '\r';
			Buffer[recvSize++] = '\n';
			Buffer[recvSize++] = '\r';
			Buffer[recvSize++] = '\n';
			break;
			}
		}
	}
	//将报文直接转发给目标服务器
	ret = send(((ProxyParam*)lpParameter)->serverSocket, Buffer, strlen(Buffer) + 1, 0);
	//等待目标服务器返回数据
	recvSize = recv(((ProxyParam*)lpParameter)->serverSocket, Buffer, MAXSIZE, 0);
	if (recvSize <= 0) {
		goto error;
	}
	printf("\n代理服务器从目标服务器收到的信息\n");
	printf("%s", Buffer);
	//判断目的服务器的发送的报文信息状态字是304还是200
	//若为304将本地的缓存发送给客户端
	if (!memcmp(&Buffer[9], "304", 3)) {
		ret = send(((ProxyParam*)lpParameter)->clientSocket, cache[i].buffer, sizeof(cache[i].buffer), 0);
		if (ret != SOCKET_ERROR) {
			printf("\n\n****************************\n");
			printf("从本地读入缓存，并返回成功");
			printf("\n****************************\n\n");
		}
	}
	//若为200更新本地的缓存，将目的服务器的新报文发送给客户端
	else
	{
		if (!strcmp(httpHeader->method, "GET") && !memcmp(&Buffer[9], "200", 3))
		{
			char Buffer2[MAXSIZE];
			memcpy(Buffer2, Buffer, sizeof(Buffer));
			const char* delim = "\r\n";
			char* ptr;
			char* p = strtok_s(Buffer2, delim, &ptr);
			bool flag = false;
			//更新时间戳
			while (p) {
				if (strlen(p) >= 15 && !memcmp(p, "Last-Modified: ", 15)) {
					flag = true;
					break;
				}
				p = strtok_s(NULL, delim, &ptr);
			}
			//添加缓存
			if (flag) {

				HttpCache tempcache;
				memcpy(tempcache.url, httpHeader->url, sizeof(httpHeader->url));
				tempcache.last_modified = p + 15;
				memcpy(tempcache.buffer, Buffer, sizeof(Buffer));
				cache.push_back(tempcache);
			}
		}
		printf("\n\n***************************************************************************\n");
		printf("本地缓存过时或者第一次访问该页面，从目的服务器发送客户端，并更新本地缓存成功");
		printf("\n***************************************************************************\n\n");
		//将目标服务器返回的数据直接转发给客户端
		ret = send(((ProxyParam*)lpParameter)->clientSocket, Buffer, sizeof(Buffer), 0);
	}

	//错误处理
error:
	printf("关闭套接字\n");
	Sleep(200);
	closesocket(((ProxyParam*)lpParameter)->clientSocket);
	closesocket(((ProxyParam*)lpParameter)->serverSocket);
	delete lpParameter;
	_endthreadex(0);
	return 0;
}

//************************************
// Method: ParseHttpHead
// FullName: ParseHttpHead
// Access: public
// Returns: void
// Qualifier: 解析TCP报文中的HTTP头部
// Parameter: char * buffer
// Parameter: HttpHeader * httpHeader
//************************************
int ParseHttpHead(char* buffer, HttpHeader* httpHeader, char sendBuffer[]) {
	char* p;
	char* ptr;
	const char* delim = "\r\n"; //回车换行符
	int flag = -1; //-1表示没有缓存，其余数字表示在缓存数组里的位置下标
	p = strtok_s(buffer, delim, &ptr);  //提取第一行
	//printf("%s\n", p);
	if (p[0] == 'G') {	//GET方式
		memcpy(httpHeader->method, "GET", 3);
		memcpy(httpHeader->url, &p[4], strlen(p) - 13);
		printf("url：%s\n", httpHeader->url);
	}
	else if (p[0] == 'P') {	//POST方式
		memcpy(httpHeader->method, "POST", 4);
		memcpy(httpHeader->url, &p[5], strlen(p) - 14);
	}

	p = strtok_s(NULL, delim, &ptr);
	while (p) {
		switch (p[0]) {
		case 'H'://HOST
			memcpy(httpHeader->host, &p[6], strlen(p) - 6);
			break;
		case 'C'://Cookie
			if (strlen(p) > 8) {
				char header[8];
				ZeroMemory(header, sizeof(header));
				memcpy(header, p, 6);
				if (!strcmp(header, "Cookie")) {
					memcpy(httpHeader->cookie, &p[8], strlen(p) - 8);
				}
			}
			break;
		default:
			break;
		}
		p = strtok_s(NULL, delim, &ptr);
	}
	//如果httpHeader的host属于禁止访问的网站表
	if (Unable_access_website(httpHeader))
	{
		printf("\n根据xxx,该网站 %s 已被禁止访问 \n", httpHeader->host);
		memset(httpHeader->host, 0, sizeof(httpHeader->host)); //把需要访问的host全改为0
	}
	//如果httpHeader的host属于钓鱼网站引导表
	if (Phishing_website(httpHeader) != -1)
	{
		int i = Phishing_website(httpHeader);
		string target = Phishing_table[i][1];
		target = target.substr(7, target.size() - 1);
		printf("\n%s是钓鱼网站，您即将被钓到其他网站\n", httpHeader->host);
		string str = string(sendBuffer);
		while (str.find(string(httpHeader->host)) != string::npos)  //将发送缓存区的host全部替换为钓鱼网站的host
		{
			str.replace(str.find(string(httpHeader->host)), string(httpHeader->host).size(), target);
		}
		memcpy(sendBuffer, str.c_str(), str.size() + 1); //用新的网站地址替换原buffer_c
		memcpy(httpHeader->host, target.c_str(), target.length() + 1);
	}
	return flag;
}

//************************************
// Method: ConnectToServer
// FullName: ConnectToServer
// Access: public
// Returns: BOOL
// Qualifier: 根据主机创建目标服务器套接字，并连接
// Parameter: SOCKET * serverSocket
// Parameter: char * host
//************************************
BOOL ConnectToServer(SOCKET* serverSocket, char* host) {
	sockaddr_in serverAddr;
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons(HTTP_PORT);
	HOSTENT* hostent = gethostbyname(host);
	if (!hostent) {
		return FALSE;
	}
	//printf(host);
	in_addr Inaddr = *((in_addr*)*hostent->h_addr_list);
	serverAddr.sin_addr.s_addr = inet_addr(inet_ntoa(Inaddr));
	*serverSocket = socket(AF_INET, SOCK_STREAM, 0);
	if (*serverSocket == INVALID_SOCKET) {
		return FALSE;
	}
	if (connect(*serverSocket, (SOCKADDR*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
		closesocket(*serverSocket);
		return FALSE;
	}
	return TRUE;
}
//************************************
// Method: 网站过滤
// FullName: Unable_access_website
// Access: public
// Returns: BOOL
// Qualifier: 根据域名是否在禁止名单中
// Parameter: HttpHeader *httpHeader
//************************************
BOOL Unable_access_website(HttpHeader* httpHeader)
{
	string str = httpHeader->host;
	if (find(disaccess_website.begin(), disaccess_website.end(), str) != disaccess_website.end())
	{
		// Element in vector.
		return TRUE;
	}
	else
	{
		return FALSE;
	}
}
//************************************
// Method: 钓鱼网站
// FullName:Phishing_website
// Access: public
// Returns: BOOL
// Qualifier: 根据解析出来的http的url是否在
// 钓鱼网站的列表中
// Parameter: HttpHeader *httpHeader
//************************************
int Phishing_website(HttpHeader* httpHeader)
{
	string str = httpHeader->url;
	int n = Phishing_table.size();
	for (int i = 0; i < n; ++i)
	{
		if (str == Phishing_table[i][0])
		{
			// Element in vector.
			return i;
		}
	}
	return -1;
}
//************************************
// Method: 用户过滤
// FullName: Unable_access_user
// Access: public
// Returns: BOOL
// Qualifier: 根据域名是否在禁止名单中
// Parameter: SOCKADDR_IN addr_conn
//************************************
BOOL Unable_access_user(SOCKADDR_IN addr_conn)
{
	char* p = inet_ntoa(addr_conn.sin_addr);
	string str = string(p);
	if (find(disaccess_usertable.begin(), disaccess_usertable.end(), str) != disaccess_usertable.end())
	{
		// Element in vector.
		return TRUE;
	}
	else
	{
		return FALSE;
	}
}


