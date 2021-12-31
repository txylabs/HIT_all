import numpy as np
import data
import matplotlib.pyplot as plt
mean = np.array([1, 2, 3])
cov = np.array([[0.01, 0, 0], [0, 1, 0], [0, 0, 1]])
N = 100
X = data.data_produce(mean, cov, N)
w,x,xmeans=data.PCA(X,2)
#得到重建的数据
Y=x@w.T@w+xmeans
fig = plt.figure()
ax = plt.axes(projection='3d')
fig.add_axes(ax)
ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("z")
ax.scatter(X[:, 0], X[:, 1], X[:, 2], c="b", label='Origin Data')
ax.scatter(Y[:, 0], Y[:, 1], Y[:, 2], c='r', label='PCA Data')
ax.plot_trisurf(Y[:, 0], Y[:, 1], Y[:, 2], color='k', alpha=0.3)
ax.legend(loc='best')
plt.show()
