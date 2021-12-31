import random

import numpy as np


def distance(v, w):
    return np.linalg.norm(v - w)


def k_means(X, k, t, N, eplison):
    m = np.zeros((t, k))
    C = []
    for i in range(k):
        number = random.randint(i * N, (i + 1) * N)
        m[:, i] = X[:, number]
        C.append([number])
    # 损失函数，所有点距离相应的簇中心距离和的最小值
    loss_0 = 0.0
    # 计算每个点距离初始中心的距离
    for i in range(k * N):
        dis = 10000
        lent = 0
        for j in range(k):
            if dis > distance(X[:, i], m[:, j]):
                dis = distance(X[:, i], m[:, j])
                lent = j
        # 将距离这个点划分到距离最近的中心所在的类
        # 先将这个点从原类中删除
        for j in range(k):
            flag = False
            for l in C[j]:
                if (i == l):
                    flag = True
            if flag == True:
                C[j].remove(i)
        C[lent].append(i)
        loss_0 += distance(X[:, i], m[:, lent])
    # 更新簇中心
    for i in range(k):
        m[:, i] = np.zeros(t)
        for j in C[i]:
            m[:, i] += X[:, j]
        m[0, i] /= len(C[i])
        m[1, i] /= len(C[i])
    while True:
        # 损失函数，所有点距离相应的簇中心距离和的最小值
        loss = 0.0
        # 计算每个点距离初始中心的距离
        for i in range(k * N):
            dis = 10000.0
            lent = 0
            for j in range(k):
                if dis > distance(X[:, i], m[:, j]):
                    dis = distance(X[:, i], m[:, j])
                    lent = j
            # 将距离这个点划分到距离最近的中心所在的类
            # 先将这个点从原类中删除
            for j in range(k):
                flag = False
                for l in C[j]:
                    if (i == l):
                        flag = True
                if flag == True:
                    C[j].remove(i)
            # 将这个点添加到新类中
            C[lent].append(i)
            loss += distance(X[:, i], m[:, lent])
        # 当达到所需的精度时或损失函数变大时退出循环
        if loss_0 - loss < eplison or loss_0 < loss:
            return C, m
        else:
            loss_0 = loss
        # 更新簇中心
        for i in range(k):
            m[:, i] = np.zeros(t)
            for j in C[i]:
                m[:, i] += X[:, j]
            m[0, i] /= len(C[i])
            m[1, i] /= len(C[i])
