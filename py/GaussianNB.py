import numpy as np

X = np.array([[-1, -1], [-2, -1], [-3, -2], [1, 1], [2, 1], [3, 2]])
Y = np.array([1, 1, 1, 2, 2, 2])

from sklearn.naive_bayes import GaussianNB

clf = GaussianNB()

clf.fit(X, Y)

print(clf.predict([[-0.8, -1]]))

clf_pf = GaussianNB()

clf_pf.partial_fit(X, Y, np.unique(Y))

print(clf_pf.predict([[-0.8, -1]]))


from sklearn.svm import SVC

slf = SVN(kernel = "linear")

A = [[0,0], [1,1]]
b =  [0,1]

clf = svm.SVC()

clf.fit(A,b)

clf.predict([[2., 2.]])




