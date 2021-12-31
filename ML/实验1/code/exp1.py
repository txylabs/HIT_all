import math

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split


def data_produce(size,M):
    #在[0,2pi]之间生成size个数据
    x=np.linspace(0,1,size)
    #生成标准差为0.09，均值为0的高斯分布噪声
    gauss_noise=np.random.normal(0,0.09,size=size)
    y=np.sin(2*np.pi*x)
    #生成测试数据集，并添加高斯噪声
    x_train=x
    y_train=y+gauss_noise
    X = []
    for i in range(M + 1):
        X.append(x_train ** i)
    X = np.array(X).T
    return x,y,x_train,y_train,X
def lsm(X,y_train,lamda):
    # 最小二乘法得到多项式函数的参数w，lamda即正则项的超参数，
    # 当lamda=0时，为无正则项的最小二乘法算法，
    # 当lamda！=0时，为带正则项的最小二乘法算法。
    w=np.dot(np.dot(np.linalg.inv(np.dot(X.T,X)+lamda*np.eye(X.shape[1])),X.T),y_train)
    #求得多项式函数的解析式为：
    pre=np.poly1d(w[::-1])
    return w,pre
def loss(X,y_train,w,lamda):
    #计算损失函数E(w)
    return 1/2*np.dot((X.dot(w)-y_train).T,(X.dot(w)-y_train))+lamda/2*np.dot(w.T,w)
def ERMS(loss,size):
    #计算方根均值Erms
    return np.sqrt(2 * loss / size)
def Gradient_descent(X,y_train,deta,lamda,times):#梯度下降法
    #设置初始的w的值
    w=np.zeros(X.shape[1])
    w0=w
    #计算初始的损失函数的取值
    loss_0=loss(X,y_train,w,lamda)
    #根据设置的迭代次数，进行迭代
    for i in range(1,times+1):
        #E（w）对w求偏导
        gradient=((X.T).dot(X)).dot(w)-np.dot(X.T,y_train)+lamda*w
        #w的迭代方程，deta为学习速率
        w=w-deta*gradient
        #计算新的损失函数
        loss_new=loss(X,y_train,w,lamda)
        #当损失函数变大时，学习速率减半
        if(loss_new<loss_0):
            loss_0=loss_new
            w0=w
        else:
            deta=deta*0.5
    #得到多项式函数
    pre = np.poly1d(w0[::-1])
    return w0,pre,loss_0
def Conjugate_gradient(X,y_train,lamda,epsilon):
    #共轭梯度法
    #计算A,b
    A=X.T.dot(X)+lamda*np.eye(X.shape[1])
    b=X.T.dot(y_train)
    #初始化w，r,p,将rkT*rk保留
    w = np.zeros(X.shape[1])
    r=A.dot(w)-b
    p=-r
    t=r.T.dot(r)
    #循环，直到t不满足精度为止
    while t>epsilon:
        #算法迭代
        alpha=t/p.T.dot(A).dot(p)
        w=w+alpha*p
        r=r+alpha*A.dot(p)
        beta=r.T.dot(r)/t
        p=-r+beta*p
        t=r.T.dot(r)
    #得到多项式函数的解析式
    pre = np.poly1d(w[::-1])
    return w,pre
def plot_lsm(size,lamda,a):
    #生成数据集
    x,y,x_train,y_train,X=data_produce(size,a)
    #最小二乘法预测
    w,pre=lsm(X,y_train,lamda)
    w_loss,pre_loss=lsm(X,y_train,math.exp(-30))
    #打印方差均根
    print(ERMS(loss(X,y_train,w,lamda),size))
    print(pre)
    plt.xlabel("x")
    plt.ylabel("y")
    plt.xlim(0,1)
    plt.ylim(-1.2,1.2)
    plt.rcParams['font.sans-serif'] = ['SimHei']
    plt.rcParams['axes.unicode_minus'] = False
    plt.title("lsm,size="+str(size)+".多项式阶数为："+str(a))
    x = np.linspace(0, 1, 100)
    y = np.sin(x * 2 * np.pi)
    plt.plot(x,y,color='b',label='true')
    plt.scatter(x_train,y_train,color='g',label='real')
    plt.plot(x,pre(x),color='r',label='lsm')
    plt.plot(x, pre_loss(x),color='y' ,label='lsm_punish')
    plt.legend()
    plt.savefig(str(size)+".png")
def plot_lsm_lamda(size,a=9):
    x,y,x_train1,y1_train,x_train=data_produce(size,a)
    x, y, x_test1, y1_test, x_test = data_produce(size*2,a)
    # 划分数据集为测试集，训练集，验证集
    #x_train, x_test, y1_train, y1_test = train_test_split(X, y_train, random_state=1, test_size=10)
    lamda=np.linspace(-40,0,40).astype(int)
    ERMtest=[]
    ERMtrain=[]
    #对于测试集和预测集分别计算均方根误差
    for i in lamda:
        t=math.exp(i)
        w_test,pre=lsm(x_test,y1_test,t)
        ERMtest.append(ERMS(loss(x_test,y1_test,w_test,t),size))
        w_train, pre = lsm(x_train, y1_train, t)
        ERMtrain.append(ERMS(loss(x_train, y1_train, w_train, t), size))
    plt.xlabel("lnlamda")
    plt.ylabel("Erms")
    plt.rcParams['font.sans-serif'] = ['SimHei']
    plt.rcParams['axes.unicode_minus'] = False
    minERM=1.0
    minlamda=0
    #找出均方根误差最小值
    for i in range(0,len(ERMtest)):
        if minERM>=ERMtest[i]:
            minERM=ERMtest[i]
            minlamda=lamda[i]
    print(minERM)
    plt.plot(lamda,ERMtrain,color='b',label='TRAIN')
    plt.plot(lamda,ERMtest,color='r',label='VALIATION')
    plt.legend()
    plt.savefig("erm4.png")
def plot_Gu(size,lamda,a):
    #生成数据集
    x,y,x_train,y_train,X=data_produce(size,a)
    #梯度下降法
    times=10000
    w,pre,loss_0=Gradient_descent(X,y_train,0.01,lamda,times)
    #打印方差均根
    print(ERMS(loss_0,size))
    print(pre)
    plt.xlabel("x")
    plt.ylabel("y")
    plt.xlim(0,1)
    plt.ylim(-1.2,1.2)
    plt.rcParams['font.sans-serif'] = ['SimHei']
    plt.rcParams['axes.unicode_minus'] = False
    plt.title("lsm,size="+str(size)+".多项式阶数为："+str(a))
    x = np.linspace(0, 1, 100)
    y = np.sin(x * 2 * np.pi)
    plt.plot(x,y,color='b',label='true')
    plt.scatter(x_train,y_train,color='g',label='train')
    plt.plot(x,pre(x),color='r',label='lsm')
    plt.legend()
    #plt.show()
    plt.savefig("g"+str(size)+".png")
def plot_Gc(size,lamda,a):
    #生成数据集
    x,y,x_train,y_train,X=data_produce(size,a)
    #共轭梯度法
    w,pre=Conjugate_gradient(X,y_train,lamda,1e-6)
    #打印方差均根
    print(ERMS(loss(X,y_train,w,lamda),size))
    print(pre)
    plt.xlabel("x")
    plt.ylabel("y")
    plt.xlim(0,1)
    plt.ylim(-1.2,1.2)
    plt.rcParams['font.sans-serif'] = ['SimHei']
    plt.rcParams['axes.unicode_minus'] = False
    plt.title("lsm,size="+str(size)+".多项式阶数为："+str(a))
    x = np.linspace(0, 1, 100)
    y = np.sin(x * 2 * np.pi)
    plt.plot(x,y,color='b',label='true')
    plt.scatter(x_train,y_train,color='g',label='train')
    plt.plot(x,pre(x),color='r',label='gc')
    plt.legend()
    #plt.show()
    plt.savefig("gc"+str(size)+".png")