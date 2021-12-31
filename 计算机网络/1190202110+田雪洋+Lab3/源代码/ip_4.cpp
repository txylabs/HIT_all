/*
IPV4 �����շ�ʵ��
*/

#include "sysInclude.h"

#include <stdio.h>

#include <malloc.h>

extern void ip_DiscardPkt(char* pBuffer, int type);


extern void ip_SendtoLower(char* pBuffer, int length);


extern void ip_SendtoUp(char* pBuffer, int length);


extern unsigned int getIpv4Address();

//�����յ��ı����Ƿ��д���,���д���,����1,���޴�,����0,���Ͻ�����
int stud_ip_recv(char* pBuffer, unsigned short length)

{
	
int version = pBuffer[0] >> 4; //�汾��	
int total_length = pBuffer[0] & 0xf;// ͷ������
short ttl = (unsigned short)pBuffer[8];// ����ʱ��
int destination = ntohl(*(unsigned int*)(pBuffer + 16));// pBuffer��16���ֽں��32bitsΪĿ��ip��ַ��long int��

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
if (destination != getIpv4Address() && destination != 0xffff)
{
	//ip���ĵ�Ŀ�ĵ�ַ�Ƿ�ΪĿ���ַ���߹㲥��ַ�������ǣ���������1
	ip_DiscardPkt(pBuffer, STUD_IP_TEST_DESTINATION_ERROR);
	return 1;
}
//����У���
unsigned long check = 0;

//��ip����ÿ2���ֽ�Ϊ��λ�ָ�����ۼ�
for (int i = 0; i < total_length * 2; i++)
{
	check += ((unsigned char)pBuffer[i * 2] << 8)+ (unsigned char)pBuffer[i * 2 + 1];
}
unsigned short low = check & 0xffff; //ȡ����16λ
unsigned short high = check >> 16; //ȡ����16λ
if (low + high!= 0xffff) //��16λ���16λ���
{
	//��������ײ�У��͵Ĵ���(��������Ϊ0xffff)����������1
	ip_DiscardPkt(pBuffer, STUD_IP_TEST_CHECKSUM_ERROR);
	return 1;
}
ip_SendtoUp(pBuffer, length); //�ύ���ϲ�Э��
return 0;
}
//��װip���Ĳ�����
int stud_ip_Upsend(char* pBuffer, unsigned short len, unsigned int srcAddr,
	unsigned int dstAddr, byte protocol, byte ttl)
{
	short length = len + 20; //�õ��������ݳ���
	char* buffer = (char*)malloc(length * sizeof(char));
	memset(buffer, 0, length);
	buffer[0] = 0x45; //�涨�汾�ź��ײ�����Ϊ4��5��4=20
	memcpy(buffer+8,&ttl,1);  //�涨����ʱ��ttl
      memcpy(buffer+9,&protocol,1);//�涨Э���
	memcpy(buffer + 20, pBuffer, len);//��������
	// �����ݳ���ת��Ϊ�����ֽ���
	unsigned short network = htons(length);
	memcpy(buffer + 2, &network, 2);
      //����ip���ĵ�Ŀ�ĵ�ַ��Դ��ַ
	unsigned int source = htonl(srcAddr); 
	unsigned int dis = htonl(dstAddr); 
	memcpy(buffer + 12, &source, 4);
	memcpy(buffer + 16, &dis, 4);

	//����У���
	unsigned long check = 0;

	//��ip����ͷ��ÿ2���ֽ�Ϊ��λ�ָ�����ۼ�
	for (int i = 0; i < 20; i+=2)
	{
		check += ((unsigned char)buffer[i] << 8)+ (unsigned char)buffer[i + 1];
	}
	unsigned short low = check & 0xffff; //ȡ����16λ
	unsigned short high = check >> 16; //ȡ����16λ
	unsigned short checksum = ~(low + high); //��16λ���16λ��Ӳ�ȡ��
      
  	// ��У���ת��Ϊ�����ֽ���
	unsigned short checksum1 = htons(checksum); 
	memcpy(buffer + 10, &checksum1, 2);
	ip_SendtoLower(buffer, length); //���ͷ���
	return 0;

}
