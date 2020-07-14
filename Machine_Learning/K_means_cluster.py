
# coding: utf-8

# In[57]:


import pandas as pd
import numpy as np
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import MinMaxScaler
import seaborn as sns
import matplotlib.pyplot as plt


# In[58]:


# Import data

# Load the train and test datasets
train_url = "http://s3.amazonaws.com/assets.datacamp.com/course/Kaggle/train.csv"
test_url = "http://s3.amazonaws.com/assets.datacamp.com/course/Kaggle/test.csv"

train = pd.read_csv(train_url)
test = pd.read_csv(test_url)

print(train.head())


# In[59]:


# Preprocessing

# Fill missing values with mean column values in the train data set
train.fillna(train.mean(),inplace=True)

# Fill missing values with mean column values in the test set
test.fillna(test.mean(), inplace=True)

# Feature Engineering

# Remove columns which not have any impact on the survival status of the passengers.
train = train.drop(['Name','Ticket','Cabin','Embarked'], axis = 1)
test = test.drop(['Name','Ticket','Cabin','Embarked'], axis = 1)

# Lavel Encoder
labelEncoder = LabelEncoder()
labelEncoder.fit(train['Sex'])
labelEncoder.fit(test['Sex'])
train['Sex'] = labelEncoder.transform(train['Sex'])
test['Sex'] = labelEncoder.transform(test['Sex'])


# In[66]:


# K-Means Clustering

X = np.array(train.drop(['Survived'], 1).astype(float))
y = np.array(train['Survived'])

# Cluster the passenger records into 'survived' or 'not survived'
kmeans = KMeans(n_clusters=2, algorithm='auto', max_iter=600, n_init=1)
# n_init, max_iter, n_jobs, algorithm

kmeans.fit(X)


# In[67]:


# Evaluate accuracy of this cluster

correct = 0
for i in range(len(X)) :
    predict_me = np.array(X[i].astype(float))
    predict_me = predict_me.reshape(-1,len(predict_me))
    prediction = kmeans.predict(predict_me)
    if prediction[0] == y[i] :
        correct += 1
print("Accuracy : ",correct/len(X))

