import numpy as np
import data
import matplotlib.pyplot as plt
mean=np.array([-2,2])
cov=np.array( [[1,0],[0,0.01]])
N=100
X=data.data_produce(mean,cov,N)
w,x,xmeans=data.PCA(X,1)
#得到重建的数据
Y=x@w.T@w+xmeans
plt.scatter(X[:, 0], X[:, 1],color='b')
plt.scatter(Y[:, 0], Y[:, 1],color='r')
plt.show()
