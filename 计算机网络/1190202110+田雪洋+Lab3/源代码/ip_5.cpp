#include "sysInclude.h"
#include <stdio.h>
#include <vector>
using namespace std;

// system support
extern void fwd_LocalRcv(char* pBuffer, int length);

extern void fwd_SendtoLower(char* pBuffer, int length, unsigned int nexthop);

extern void fwd_DiscardPkt(char* pBuffer, int type);

extern unsigned int getIpv4Address();

// implemented by students

vector<stud_route_msg> route;  //设置遍历器结构作为路由表

/*
路由表初始函数
*/
void stud_Route_Init()
{
	//初始化,route为全局变量,已经初始化
	return;
}

/*
路由表表项
输入：proute ：指向需要添加路由信息的结构体头部，
    其数据结构
	stud_route_msg 的定义如下：
    typedef struct stud_route_msg
    {
      unsigned int dest;  //目的地址
      unsigned int masklen;  //子网掩码长度
      unsigned int nexthop;  //下一跳
    } stud_route_msg;
*/
//往路由表中添加表项
void stud_route_add(stud_route_msg* proute)
{

	stud_route_msg route1; //定义待添加的路由表项
      //将路由表表项中的目的地址，子网掩码长度，下一跳，由网络字节序转换为本地字节序
	unsigned int dest = ntohl(proute->dest); 
	unsigned int masklen = ntohl(proute->masklen);   
	unsigned int nexthop = ntohl(proute->nexthop); 
      //存储到待添加的路由表项中
	route1.dest = dest;
	route1.masklen = masklen;
	route1.nexthop = nexthop; 
      //添加到路由表中 
	route.push_back(route1);  
	return;
}
//处理接收的分组
int stud_fwd_deal(char* pBuffer, int length)
{
	int version = pBuffer[0] >> 4; //版本号	
  	int total_length = pBuffer[0] & 0xf;// 头部长度
	short ttl = (unsigned short)pBuffer[8];// 生存时间
	int destination = ntohl(*(unsigned int*)(pBuffer + 16));// pBuffer第16个字节后的32bits为目的ip地址

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
	if (destination == getIpv4Address())
	{
		//ip报文的目的地址为本机地址，上交上层协议处理
		fwd_LocalRcv(pBuffer, length);
		return 0;
	}
      //若不是本机地址，遍历路由表
	stud_route_msg* dst_route = NULL;  //路由表项
	int dest = destination;   //dest为目的地址
	for (int i = 0; i < route.size(); i++)  //遍历路由表
	{     
            //对于路由表中的每一个路由表项，求出目的地址的网络地址
		unsigned int temp_sub_net = route[i].dest & ((1 << 31) >> (route[i].masklen - 1)); 
		if (temp_sub_net == dest) //如果目的地址与路由表的某一个表项匹配
		{
			dst_route = &route[i];  //记录匹配的位置
			break;
		}
	}
	if (dst_route==NULL)
	{
		//如果出现没有匹配地址，报错，直接返回1
		fwd_DiscardPkt(pBuffer, STUD_FORWARD_TEST_NOROUTE);
		return 1;
	}
	else  
	{
		pBuffer[8] = ttl - 1;   //将ttl减1
		memset(pBuffer + 10, 0, 2);
		//计算校验和
		unsigned long check = 0;

		//将ip报文每2个字节为单位分割，进行累加	
		for (int i = 0; i < total_length * 2; i++)
		{
			check += ((unsigned char)pBuffer[i*2] << 8)+ (unsigned char)pBuffer[i*2 + 1];
		}
		unsigned short low = check & 0xffff; //取出低16位
		unsigned short high = check >> 16; //取出高16位
            unsigned checksum=~(low+high);
		unsigned short header_checksum = htons(checksum);
		memcpy(pBuffer + 10, &header_checksum, 2); //更新校验和
		fwd_SendtoLower(pBuffer, length, dst_route->nexthop); //发送组装好的ip分组到目的网络
	}
	return 0;
}