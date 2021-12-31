/*
IPV4 分组收发实验
*/

#include "sysInclude.h"

#include <stdio.h>

#include <malloc.h>

extern void ip_DiscardPkt(char* pBuffer, int type);


extern void ip_SendtoLower(char* pBuffer, int length);


extern void ip_SendtoUp(char* pBuffer, int length);


extern unsigned int getIpv4Address();

//检测接收到的报文是否有错误,若有错误,返回1,若无错,返回0,并上交报文
int stud_ip_recv(char* pBuffer, unsigned short length)

{
	
int version = pBuffer[0] >> 4; //版本号	
int total_length = pBuffer[0] & 0xf;// 头部长度
short ttl = (unsigned short)pBuffer[8];// 生存时间
int destination = ntohl(*(unsigned int*)(pBuffer + 16));// pBuffer第16个字节后的32bits为目的ip地址（long int）

//检测ip报文的版本号是否为4      
if (version != 4)
{

	//如果出现版本号不为4的错误，报告错误并返回1

	ip_DiscardPkt(pBuffer, STUD_IP_TEST_VERSION_ERROR);

	return 1;

}
//检测ip报文的头部长度是否正确
if (total_length < 5)
{
	//ip报文的头部长度要>=20字节，若小于，报错，返回1
      ip_DiscardPkt(pBuffer, STUD_IP_TEST_HEADLEN_ERROR);
      return 1;
}
//检测ip报文的生存时间
if (ttl <= 0)
{
	//ip报文的生存时间要>0,否则，报错，返回1
	ip_DiscardPkt(pBuffer, STUD_IP_TEST_TTL_ERROR);
	return 1;
}
//检测ip报文的目的地址		
if (destination != getIpv4Address() && destination != 0xffff)
{
	//ip报文的目的地址是否为目标地址或者广播地址，若不是，报错，返回1
	ip_DiscardPkt(pBuffer, STUD_IP_TEST_DESTINATION_ERROR);
	return 1;
}
//计算校验和
unsigned long check = 0;

//将ip报文每2个字节为单位分割，进行累加
for (int i = 0; i < total_length * 2; i++)
{
	check += ((unsigned char)pBuffer[i * 2] << 8)+ (unsigned char)pBuffer[i * 2 + 1];
}
unsigned short low = check & 0xffff; //取出低16位
unsigned short high = check >> 16; //取出高16位
if (low + high!= 0xffff) //低16位与高16位相加
{
	//如果出现首部校验和的错误(计算结果不为0xffff)，报错，返回1
	ip_DiscardPkt(pBuffer, STUD_IP_TEST_CHECKSUM_ERROR);
	return 1;
}
ip_SendtoUp(pBuffer, length); //提交给上层协议
return 0;
}
//组装ip报文并发送
int stud_ip_Upsend(char* pBuffer, unsigned short len, unsigned int srcAddr,
	unsigned int dstAddr, byte protocol, byte ttl)
{
	short length = len + 20; //得到这层的数据长度
	char* buffer = (char*)malloc(length * sizeof(char));
	memset(buffer, 0, length);
	buffer[0] = 0x45; //规定版本号和首部长度为4和5×4=20
	memcpy(buffer+8,&ttl,1);  //规定生存时间ttl
      memcpy(buffer+9,&protocol,1);//规定协议号
	memcpy(buffer + 20, pBuffer, len);//复制数据
	// 将数据长度转换为网络字节序
	unsigned short network = htons(length);
	memcpy(buffer + 2, &network, 2);
      //解析ip报文的目的地址和源地址
	unsigned int source = htonl(srcAddr); 
	unsigned int dis = htonl(dstAddr); 
	memcpy(buffer + 12, &source, 4);
	memcpy(buffer + 16, &dis, 4);

	//计算校验和
	unsigned long check = 0;

	//将ip报文头部每2个字节为单位分割，进行累加
	for (int i = 0; i < 20; i+=2)
	{
		check += ((unsigned char)buffer[i] << 8)+ (unsigned char)buffer[i + 1];
	}
	unsigned short low = check & 0xffff; //取出低16位
	unsigned short high = check >> 16; //取出高16位
	unsigned short checksum = ~(low + high); //低16位与高16位相加并取反
      
  	// 将校验和转换为网络字节序
	unsigned short checksum1 = htons(checksum); 
	memcpy(buffer + 10, &checksum1, 2);
	ip_SendtoLower(buffer, length); //发送分组
	return 0;

}
