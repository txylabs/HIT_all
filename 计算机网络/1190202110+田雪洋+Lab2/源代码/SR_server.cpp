#include <stdio.h>
#include <winsock2.h>
#include<time.h>
#include <fstream>
#include<string>
#include<vector>
#include <iostream>
#pragma comment (lib, "ws2_32.lib")  //加载 ws2_32.dll
#pragma warning(disable:4996)
using namespace std;
#define CMD_SIZE 1000
#define BUFFER_LENGTH 20 //缓冲区大小，（以太网中 UDP 的数据帧中包长度应小于 1480 字节）
#define SEND_WIND_SIZE 8//发送窗口大小为 8，GBN 中应满足 W + 1 <= N（W 为发送窗口大小，N 为序列号个数）

//如果将窗口大小设为 1，则为停-等协议
const int SEQ_SIZE = 16; //序列号的个数，从 0~15 共计 16 个
//由于发送数据第一个字节如果值为 0，则数据会发送失败
//因此接收端序列号为 1~16，与发送端一一对应
int ack[SEQ_SIZE];//收到 ack 情况，对应 0~15 的 ，0表示未确认，-1表示未使用，1表示重传
int nextseqnum;//当前数据包的 seq
int base;//当前等待确认的 ack
int totalSeq;//收到的确认的总数
SOCKET sockServer;
SOCKADDR_IN addrServer; //服务器地址
int totalPacket;//需要发送的包总数
int clocktimer[SEQ_SIZE];//收到 ack 情况，对应 0~15 的 ack,

//************************************
// Method: seqIsAvailable
// FullName: seqIsAvailable
// Access: public
// Returns: bool
// Qualifier: 当前序列号 nextseqnum 是否可用
//************************************
bool seqIsAvailable() {
    int step;
    step = nextseqnum - base;
    step = step >= 0 ? step : step + SEQ_SIZE;
    //序列号是否在当前发送窗口之内
    if (step >= SEND_WIND_SIZE) {
        return false;
    }
    //序列号未被使用或重传
    if (ack[nextseqnum] == -1 || ack[nextseqnum] == 1) {
        return true;
    }
    return false;
}

//************************************
// Method: ackHandler
// FullName: ackHandler
// Access: public
// Returns: void
// Qualifier: 收到 ack，累积确认，取数据帧的第一个字节
//由于发送数据时，第一个字节（序列号）为 0（ASCII）时发送失败，因此加一了，此处需要减一还原
// Parameter: char c
//************************************
void ackHandler(char c) {
    unsigned char index = (unsigned char)c - 1; //序列号减一
    printf("收到的ack是: %d\n", index);
    //若base收到确认，base++
    if (base == index)
    {
        ack[base] = -1;
        base = (base + 1) % SEQ_SIZE;
    }
    else
    {
        ack[index] = -1;
    }
}

//************************************
// Method: getCurTime
// FullName: getCurTime
// Access: public
// Returns: void
// Qualifier: 获取当前系统时间，结果存入 ptime 中
// Parameter: char * curtime
//************************************
void getCurTime(string& curtime) {
    char buffer[128];
    memset(buffer, 0, sizeof(buffer));
    time_t c_time;
    struct tm* p;
    time(&c_time);
    p = localtime(&c_time);
    //将数据格式化，输出到字符串
    sprintf_s(buffer, "%d/%d/%d %d:%d:%d",
        p->tm_year + 1900,
        p->tm_mon,
        p->tm_mday,
        p->tm_hour,
        p->tm_min,
        p->tm_sec);
    buffer[strlen(buffer)] = 0;
    curtime = string(buffer);
}

int main() {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    //创建套接字
    SOCKET sock = socket(AF_INET, SOCK_DGRAM, 0);
    //绑定套接字
    sockaddr_in servAddr;
    //no block
    int iMode = 1;
    ioctlsocket(sock, FIONBIO, (u_long FAR*) & iMode);
    memset(&servAddr, 0, sizeof(servAddr));  //每个字节都用0填充
    servAddr.sin_family = PF_INET;  //使用IPv4地址
    servAddr.sin_addr.s_addr = htonl(INADDR_ANY); //自动获取IP地址
    servAddr.sin_port = htons(1234);  //端口
    bind(sock, (SOCKADDR*)&servAddr, sizeof(SOCKADDR));
    printf("socket初始化成功\n");
    //接收客户端请求
    SOCKADDR clntAddr;  //客户端地址信息
    int nSize = sizeof(SOCKADDR);
    while (1) {
        char cmd[CMD_SIZE] = { 0 };  //缓冲区
        int strLen = recvfrom(sock, cmd, CMD_SIZE, 0, &clntAddr, &nSize);
        if (strLen != -1)cmd[strLen] = 0;
        if (strstr(cmd, "-time") != NULL)
        {
            string curtime;
            getCurTime(curtime);
            sendto(sock, curtime.c_str(), curtime.size(), 0, &clntAddr, nSize);

        }
        else if (strstr(cmd, "-quit") != NULL)
        {
            string curtime = "Good bye!";
            sendto(sock, curtime.c_str(), curtime.size(), 0, &clntAddr, nSize);
        }
        else if (strstr(cmd, "-receive") != NULL)
        {
            //no block
            iMode = 0;
            ioctlsocket(sock, FIONBIO, (u_long FAR*) & iMode);
            unsigned short waitSeq = 1;//等待期望接受的Seq的最小值
            unsigned short seq;//实际收到的Seq
            unsigned short recvSeq = 0;//已确认的序列号的最大值
            vector<string>ackcache(SEQ_SIZE + 1);
            string cache;//收到的临时缓存
            string receivecache;//最终缓存
            bool ack_send[SEQ_SIZE + 1];//ack发送情况的记录，对应1-20的ack,刚开始全为false
            for (int i = 0; i <= SEQ_SIZE; ++i)
            {
                ack_send[i] = false;
            }
            cout << "正在从客户端接收数据...\n" << endl;
            while (1)
            {
                char buffer[1024] = { 0 };
                int a = recvfrom(sock, buffer, BUFFER_LENGTH + 1, 0, &clntAddr, &nSize);
                if (strstr(buffer, "Finished!") != NULL)
                {
                    printf("文件传输完毕\n");
                    break;
                }
                seq = (unsigned short)buffer[0];
                buffer[a] = 0;
                cache = string(buffer);

                //随机法模拟包是否丢失
                printf("收到包的seq是 %d\n", seq);
                //若收到的ack就是期望的ack
                if (seq == waitSeq)
                {
                    //把相应的ack_send改为true，表示可以上交
                    ack_send[waitSeq] = true;
                    cache.erase(0, 1);
                    ackcache[waitSeq] = cache;
                    //把从当前位置开始连续的可以上交的包上交
                    while (ack_send[waitSeq] == true)
                    {
                        receivecache.append(ackcache[waitSeq]);
                        ackcache[waitSeq] = "";
                        ack_send[waitSeq] = false;//重新置为不可上交的状态
                        waitSeq++;
                        if (waitSeq == 17)
                        {
                            waitSeq = 1;
                        }
                    }
                    buffer[0] = seq;
                    recvSeq = seq;
                    buffer[1] = '\0';
                }
                else
                {
                    int step = seq - waitSeq > 0 ? seq - waitSeq : seq - waitSeq + SEQ_SIZE;
                    if (step > SEND_WIND_SIZE)
                    {
                        buffer[0] = seq;
                        recvSeq = seq;
                        buffer[1] = '\0';
                    }
                    else
                    {
                        //把相应的ack_send改为true，表示可以上交
                        ack_send[seq] = true;
                        cache.erase(0, 1);
                        ackcache[waitSeq] = cache;
                        buffer[0] = seq;
                        recvSeq = seq;
                        buffer[1] = '\0';
                    }
                }
                sendto(sock, buffer, 2, 0, &clntAddr, nSize);
                printf("发送的ack为 %d\n", (unsigned char)buffer[0]);
                Sleep(500);
            }
            receivecache.erase(receivecache.size() - 8, receivecache.size() - 1);
            ofstream fileout;
            fileout.open("server_receive.txt");
            fileout << receivecache;
            fileout.close();
            //no block
            iMode = 1;
            ioctlsocket(sock, FIONBIO, (u_long FAR*) & iMode);
        }
        else if (strstr(cmd, "-testsr") != NULL)
        {
            char sendBuffer[BUFFER_LENGTH * 30];
            std::ifstream file;
            file.open("server_send.txt");
            file.seekg(0, ios::end);
            int fileSize = (int)file.tellg();
            file.seekg(0, ios::beg);
            file.read(sendBuffer, fileSize);
            totalPacket = sizeof(sendBuffer) / BUFFER_LENGTH;
            printf("发送文件已加载完成，文件包含%d个字节，开始发送\n", fileSize);
            int sendSum = fileSize / 20 + (fileSize % 20 != 0);
            int timeout = 0;
            file.close();
            base = 0;
            nextseqnum = 0;
            totalSeq = 0;
            char buffer[BUFFER_LENGTH]; //数据发送接收缓冲区
            ZeroMemory(buffer, sizeof(buffer));
            //初始化发送窗口
            for (int i = 0; i < SEQ_SIZE; ++i)
            {
                ack[i] = -1;
                clocktimer[i] = 0;
            }
            while (totalSeq < sendSum)
            {
                if (seqIsAvailable()) {
                    //发送给客户端的序列号从 1 开始
                    buffer[0] = nextseqnum + 1;
                    ack[nextseqnum] = 0;
                    printf("发送的seq为: %d\n", nextseqnum);
                    if (BUFFER_LENGTH * totalSeq >= 600)break;
                    memcpy(&buffer[1], sendBuffer + BUFFER_LENGTH * totalSeq, BUFFER_LENGTH);
                    sendto(sock, buffer, BUFFER_LENGTH + 1, 0, &clntAddr, nSize);
                    ++nextseqnum;
                    nextseqnum %= SEQ_SIZE;
                    ++totalSeq;
                    Sleep(500);
                }
                //等待 Ack，若没有收到，则返回值为-1，计数器+1
                int recvSize = recvfrom(sock, buffer, BUFFER_LENGTH, 0, &clntAddr, &nSize);
                if (recvSize < 0) {
                    int count = 0;
                    for (int i = 0; i < SEND_WIND_SIZE; ++i)
                    {
                        if (ack[(i+base)%SEQ_SIZE] == 0)
                        {
                            clocktimer[(i + base) % SEQ_SIZE] += 1;
                            if (clocktimer[(i + base) % SEQ_SIZE] > 20)
                            {
                                cout << "超时错误！\n";
                                ack[(i + base) % SEQ_SIZE] = 1;
                                totalSeq--;
                                clocktimer[(i + base) % SEQ_SIZE] = 0;
                            }
                        }
                    }
                    nextseqnum = base;
                }
                else {
                    ackHandler(buffer[0]);
                    clocktimer[buffer[0] - 1] = 0;
                }
            }
            Sleep(500);
            string curtime = "Finished!";
            sendto(sock, curtime.c_str(), curtime.size(), 0, &clntAddr, nSize);
            cmd[0] = 0;
        }
    }
    closesocket(sock);
    WSACleanup();
    return 0;
}