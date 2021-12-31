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

vector<stud_route_msg> route;  //���ñ������ṹ��Ϊ·�ɱ�

/*
·�ɱ��ʼ����
*/
void stud_Route_Init()
{
	//��ʼ��,routeΪȫ�ֱ���,�Ѿ���ʼ��
	return;
}

/*
·�ɱ����
���룺proute ��ָ����Ҫ���·����Ϣ�Ľṹ��ͷ����
    �����ݽṹ
	stud_route_msg �Ķ������£�
    typedef struct stud_route_msg
    {
      unsigned int dest;  //Ŀ�ĵ�ַ
      unsigned int masklen;  //�������볤��
      unsigned int nexthop;  //��һ��
    } stud_route_msg;
*/
//��·�ɱ�����ӱ���
void stud_route_add(stud_route_msg* proute)
{

	stud_route_msg route1; //�������ӵ�·�ɱ���
      //��·�ɱ�����е�Ŀ�ĵ�ַ���������볤�ȣ���һ�����������ֽ���ת��Ϊ�����ֽ���
	unsigned int dest = ntohl(proute->dest); 
	unsigned int masklen = ntohl(proute->masklen);   
	unsigned int nexthop = ntohl(proute->nexthop); 
      //�洢������ӵ�·�ɱ�����
	route1.dest = dest;
	route1.masklen = masklen;
	route1.nexthop = nexthop; 
      //��ӵ�·�ɱ��� 
	route.push_back(route1);  
	return;
}
//������յķ���
int stud_fwd_deal(char* pBuffer, int length)
{
	int version = pBuffer[0] >> 4; //�汾��	
  	int total_length = pBuffer[0] & 0xf;// ͷ������
	short ttl = (unsigned short)pBuffer[8];// ����ʱ��
	int destination = ntohl(*(unsigned int*)(pBuffer + 16));// pBuffer��16���ֽں��32bitsΪĿ��ip��ַ

	//���ip���ĵİ汾���Ƿ�Ϊ4      
	if (version != 4)
	{

		//������ְ汾�Ų�Ϊ4�Ĵ��󣬱�����󲢷���1

		ip_DiscardPkt(pBuffer, STUD_IP_TEST_VERSION_ERROR);

		return 1;

	}
	//���ip���ĵ�ͷ�������Ƿ���ȷ
	if (total_length < 5)
	{
		//ip���ĵ�ͷ������Ҫ>=20�ֽڣ���С�ڣ���������1
      	ip_DiscardPkt(pBuffer, STUD_IP_TEST_HEADLEN_ERROR);
     		return 1;
	}
	//���ip���ĵ�����ʱ��
	if (ttl <= 0)
	{
		//ip���ĵ�����ʱ��Ҫ>0,���򣬱�������1
		ip_DiscardPkt(pBuffer, STUD_IP_TEST_TTL_ERROR);
		return 1;
	}
	//���ip���ĵ�Ŀ�ĵ�ַ		
	if (destination == getIpv4Address())
	{
		//ip���ĵ�Ŀ�ĵ�ַΪ������ַ���Ͻ��ϲ�Э�鴦��
		fwd_LocalRcv(pBuffer, length);
		return 0;
	}
      //�����Ǳ�����ַ������·�ɱ�
	stud_route_msg* dst_route = NULL;  //·�ɱ���
	int dest = destination;   //destΪĿ�ĵ�ַ
	for (int i = 0; i < route.size(); i++)  //����·�ɱ�
	{     
            //����·�ɱ��е�ÿһ��·�ɱ�����Ŀ�ĵ�ַ�������ַ
		unsigned int temp_sub_net = route[i].dest & ((1 << 31) >> (route[i].masklen - 1)); 
		if (temp_sub_net == dest) //���Ŀ�ĵ�ַ��·�ɱ��ĳһ������ƥ��
		{
			dst_route = &route[i];  //��¼ƥ���λ��
			break;
		}
	}
	if (dst_route==NULL)
	{
		//�������û��ƥ���ַ������ֱ�ӷ���1
		fwd_DiscardPkt(pBuffer, STUD_FORWARD_TEST_NOROUTE);
		return 1;
	}
	else  
	{
		pBuffer[8] = ttl - 1;   //��ttl��1
		memset(pBuffer + 10, 0, 2);
		//����У���
		unsigned long check = 0;

		//��ip����ÿ2���ֽ�Ϊ��λ�ָ�����ۼ�	
		for (int i = 0; i < total_length * 2; i++)
		{
			check += ((unsigned char)pBuffer[i*2] << 8)+ (unsigned char)pBuffer[i*2 + 1];
		}
		unsigned short low = check & 0xffff; //ȡ����16λ
		unsigned short high = check >> 16; //ȡ����16λ
            unsigned checksum=~(low+high);
		unsigned short header_checksum = htons(checksum);
		memcpy(pBuffer + 10, &header_checksum, 2); //����У���
		fwd_SendtoLower(pBuffer, length, dst_route->nexthop); //������װ�õ�ip���鵽Ŀ������
	}
	return 0;
}