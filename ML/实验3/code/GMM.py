import random
from scipy.stats import multivariate_normal
import numpy as np


# 损失函数为极大似然函数，计算损失函数
def loss(X, cov, mu, alpha, k, N):
    loss = 0.0
    for i in range(k * N):
        l = 0.0
        for j in range(k):
            l += alpha[j] * multivariate_normal.pdf(x=X[:, i], cov=cov[j], mean=mu[j])
        loss += np.log(l)
    return loss


def GMM(X, k, N, t, eplison):
    C = []
    mu = []
    cov = []
    alpha = np.zeros(k)
    y_ik = np.zeros((k * N, k))
    # 随机初始化变量
    for i in range(k):
        C.append([])
        alpha[i] = 1.0 / k
        number = random.randint(i * N, (i + 1) * N)
        mu.append(X[:, number])
        cov.append(0.1 * k * np.eye(t))
    # 计算初始的损失函数
    loss_0 = loss(X, cov, mu, alpha, k, N)
    while True:
        # 计算每个样本的高斯混合分布成分的后验概率
        for i in range(k * N):
            a = 0.0
            for j in range(k):
                a += alpha[j] * multivariate_normal.pdf(x=X[:, i], cov=cov[j], mean=mu[j])
            for j in range(k):
                y_ik[i][j] = alpha[j] * multivariate_normal.pdf(x=X[:, i], cov=cov[j], mean=mu[j]) / a
        # 更新参数mu和cov,alpha
        for i in range(k):
            mn = 0.0
            mu_zi = np.zeros(t)
            cov_zi = np.zeros((t, t))
            for j in range(k * N):
                mn += y_ik[j][i]
                mu_zi += y_ik[j][i] * X[:, j]
                tt = (X[:, j] - mu[i]).reshape(-1, 1)
                cov_zi += y_ik[j][i] * tt.dot(tt.T)
            alpha[i] = mn / (k * N)
            mu[i] = mu_zi / mn
            cov[i] = cov_zi / mn
        # 计算损失函数
        loss_1 = loss(X, cov, mu, alpha, k, N)
        # 当达到所需的精度时或损失函数变大时退出循环
        if loss_0 - loss_1 < eplison or loss_0 < loss_1:
            # 计算归属类
            for i in range(N * k):
                maxx = 0.0
                lent = 0
                for j in range(k):
                    if maxx < y_ik[i][j]:
                        maxx = y_ik[i][j]
                        lent = j
                C[lent].append(i)
            # 计算簇中心
            m = np.zeros((t, k))
            for i in range(k):
                for j in C[i]:
                    m[:, i] += X[:, j]
                m[:, i] /= len(C[i])
            return C, m, loss_1
        else:
            loss_0 = loss_1
