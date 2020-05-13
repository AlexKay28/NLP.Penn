from __future__ import print_function
from hyperas import optim
from hyperas.distributions import choice, uniform
from hyperopt import Trials, STATUS_OK, tpe

import numpy as np
import pandas as pd
from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences

from keras.layers import Input, Embedding, Activation, Flatten, Dense
from keras.layers import Conv1D, MaxPooling1D, Dropout
from keras.models import Model, Sequential

from keras.callbacks import EarlyStopping, ModelCheckpoint
from keras.utils import to_categorical
from sklearn.utils import shuffle

from hyperas.distributions import choice, uniform


# In[2]:


def data():
    Train = pd.read_csv('../train_data_2.csv')[:400]
    Validation = pd.read_csv('../validation_data_2.csv')[:100]
    Test = pd.read_csv('../test_data_2.csv')[:100]

    Train = pd.concat([Train, Validation])

    X_train, y_train = Train['tweet'], to_categorical(Train['class'])
    X_test, y_test   = Test['tweet'], to_categorical(Test['class'])

    train_texts = X_train.values
    train_texts = [s.lower() for s in train_texts]

    test_texts = X_test.values
    test_texts = [s.lower() for s in test_texts]

    alphabet = "abcdefghijklmnopqrstuvwxyz0123456789,;.!?:'\"/\\|_@#$%^&*~`+-=<>()[]{}"
    char_dict = {}
    for i, char in enumerate(alphabet):
        char_dict[char] = i + 1

    tk = Tokenizer(num_words=None, char_level=True, oov_token='UNK')
    tk.fit_on_texts(train_texts)
    tk.word_index = char_dict.copy()
    tk.word_index[tk.oov_token] = max(char_dict.values()) + 1

    train_sequences = tk.texts_to_sequences(train_texts)
    test_sequences  = tk.texts_to_sequences(test_texts)

    # Padding
    train_data = pad_sequences(train_sequences, maxlen=200, padding='post')
    test_data = pad_sequences(test_sequences, maxlen=200, padding='post')

    # Convert to numpy array
    x_train = np.array(train_data, dtype='float32')
    x_test  = np.array(test_data,  dtype='float32')

    char_vocab = len(tk.word_index)
    embedding_weights = []
    embedding_weights.append(np.zeros(char_vocab))

    for char, i in tk.word_index.items():
        onehot = np.zeros(char_vocab)
        onehot[i-1] = 1
        embedding_weights.append(onehot)
    embedding_weights = np.array(embedding_weights)

    return x_train, y_train, x_test, y_test, embedding_weights


# In[3]:


def create_model(x_train, y_train, x_test, y_test, embedding_weights):

    input_size = x_train.shape[1]
    embedding_size = embedding_weights.shape[1]

    conv_layers = [[256,7, 3],
                   [256,7, 3],
                   [256,7, -1],
                   [256,7, 3]] #filter_num, filter_size, pooling_size

    fully_connected_layers = [200, 200]
    num_of_classes = 2
    dropout_p = 0.5

    embedding_layer = Embedding(char_vocab+1,
                           embedding_size,
                           input_length=input_size,
                           weights=[embedding_weights])

    inputs = Input(shape=(input_size,), name='input', dtype='int64')
    x = embedding_layer(inputs)
    for filter_num, filter_size, pooling_size in conv_layers:
        x = Conv1D(filter_num, filter_size)(x)
        x = Activation('relu')(x)
        if pooling_size != -1:
            x = MaxPooling1D(pool_size=pooling_size)(x)
    x = Flatten()(x)
    x = Dense({{choice([256, 512, 1024])}}, activation={{choice(['relu', 'sigmoid'])}})(x)
    x = Dropout({{uniform(0, 1)}})(x)
    x = Dense({{choice([256, 512, 1024])}}, activation={{choice(['relu', 'sigmoid'])}})(x)
    x = Dropout({{uniform(0, 1)}})(x)
    predictions = Dense(num_of_classes, activation='softmax')(x)
    model = Model(inputs=inputs, outputs=predictions)
    model.compile(optimizer={{choice(['rmsprop', 'adam', 'sgd'])}},
                  loss='binary_crossentropy',
                  metrics=['accuracy'])

    model.summary()
    result = model.fit(x_train, y_train,
              batch_size={{choice([32, 64, 128])}},
              epochs=5,
              verbose=1,
              validation_split=0.1)
    #get the highest validation accuracy of the training epochs
    validation_acc = np.amax(result.history['val_accuracy'])
    print('Best validation acc of epoch:', validation_acc)
    return {'loss': -validation_acc, 'status': STATUS_OK, 'model': model}


# In[4]:


best_run, best_model = optim.minimize(model=create_model,
                                      data=data,
                                      algo=tpe.suggest,
                                      max_evals=2,
                                      trials=Trials(),
                                      notebook_name='CNN_charLvl_hyperopt')
X_train, y_train, X_test, y_test, embedding_weights = data()
print("Evalutation of best performing model:")
print(best_model.evaluate(X_test, y_test))
print("Best performing model chosen hyper-parameters:")
print(best_run)
