import math

import exp1
import numpy as np
import matplotlib.pyplot as plt
#不带正则项的最小二乘法的解析解
#样本容量为10
# exp1.plot_lsm(size=10,lamda=0,a=1)#一次函数
#exp1.plot_lsm(size=10,lamda=0,a=3)#三次函数
#exp1.plot_lsm(size=10,lamda=0,a=5)#五次函数
#exp1.plot_lsm(size=10,lamda=0,a=7)#七次函数
#exp1.plot_lsm(size=10,lamda=0,a=9)#九次函数
#exp1.plot_lsm(size=10,lamda=0,a=19)#十九次函数
#样本容量为30
#exp1.plot_lsm(size=30,lamda=0,a=9)#九次函数
#样本容量为50
#exp1.plot_lsm(size=50,lamda=0,a=9)#九次函数
#样本容量为100
#exp1.plot_lsm(size=100,lamda=0,a=9)#九次函数
#带正则项的最小二乘法的解析解
#exp1.plot_lsm_lamda(10)
#梯度下降法不同阶数
#exp1.plot_Gu(size=10,lamda=math.exp(-12),a=1)#一次函数
#exp1.plot_Gu(size=10,lamda=math.exp(-12),a=3)#三次函数
#exp1.plot_Gu(size=10,lamda=math.exp(-12),a=5)#五次函数
#exp1.plot_Gu(size=10,lamda=math.exp(-12),a=7)#七次函数
#exp1.plot_Gu(size=10,lamda=math.exp(-12),a=9)#九次函数
#exp1.plot_Gu(size=10,lamda=math.exp(-12),a=19)#十九次函数
#样本容量为30
#exp1.plot_Gu(size=30,lamda=math.exp(-12),a=9)#九次函数
#样本容量为50
#exp1.plot_Gu(size=50,lamda=math.exp(-12),a=9)#九次函数
#样本容量为100
#exp1.plot_Gu(size=100,lamda=math.exp(-12),a=9)#九次函数
#共轭梯度法
#exp1.plot_Gc(size=10,lamda=math.exp(-10),a=1)#一次函数
#exp1.plot_Gc(size=10,lamda=math.exp(-10),a=3)#三次函数
#exp1.plot_Gc(size=10,lamda=math.exp(-10),a=5)#五次函数
#exp1.plot_Gc(size=10,lamda=math.exp(-10),a=7)#七次函数
#exp1.plot_Gc(size=10,lamda=math.exp(-10),a=9)#九次函数
#exp1.plot_Gc(size=10,lamda=math.exp(-10),a=19)#十九次函数
#样本容量为30
#exp1.plot_Gc(size=30,lamda=math.exp(-10),a=9)#九次函数
#样本容量为50
#exp1.plot_Gc(size=50,lamda=math.exp(-10),a=9)#九次函数
#样本容量为100
#exp1.plot_Gc(size=100,lamda=math.exp(-10),a=9)#九次函数