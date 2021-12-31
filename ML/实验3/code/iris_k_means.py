import numpy as np
import pandas as pd
import data_precess
import k_means
import matplotlib.pyplot as plt
#从文件中读入鸢尾花数据集
data=pd.read_csv("iris.csv")
#将鸢尾花数据集的标签数字化
data.loc[data['class']=='Iris-setosa','class']=0
data.loc[data['class']=='Iris-virginica','class']=2
data.loc[data['class']=='Iris-versicolor','class']=1
#选取特征，归一化
data['petal_length'] = (data['petal_length'] - data['petal_length'].min()) / (data['petal_length'].max() - data['petal_length'].min())
data['petal_width'] = (data['petal_width'] - data['petal_width'].min()) / (data['petal_width'].max() - data['petal_width'].min())
#绘图
plt.title('iris')
plt.xlabel('petal_width')
plt.ylabel('petal_length')
plt.scatter(data.loc[data['class']==0,'petal_width'],data.loc[data['class']==0,'petal_length'],label='Iris-setosa')
plt.scatter(data.loc[data['class']==1,'petal_width'],data.loc[data['class']==1,'petal_length'],label='Iris-virginica')
plt.scatter(data.loc[data['class']==2,'petal_width'],data.loc[data['class']==2,'petal_length'],label='Iris-versicolor')
#处理数据，使之适应k-means函数
X=data[['petal_width','petal_length']]
X=X.values
X=X.T
y=[]
y.append(data.loc[data['class']==0,'class'].values.tolist())
y.append(data.loc[data['class']==1,'class'].values.tolist())
y.append(data.loc[data['class']==2,'class'].values.tolist())
for i in range(3):
    for j in range(len(y[i])):
        y[i][j]=j+i*len(y[i])
#代入k-means函数求解
C,m=k_means.k_means(X,3,2,50,1e-6)
#绘制簇点
plt.scatter(m[0,:],m[1,:],color='b',marker='x', s=200,label="Cluster_point")
plt.show()
print(data_precess.accaury(y,C,3))