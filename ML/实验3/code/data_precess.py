import numpy as np

def data_produce(N,k,cov,mean):
    X = np.ones((2, k*N))
    y = []
    for i in range(0,k):
        x_train=np.random.multivariate_normal(mean[i],cov[i],size=N)
        X[:,i*N:(i+1)*N]=x_train.T
        y.append(list(range(i*N,(i+1)*N)))
    return X,y
#计算准确率
def accaury(y,pre,k):
    sum=0.0
    s=0.0
    for i in range(k):
        for j in y[i]:
            s+=1
            if j in pre[i]:
                sum+=1
    return sum/s

