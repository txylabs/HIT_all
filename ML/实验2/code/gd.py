import numpy as np

def data_produce(size1,size0,mean1,mean0,cov1,cov0):
    x_train=np.zeros((size1+size0,2))
    y=np.zeros(size1+size0)
    x_train[:size1,:]=np.random.multivariate_normal(mean1,cov1,size=size1)
    x_train[size1:, :] = np.random.multivariate_normal(mean0,cov0,size=size0)
    y[:size1]=1
    X = np.ones((size1+size0,3))
    # 构造X矩阵，第一维都设置成1，方便与w相乘
    X[:,1:3] = x_train
    return X,x_train,y

def loss(X, y, w):
    lw = 0.0
    for i in range(len(y)):
        t=w.T.dot(X[i])
        if t<=0:
            lw += y[i] * t - np.log(1 + np.exp(t))
        else:
            lw += y[i] * t + np.log(np.exp(-t)/(1 + np.exp2(-t))+1e-3)
    lw = -lw/len(y)
    return lw


def daoshu(X, y, w):
    dao = 0.0
    for i in range(len(y)):
        t=w.T.dot(X[i])
        dao += y[i] * X[i] - X[i] / (1 + np.exp(t))
    dao = dao/(len(y))
    return dao

def gradient_descent(X, y, lamda, deta, epsilon, times):
    w = np.ones(X.shape[1])
    lw_0 = loss(X, y, w)
    for i in range(times):
        w0=w
        w = w + lamda * deta * w + deta*daoshu(X, y, w)
        lw = loss(X, y, w)
        if lw > lw_0:
            deta = 0.5 * deta
            w=w0
        elif lw_0 - lw < epsilon:
            print(i)
            break
        else:
            lw_0 = lw
    return w
def prediction(x,w):
    t=w.T.dot(x)
    if t<=0:
        p0=1.0/(1+np.exp(t))
    else:
        p0 = np.exp(-t) / (1 + np.exp(-t))
    if p0<0.5:return 1
    else: return 0
def accuary(X,w,y):
    sum=0.0
    for i in range(len(y)):
        pre=prediction(X[i],w)
        if pre==y[i]:
            sum+=1
    return sum/len(y)


def Conjugate_gradient(X, y_train, epsilon,lamda):
    # 共轭梯度法
    # 计算A,b
    w = np.ones(X.shape[1])
    #求黑塞矩阵
    a = []
    aa=[]
    for i in range(len(y_train)):
        tt=1.0/(1+np.exp(-w.T.dot(X[i])))
        a.append(tt)
        aa.append(1-tt)
    a=np.array(a).reshape(1,-1)
    aa=np.array(aa).reshape(-1,1)
    A=aa.dot(a)
    A=A.dot(np.eye(A.shape[0]))
    H=(X.T.dot(A).dot(X))+2*lamda*np.ones((X.shape[1]))
    b = X.T.dot(y_train)
    # 初始化w，r,p,将dkT*rk保留
    g=H.dot(w)-b
    d=-g
    r=d
    t=d.T.dot(r)
    # 循环，直到t不满足精度为止
    while t > epsilon:
        # 算法迭代
        alpha = t / d.T.dot(H).dot(d)
        w = w + alpha * d
        r = r - alpha * H.dot(d)
        beta = (r.T.dot(H)).dot(d) / d.T.dot(H).dot(d)
        d = r - beta * d
        t = d.T.dot(r)
    return w
def erjiedao(X, y, w):
    dao = 0.0
    for i in range(len(y)):
        t = w.T.dot(X[i])
        dao += X[i].dot(X[i].T)*(np.exp(t)/1+np.exp(t))*(1.0/1+np.exp(t))
    dao = dao/(len(y))
    return dao
def newton(X, y_train, epsilon,lamda,alpha):
    w = np.ones(X.shape[1])
    loss0=loss(X,y_train,w)
    while True:
        w_old=w.copy()
        w=w-alpha*(len(y_train)*daoshu(X,y_train,w)/erjiedao(X,y_train,w)+lamda*w)
        loss1=loss(X,y_train,w)
        if loss0<loss1:
            alpha=0.5*alpha
            w=w_old
        elif loss0-loss1<epsilon:
            return w



