import numpy as np
import keras

class DataGenerator(keras.utils.Sequence):
    def __init__(self, seq, labels, batch_size=32, dim=33, n_classes=1, shuffle=True):
        'Initialization'
        self.dim        = dim
        self.batch_size = batch_size
        self.labels     = labels
        # self.list_IDs = list_IDs
        self.list_IDs   = [index for index in range(len(seq)-1)]
        self.data       = seq
        self.n_classes  = n_classes
        self.shuffle    = shuffle
        self.on_epoch_end()

    def on_epoch_end(self):
        'Updates indexes after each epoch'
        self.indexes = np.arange(len(self.list_IDs))
        if self.shuffle == True:
            np.random.shuffle(self.indexes)

    def __data_generation(self, list_IDs_temp):
        'Generates data containing batch_size samples' # X : (n_samples, *dim, n_channels)
        # Initialization
        X = np.empty((self.batch_size, self.dim))
        y = np.empty(self.batch_size, dtype=int)
        # Generate data
        for i, ID in enumerate(list_IDs_temp):
            # Store sample
            #X[i,] = np.load('data/' + ID + '.npy')
            X[i,] = np.array(self.data[ID])

            # Store class
            y[i] = self.labels[ID]
        if self.n_classes > 1:
            return X, keras.utils.to_categorical(y, num_classes=self.n_classes)
        else:
            return X, y


    def __len__(self):
        'Denotes the number of batches per epoch'
        return int(np.floor(len(self.list_IDs) / self.batch_size))

    def __getitem__(self, index):
        'Generate one batch of data'
        # Generate indexes of the batch
        indexes = self.indexes[index*self.batch_size:(index+1)*self.batch_size]

        # Find list of IDs
        list_IDs_temp = [self.list_IDs[k] for k in indexes]
        #print("list_IDs_temp: ", list_IDs_temp)
        # Generate data
        X, y = self.__data_generation(list_IDs_temp)
        #print(X)
        return X, y