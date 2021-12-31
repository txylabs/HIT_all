import numpy as np
from PIL import Image
import data
X=data.faces("image1.png")
w,x,xmeans=data.PCA(X,100)
# w,x,xmeans=data.PCA(X,50)
# w,x,xmeans=data.PCA(X,10)
# w,x,xmeans=data.PCA(X,5)
#w,x,xmeans=data.PCA(X,1)
#得到重建的数据
Y=x@w.T@w+xmeans
y=Image.fromarray(Y).convert('RGB')
t=Image.fromarray(X).convert('RGB')
print(data.psnr(X,Y))
#t.save("gface01.png")
#y.save("gface02.png")
#y.save("gface03.png")
# y.save("gface04.png")
# y.save("gface05.png")
#y.save("gface07.png")
# print(t.show())
# print(y.show())