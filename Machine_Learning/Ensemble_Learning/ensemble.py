
# coding: utf-8

# In[4]:


import numpy as np
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import VotingClassifier
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
import warnings

warnings.filterwarnings('ignore')


# In[ ]:


from scipy.io import loadmat
from scipy import stats
import matplotlib.pyplot as plt

# load matrix data
mnist = loadmat("mnist-original.mat")
mnist_data = mnist["data"].T  # transpose the matrix
mnist_label = mnist["label"][0]

pd_data = pd.DataFrame(mnist["data"].T) # type conversion to DataFrame for data exploration
pd_label = pd.DataFrame(mnist["label"][0])

# Data exploration
pd_data.describe()
pd_label.describe()

plt.boxplot(mnist_label)
plt.show()


# In[6]:


# Split Data set

# Reduce the size of the dataset. 70000 -> 7000
X_train, data, y_train, label = train_test_split(mnist_data, mnist_label, test_size=0.1, random_state=1)

# Split Data set into D1 and D2/3
# X_train & y_train for D1(train)
X_train, X_test, y_train, y_test= train_test_split(data, label, test_size=0.2, random_state=0)

# Split Data set into D2 and D3
# testC_x & testC_y for testing each classifier / testE_x & testE_y for testing ensemble learning
testC_x, testE_x, testC_y, testE_y = train_test_split(X_test, y_test, test_size= 0.5)


# In[8]:


# Build three classifier models using D1 and D2
# For each model, find the parameter combination with the high score

from sklearn.model_selection import GridSearchCV

# parameter list for finding best parameter combination
svmL_param_grid = {'kernel' : ['linear'],
                 'gamma' : [10, 100],
                 'C' : [0.1, 1.0, 10.0]}

svmR_param_grid = {'kernel' : ['rbf'],
                 'gamma' : [10, 100],
                 'C' : [0.1, 1.0, 10.0]}

logistic_param_grid = {'solver' : ['liblinear', 'lbfgs', 'sag'],
                      'max_iter' : [50, 100, 200]}

# Build three classifier
logistic = LogisticRegression()
svm_linear = SVC(kernel = 'linear')
svm_rbf = SVC(kernel = 'rbf')

# Training each classifier using D1
logistic.fit(X_train, y_train)
svm_linear.fit(X_train,y_train)
svm_rbf.fit(X_train, y_train)

# Bulid GridSearchCV which find the parameter combination with the highest score
grid_logistic = GridSearchCV(logistic, param_grid = logistic_param_grid, scoring='accuracy', n_jobs = 4, cv=10)
grid_svmL = GridSearchCV(svm_linear, param_grid = svmL_param_grid, scoring='accuracy', n_jobs = 4, cv=10)
grid_svmR = GridSearchCV(svm_rbf, param_grid = svmR_param_grid, scoring='accuracy', n_jobs = 4, cv=10)

# Train GridSearchCV using D2
grid_logistic.fit(testC_x, testC_y)
grid_svmL.fit(testC_x, testC_y)
grid_svmR.fit(testC_x, testC_y)

# The highest score and the best parameter combination
print("\n* SVM - linear *")
print("Best Parameter :", grid_svmL.best_estimator_) # return the best combination parameter
print("High Score :", grid_svmL.best_score_) # return the best parameter with the highest score

print("\n* SVM - rbf *")
print("Best Parameter :", grid_svmR.best_estimator_)
print("High Score :", grid_svmR.best_score_)

print("\n* Logistic Regression *")
print("Best Parameter :", grid_logistic.best_estimator_)
print("High Score :", grid_logistic.best_score_)


# In[9]:


# Find a classifier with the highest score
# SVM - linear (0), SVM - rbf (1), Logistic Regression (2)
score = []
score.append(grid_svmL.best_score_)
score.append(grid_svmR.best_score_)
score.append(grid_logistic.best_score_)
max_clf = score.index(max(score)) # return order of a classifier which has the highest score
print(max_clf) 


# In[11]:


# Ensemble Learning

from collections import Counter

# Predict result using D3(for ensemble)
predict_logistic = logistic.predict(testE_x)
predict_svmL = svm_linear.predict(testE_x)
predict_svmR = svm_rbf.predict(testE_x)


predict_result = []

# predictions with the highest frequency will be final result.
for index in range(len(predict_logistic)) :
    predict_total = [predict_svmL[index], predict_svmR[index], predict_logistic[index]]
    cnt = Counter(predict_total)
    if cnt.most_common(1)[0][1] == 1 : # if all classifiers have different predictions
        predict_result.append(cnt.most_common(3)[max_clf][0]) # prediction which a classifier(the highest score) has will be final result
    else : 
        predict_result.append(cnt.most_common(1)[0][0]) # prediction with the highest frequency will be final result.

# Ensemble Learning Score
ensemble_accuracy = np.mean(np.equal(testE_y, predict_result))
print("Ensemble Accuracy : ", ensemble_accuracy)


# In[12]:


# Confusion Matrix

import seaborn as sn

# For SVM - linear

# make confusion matrix with margin
confusion_svmL = pd.crosstab(testE_y, predict_svmL,rownames=['Actual'], colnames = ['Predicted'], margins = True)

# show confusion matrix using heatmap
sn.heatmap(confusion_svmL, annot= True)


# In[13]:


# For SVM - rbf
confusion_svmR = pd.crosstab(testE_y, predict_svmR,rownames=['Actual'], colnames = ['Predicted'], margins = True)
sn.heatmap(confusion_svmR, annot= True)


# In[14]:


# For Logistic Regression
confusion_logistic = pd.crosstab(testE_y, predict_logistic,rownames=['Actual'], colnames = ['Predicted'], margins = True)
sn.heatmap(confusion_logistic, annot= True)


# In[15]:


# For Ensemble

predict_result = np.array(predict_result) # type conversion list -> numpy array for making confusion matrix

confusionE = pd.crosstab(testE_y, predict_result,rownames=['Actual'], colnames = ['Predicted'], margins = True)
sn.heatmap(confusionE, annot= True)

