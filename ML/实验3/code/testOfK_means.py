import numpy as np
import data_precess
import k_means
import matplotlib.pyplot as plt
N=100
k=3
eplison=1e-6
mean=np.array([ [1,4], [3,1], [5,4] ])
cov=np.array([ [[0.5,0],[0,0.5]], [[0.5,0],[0,0.5]], [[0.5,0],[0,0.5]] ])
X,y=data_precess.data_produce(N,k,cov,mean)
C,m=k_means.k_means(X,k,2,N,eplison)
print(data_precess.accaury(y,C,k))
plt.title("k-means")
plt.scatter(X[0,0:100],X[1,0:100],color='r',label="label1")
plt.scatter(X[0,100:200],X[1,100:200],color='y',label="label2")
plt.scatter(X[0,200:300],X[1,200:300],color='g',label="label3")
plt.scatter(m[0,:],m[1,:],color='b',marker='x', s=200,label="Cluster_point")
plt.legend()
plt.show()