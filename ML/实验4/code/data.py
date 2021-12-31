import os
from PIL import Image
import numpy as np
def data_produce(mean,cov,N):
    X = np.random.multivariate_normal(mean, cov, size=N)
    return X
def PCA(X,k):
    xmeans=np.sum(X,axis=0)/X.shape[0]
    x=X-xmeans
    cov=np.cov(x,rowvar=False)
    #计算特征值和特征向量
    lamda, featurevector = np.linalg.eig(cov)
    #对特征值进行排序
    index=np.argsort(lamda)[::-1]
    w=featurevector[:,index[0:k:1]]
    return w.T,x,xmeans

def faces(file):
    #读取人脸图片
    pic = Image.open(file).convert('L') # 读入图片，并将三通道转换为灰度图
    return np.array(pic)

def psnr(source, target):
    #计算峰值信噪比
    rmse = np.sqrt(np.mean((source - target) ** 2))
    return 20 * np.log10(255.0 / rmse)


