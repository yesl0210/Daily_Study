
# coding: utf-8

# In[111]:


import numpy as np
import pandas as pd
from collections import Counter
import random
import copy
import warnings

warnings.filterwarnings('ignore')


# In[112]:


# mushrooms.csv
df = pd.read_csv('mushrooms.csv') #8124

# Split data and class
classes = df['class']
df = df.drop(columns=['class'])
columns = df.columns # columns' name


# In[113]:


class KModes:
    cluster = [] # container for each cluster
    mode = [] # Center Mode for each cluster
    labels = [] # Labels for complete cluster
    purity = 0 # measurement for clustering' results

    def __init__(self, k):
        self.k = k # the number of clusters
    
    def predict(self,df, columns) :
        
        # [ 1 - Set Initial Center Mode ] #
        
        random_mode = [] # mode vector when iteration = 1
        mode = [] # mode vector when iteration != 1
        
        # 1-1) Select randomly n data as initial center mode
        i = 0
        while i < self.k :
            number = random.randint(0, len(df)-1) # get random index
            if(number in random_mode) :
                continue
            else :
                random_mode.append(number) # append data to initial center mode set
            i +=1
        print("mode: ", random_mode)

        # 1-2) Get attribute value for initial center mode.
        for mode_n in range(self.k) :
            mode_value = []
            for col in columns :
                mode_value.append(df.loc[random_mode[mode_n],col])
            mode.append(mode_value)
        print(mode)

        # 1-3) Create Cluster List
        cluster = [] # Total Cluster Set

        for n_cluster in range(self.k) : # Create k cluster (by user input)
            list = []
            total = []
            list.append(random_mode[n_cluster])
            cluster.append(list)
            
        # [ 2 - K-Mode Itertation ] #
        
        pre_totalCost = 0 # Previous total Cost
        totalCost = 0 # Current total Cost
        iteration = 1 # The number of iteration 
        pre_cluster = [] # Previous Cluster Set

        while (1) : # K-Mode Iteration
            print("******* Iteration ",iteration,"*******\n")
            
            for i in range(len(df)) : # for all data
                print("** i =",i," **")
                
                # 2-1) Define and initialize distance set
                distance = [] # Calculated distance set between current data and each mode vector
                for init_distance in range(self.k) : # Initialize distance for each mode to 0
                    distance.append(0)

                # When iteration = 1 and current data is selected as center mode
                # -> don't calculate distance
                if iteration == 1 and i in random_mode : 
                    continue

                # 2-2) Calculate distance for all mode vector
                for index_mode in range(len(mode)) :
                    mode_col = 0 # index for columns of mode vectors
                    
                    # Check whether two string is same for each columns
                    for col in columns :
                        if(df.loc[i,col] != mode[index_mode][mode_col]) : # If differ
                            distance[index_mode] += 1
                        else : # If same                            
                            # There are data with this attribute value in this cluster
                            if df.loc[i,col] in df.loc[cluster[index_mode],col] :
                                distance[index_mode] += 1 - ((df.loc[cluster[index_mode],col].count(df.loc[i,col])+1)/(len(cluster[index_mode])+1))
                            else : # There are not data with this attribute value in this cluster
                                distance[index_mode] += 1 - (1/(len(cluster[index_mode])+1))                        
                        mode_col += 1

                # 2-3) Find a Cluster with the shortest distance.
                short = np.min(distance) # Minimum distance out of calculated distance set
                totalCost += short # Add cost about this data to total cost
                short_mode = distance.index(short) # Find the cluster with the shortest distance
                print("distance : ",distance)
                
                # 2-4) Assign data to Cluster
                if(iteration == 1) : # when iteration = 1
                    cluster[short_mode].append(i) # Assign index of current data to the cluster found above.
                else : # when iteration != 1
                    if i not in cluster[short_mode] : # If index of current data isn't in the cluster found above.
                        for find_cluster in range(len(mode)) : # Find the cluster current data belong to
                            if(i in cluster[find_cluster]) : # cluster with current data
                                cluster[find_cluster].remove(i) # remove index of current data from previous cluster
                                cluster[short_mode].append(i) # assign this to newly founded cluster
                print("short_mode = ",short_mode)
                print("cluster[short_mode]= ",cluster[short_mode],"\n\n")
                print("cluster", cluster, "\n\n\n")


            # End Creating Cluster for this iteration
            
            # 2-5) compare total Cost between previous cost and current cost
            print("[ iteration ",iteration,"] pre_totalCost = ", pre_totalCost, "/ totalCost = ", totalCost,"\n\n\n\n")
            if(iteration != 1) : # when iteration is not first
                if(pre_totalCost <= totalCost) : # If the total cost hasn't been reduced -> use previous cluster
                    cluster = copy.deepcopy(pre_cluster) # copy previous cluster
                    self.cluster = copy.deepcopy(cluster)
                    self.mode = copy.deepcopy(mode)
                    break; 

            # If iteration is first or total Cost decrease
            iteration += 1
            pre_totalCost = totalCost 
            pre_cluster=copy.deepcopy(cluster)
            totalCost = 0 # initialize totalCost

            # 2-6) Select Mode Vector by using frequent attribute value in each cluster
            for index_mode in range(len(mode)) :
                mode[index_mode].clear()
                for col in columns :
                    # get frequent value for each attribute
                    frequent = Counter(df.loc[cluster[index_mode],col]).most_common(1)[0][0]
                    mode[index_mode].append(frequent)
            print("Change Mode : ", mode)

        # When clustering doen, make labels for complete cluster
        labels = []
        for i in range(len(df)) : 
            for index_mode in range(len(mode)) : 
                if i in cluster[index_mode] : # if data is in this cluster
                    labels.append(index_mode)
        self.labels = copy.deepcopy(labels)       
        
    # Measure and return purity
    # df - input data set used to predict cluster
    # classes - real class of data
    # return self.purity - measurement for clustering's result
    def score(self,df, classes) : # measure purity
        self.purity = self.purityF(self.labels, classes)
        return self.purity
        
    # purity function
    def purityF(self, clusters, classes):
        cm = np.array(pd.crosstab(clusters, classes))
        return np.sum(np.amax(cm, axis=1)) / np.sum(cm)


# In[ ]:


clustering = KModes(3) # create class KModes (k=2)
clustering.predict(df,columns)


# In[99]:


print("* Cluster: ",clustering.cluster)
print("\n* Mode: ",clustering.mode)
print("\n* Labels: ",clustering.labels)
print("\n* Purity: ",clustering.score(df,classes))

