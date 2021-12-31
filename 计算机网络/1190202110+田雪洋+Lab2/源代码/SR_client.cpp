#include <stdio.h>
#include <WinSock2.h>
#include<string>
#include<iostream>
#include<fstream>
#include<vector>
using namespace std;
#pragma comment(lib, "ws2_32.lib")  //加载 ws2_32.dll
#define BUFFER_LENGTH 20 //缓冲区大小，（以太网中 UDP 的数据帧中包长度应小于 1480 字节）
#define SEND_WIND_SIZE 8//发送窗口大小为 8，GBN 中应满足 W + 1 <= N（W 为发送窗口大小，N 为序列号个数）
#define BUF_SIZE 100
const int SEQ_SIZE = 16; //序列号的个数，从 0~15 共计 16 个
BOOL ack[SEQ_SIZE];//收到 ack 情况，对应 0~15 的 ack
int nextseqnum;//当前数据包的 seq
int base;//当前等待确认的 ack
int totalSeq;//收到的确认的总数
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

void printTips() {
    printf("*********************************************\n");
    printf("| -time to get current time |\n");
    printf("| -quit to exit client |\n");
    printf("| -testsr [X] [Y] to test the gbn |\n");
    printf("| -receive to test two-way transmission |\n");
    printf("*********************************************\n");
}
//************************************
// Method: lossInLossRatio
// FullName: lossInLossRatio
// Access: public
// Returns: BOOL
// Qualifier: 根据丢失率随机生成一个数字，判断是否丢失,丢失则返回TRUE，否则返回 FALSE
// Parameter: float lossRatio [0,1]
//************************************
BOOL lossInLossRatio(float lossRatio) {
    int lossBound = (int)(lossRatio * 100);
    int r = rand() % 101;
    if (r <= lossBound) {
        return TRUE;
    }
    return FALSE;
}
int main() {

    //初始化DLL
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    //创建套接字
    SOCKET sock = socket(PF_INET, SOCK_DGRAM, 0);
    //服务器地址信息
    sockaddr_in servAddr;
    memset(&servAddr, 0, sizeof(servAddr));  //每个字节都用0填充
    servAddr.sin_family = PF_INET;
    servAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    servAddr.sin_port = htons(1234);
    //不断获取用户输入并发送给服务器，然后接受服务器数据
    sockaddr fromAddr;
    int addrLen = sizeof(fromAddr);
    char sendBuffer[BUFFER_LENGTH * 30];
    std::ifstream file;
    file.open("client_send.txt");
    file.seekg(0, ios::end);
    int fileSize = (int)file.tellg();
    file.seekg(0, ios::beg);
    file.read(sendBuffer, fileSize);
    file.close();
    while (1)
    {
        string cmd;
        printTips();
        printf("请输入你的选择\n");
        cin >> cmd;
        if (strstr(cmd.c_str(), "-receive") != NULL)
        {
            //no block
            int iMode = 1;
            ioctlsocket(sock, FIONBIO, (u_long FAR*) & iMode);
            sendto(sock, cmd.c_str(), cmd.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
            char sendBuffer[BUFFER_LENGTH * 30];
            std::ifstream file;
            file.open("client_send.txt");
            file.seekg(0, ios::end);
            int fileSize = (int)file.tellg();
            file.seekg(0, ios::beg);
            file.read(sendBuffer, fileSize);
            totalPacket = sizeof(sendBuffer) / BUFFER_LENGTH;
            printf("正在向服务器发送数据\n,发送文件已加载完成，文件包含%d个字节，开始发送\n", fileSize);
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
                    sendto(sock, buffer, BUFFER_LENGTH + 1, 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
                    ++nextseqnum;
                    nextseqnum %= SEQ_SIZE;
                    ++totalSeq;
                    Sleep(500);
                }
                //等待 Ack，若没有收到，则返回值为-1，计数器+1
                int recvSize = recvfrom(sock, buffer, BUFFER_LENGTH, 0, &fromAddr, &addrLen);
                if (recvSize < 0) {
                    int count = 0;
                    for (int i = 0; i < SEND_WIND_SIZE; ++i)
                    {
                        if (ack[(i + base) % SEQ_SIZE] == 0)
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
            sendto(sock, curtime.c_str(), curtime.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
            cmd[0] = 0;
            //no block
            iMode = 0;
            ioctlsocket(sock, FIONBIO, (u_long FAR*) & iMode);
        }
        else
        {
            if (strstr(cmd.c_str(), "-time") != NULL)
            {
                char buffer[BUF_SIZE] = { 0 };
                sendto(sock, cmd.c_str(), cmd.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
                int strLen = recvfrom(sock, buffer, BUF_SIZE, 0, &fromAddr, &addrLen);
                if (strLen != -1) buffer[strLen] = 0;
                printf("Message form server: %s\n", buffer);
                Sleep(1000);
            }
            else if (strstr(cmd.c_str(), "-quit") != NULL)
            {
                char buffer[BUF_SIZE] = { 0 };
                sendto(sock, cmd.c_str(), cmd.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
                int strLen = recvfrom(sock, buffer, BUF_SIZE, 0, &fromAddr, &addrLen);
                if (strLen != -1)buffer[strLen] = 0;
                printf("Message form server: %s\n", buffer);
                closesocket(sock);
                WSACleanup();
                break;
            }
            else if (strstr(cmd.c_str(), "-testsr") != NULL)
            {
                printf("请输入丢包率，ACK丢失率：\n");
                float packetLossRatio = 0.2; //默认包丢失率 0.2
                float ackLossRatio = 0.2; //默认 ACK 丢失率 0.2
                cin >> packetLossRatio;
                cin >> ackLossRatio;
                printf("开始进行SR测试...\n");
                printf("包丢失率:%f, ACK 丢失率:%f\n", packetLossRatio, ackLossRatio);

                unsigned short waitSeq = 1;//等待期望接受的Seq的最小值
                unsigned short seq;//实际收到的Seq
                unsigned short recvSeq = 0;//已确认的序列号的最大值
                vector<string>ackcache(SEQ_SIZE +1);
                string cache;//收到的临时缓存
                string receivecache;//最终缓存
                bool ack_send[SEQ_SIZE+1];//ack发送情况的记录，对应1-20的ack,刚开始全为false
                for (int i = 0; i <= SEQ_SIZE; ++i)
                {
                    ack_send[i] = false;
                }
                sendto(sock, cmd.c_str(), cmd.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
                while (1)
                {
                    char buffer[1024] = { 0 };
                    int a = recvfrom(sock, buffer, BUFFER_LENGTH + 1, 0, &fromAddr, &addrLen);
                    if (strstr(buffer, "Finished!") != NULL)
                    {
                        printf("文件传输完毕\n");
                        break;
                    }
                    seq = (unsigned short)buffer[0];
                    buffer[a] = 0;
                    cache = string(buffer);

                    //随机法模拟包是否丢失
                    BOOL b = lossInLossRatio(packetLossRatio);
                    if (b) {
                        printf("seq为 %d 的包丢失\n", seq);
                        continue;
                    }
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
                    b = lossInLossRatio(ackLossRatio);
                    if (b) {
                        printf("丢失的ACK为: %d \n", (unsigned char)buffer[0]);
                        continue;
                    }
                    sendto(sock, buffer, 2, 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
                    printf("发送的ack为 %d\n", (unsigned char)buffer[0]);
                    Sleep(500);
                }
                receivecache.erase(receivecache.size() - 8, receivecache.size() - 1);
                ofstream fileout;
                fileout.open("client_receive.txt");
                fileout << receivecache;
                fileout.close();
            }
        }
        system("pause");
    }
    closesocket(sock);
    WSACleanup();
    return 0;
}
