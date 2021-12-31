from sklearn import datasets
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
import gd
import warnings

warnings.filterwarnings('ignore')
cancer = datasets.load_breast_cancer()
# 将乳腺癌数据集转换为dataframe类型
data = pd.DataFrame(cancer.data, columns=cancer.feature_names)
data['target'] = cancer.target
# #对数据进行归一化
for i in range(30):
    data.iloc[:, i] = (data.iloc[:, i] - data.iloc[:, i].min()) / (data.iloc[:, i].max() - data.iloc[:, i].min())
# 划分训练集和测试集，测试集的数量为40%
x_train, x_test, y_train, y_test = train_test_split(data.iloc[:, 0:29], data.iloc[:, 30], test_size=0.4, random_state=2)
# 生成的X矩阵
X_train = np.ones((x_train.shape[0], x_train.shape[1] + 1))
y_train = y_train.values
y_test = y_test.values
X_test = np.ones((x_test.shape[0], x_test.shape[1] + 1))
# 构造X矩阵，第一维都设置成1，方便与w相乘
X_train[:, 1:x_train.shape[1] + 1] = x_train.values
X_test[:, 1:x_test.shape[1] + 1] = x_test.values
# 有惩罚项，满足贝叶斯假设
deta = 0.001
epsilon = 1e-6
lamda = 1e-2
times = 10000

w = gd.gradient_descent(X_train, y_train, lamda=lamda, deta=deta, epsilon=epsilon, times=int(times))
print(gd.accuary(X_train, w, y_train))

w_test = gd.gradient_descent(X_test, y_test, lamda=lamda, deta=deta, epsilon=epsilon, times=int(times))
print(gd.accuary(X_test, w_test, y_test))
