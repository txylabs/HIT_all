#include <stdio.h>
#include <WinSock2.h>
#include<string>
#include<iostream>
#include<fstream>
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
    //序列号未被确认
    if (ack[nextseqnum]) {
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
    if (base <= index) {
        for (int i = base; i <= index; ++i) {
            ack[i] = TRUE;
        }
        base = (index + 1) % SEQ_SIZE;
    }
    else {
        //ack 超过了最大值，回到了 base 的左边
        for (int i = base; i < SEQ_SIZE; ++i) {
            ack[i] = TRUE;
        }
        for (int i = 0; i <= index; ++i) {
            ack[i] = TRUE;
        }
        base = index + 1;
    }
}
void printTips() {
    printf("*********************************************\n");
    printf("| -time to get current time |\n");
    printf("| -quit to exit client |\n");
    printf("| -testgbn [X] [Y] to test the gbn |\n");
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
            Sleep(100);
            char sendBuffer[BUFFER_LENGTH * 30];
            std::ifstream file;
            file.open("client_send.txt");
            file.seekg(0, ios::end);
            int fileSize = (int)file.tellg();
            file.seekg(0, ios::beg);
            file.read(sendBuffer, fileSize);
            totalPacket = sizeof(sendBuffer) / BUFFER_LENGTH;
            printf("发送文件已加载完成，文件包含%d个字节，开始向服务器发送\n", fileSize);
            int sendSum = fileSize / 20 + (fileSize % 20 != 0);
            file.close();
            base = 0;
            nextseqnum = 0;
            totalSeq = 0;
            int timecount = 0;
            int file_send_number = 0;
            char buffer[BUFFER_LENGTH]; //数据发送接收缓冲区
            ZeroMemory(buffer, sizeof(buffer));
            //初始化发送窗口
            for (int i = 0; i < SEND_WIND_SIZE; ++i)
            {
                ack[i] = TRUE;
            }
            while (totalSeq < sendSum)
            {
                if (seqIsAvailable()) {
                    //发送给客户端的序列号从 1 开始
                    buffer[0] = nextseqnum + 1;
                    ack[nextseqnum] = FALSE;
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
                    timecount++;
                    //20 次等待 ack 则超时重传
                    if (timecount > 20)
                    {
                        printf("超时事件发生.\n");
                        int index;
                        //从发生超时的开始，所以ack全部变为可用
                        for (int i = 0; i < SEND_WIND_SIZE; ++i) {
                            index = (i + base) % SEQ_SIZE;
                            ack[index] = TRUE;
                        }
                        //总确认数减少
                        totalSeq -= SEND_WIND_SIZE;
                        nextseqnum = base;
                        timecount = 0;
                    }
                }
                else {
                    ackHandler(buffer[0]);
                    timecount = 0;
                }
            }
            Sleep(500);
            string curtime = "Finished!";
            sendto(sock, curtime.c_str(), curtime.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
            printf("已成功向服务器上传文件，请重启客户端\n");
            system("pause");
            return 0;
        }
        else
        {
            if (strstr(cmd.c_str(), "-time") != NULL)
            {
                char buffer[BUF_SIZE] = { 0 };
                sendto(sock, cmd.c_str(), cmd.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
                int strLen = recvfrom(sock, buffer, BUF_SIZE, 0, &fromAddr, &addrLen);
                buffer[strLen] = 0;
                printf("Message form server: %s\n", buffer);
                Sleep(1000);
            }
            else if (strstr(cmd.c_str(), "-quit") != NULL)
            {
                char buffer[BUF_SIZE] = { 0 };
                sendto(sock, cmd.c_str(), cmd.size(), 0, (struct sockaddr*)&servAddr, sizeof(servAddr));
                int strLen = recvfrom(sock, buffer, BUF_SIZE, 0, &fromAddr, &addrLen);
                buffer[strLen] = 0;
                printf("Message form server: %s\n", buffer);
                closesocket(sock);
                WSACleanup();
                break;
            }
            else if (strstr(cmd.c_str(), "-testgbn") != NULL)
            {
                printf("请输入丢包率，ACK丢失率：\n");
                float packetLossRatio = 0.2; //默认包丢失率 0.2
                float ackLossRatio = 0.2; //默认 ACK 丢失率 0.2
                cin >> packetLossRatio;
                cin >> ackLossRatio;
                printf("开始进行GBN测试...\n");
                printf("包丢失率:%f, ACK 丢失率:%f\n", packetLossRatio, ackLossRatio);

                unsigned short waitSeq = 1;//等待期望接受的Seq
                unsigned short seq;//实际收到的Seq
                unsigned short recvSeq = 0;//接收窗口大小为 1，已确认的序列号
                string cache;//收到的临时缓存
                string receivecache;//最终缓存
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
                    buffer[a] = 0;
                    cache = string(buffer);
                    seq = (unsigned short)buffer[0];
                    //随机法模拟包是否丢失
                    BOOL b = lossInLossRatio(packetLossRatio);
                    if (b) {
                        printf("seq为 %d 的包丢失\n", seq);
                        continue;
                    }
                    printf("收到包的seq是 %d\n", seq);
                    //如果是期待的包，正确接收，正常确认即可
                    if ((waitSeq - seq) == 0) {
                        ++waitSeq;
                        if (waitSeq == 17) {
                            waitSeq = 1;
                        }
                        buffer[0] = seq;
                        recvSeq = seq;
                        buffer[1] = '\0';
                        cache.erase(0, 1);
                        while (receivecache.find(cache)!= string::npos)
                        {
                            int t = receivecache.find(cache);
                            receivecache.erase(t,t+cache.size());
                        }
                        receivecache.append(cache);

                        cout << "收到的数据为：" << cache << "\n长度为：" << cache.size() << endl;
                    }
                    else {

                        //如果当前一个包都没有收到，则等待 Seq 为 1 的数据包，不是则不返回 ACK（因为并没有上一个正确的 ACK）
                        if (!recvSeq) {
                            continue;
                        }
                        buffer[0] = recvSeq;
                        buffer[1] = '\0';
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
                receivecache.erase(receivecache.size() - 8,receivecache.size()-1);
                ofstream fileout;
                fileout.open("client_receive.txt");
                fileout << receivecache.c_str();
                fileout.close();
            }
        }
        system("pause");
    }
    closesocket(sock);
    WSACleanup();
    return 0;
}
