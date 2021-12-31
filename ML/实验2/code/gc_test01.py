import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import accuracy_score
import gd
#有惩罚项，满足贝叶斯假设，共轭梯度法
mean1=[0.8,0.3]
mean0=[-1,-0.3]
cov1=[[0.2,0],[0,0.2]]
cov0=[[0.2,0],[0,0.2]]
size1=50
size0=50
deta=0.01
epsilon=1e-4
lamda=1e-2
times=10000
X,x_train,y_train=gd.data_produce(size1,size0,mean1,mean0,cov1,cov0)
X_test,x_test,y_test=gd.data_produce(2*size1,2*size0,mean1,mean0,cov1,cov0)
fig = plt.figure(figsize=(12,6),dpi=120)
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False
ax = fig.add_subplot(121)
ax2=fig.add_subplot(122)
ax.set_xlabel('X')
ax.set_ylabel('Y')
ax.scatter(x_train[:size1,0],x_train[:size1,1],label="train_y=1")
ax.scatter(x_train[size1:,0],x_train[size1:,1],label="train_y=0")
w=gd.Conjugate_gradient(X,y_train,epsilon=epsilon,lamda=lamda)
result1=gd.accuary(X,w,y_train)
print(max(result1,1-result1))
x=np.linspace(x_train[:,0].min(),x_train[:,0].max(),100)
y=-(w[1]*x+w[0])/w[2]
ax.plot(x,y,color='r')
print(-w[1]/w[2])
print(-w[0]/w[2])
ax.set_title("train_data")
ax.legend()
ax2.set_xlabel('X')
ax2.set_ylabel('Y')
ax2.scatter(x_test[:2*size1,0],x_test[:2*size1,1],label="test_y=1")
ax2.scatter(x_test[2*size1:,0],x_test[2*size1:,1],label="test_y=0")
w_test=gd.Conjugate_gradient(X_test,y_test,epsilon=epsilon,lamda=lamda)
result2=gd.accuary(X_test,w_test,y_test)
print(max(result2,1-result2))
x_test=np.linspace(x_test[:,0].min(),x_test[:,0].max(),100)
y_test=-(w_test[1]*x+w_test[0])/w_test[2]
ax2.plot(x_test,y_test,color='r')
print(-w_test[1]/w_test[2])
print(-w_test[0]/w_test[2])
ax2.set_title("test_data")
ax2.legend()
#plt.show()
plt.savefig("gctest01")
