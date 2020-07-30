# NLP.Penn. #SMM4H Competition! 
Collection of works for "Social Media Mining for Health Applications (#SMM4H) Shared Task 2020" competition

UPENN: https://www.upenn.edu/
competition link: https://healthlanguageprocessing.org/smm4h-sharedtask-2020/

List of models which was constructed:
Embedding vecs was used from http://vectors.nlpl.eu/explore/embeddings/en/models/.

1. Classic ML models: (was fitted on unbalanced dataset. probably it was bad decision... look at FastText + SVM model.)

  1.1 TFIDF + SVM. F1-score: 64.328%; std: 6.011;
  
  1.2 TFIDF + MLP. F1-score: 65.988%; std: 5.821;
  
  1.3 TFIDF + GBM. F1-score: 57.575%; std: 3.853;
  
  1.4 FastText + SVM. F1-score: 84%. I guess, model decided to mark all sequences as Negative ('0'). 
  
  1.5 TFIDF + SVM (balanced dataset 3). F1-score: 74.321%
  
  1.6 TFIDF + TPOT (balanced dataset 3). F1-score: 75.177
  
  ! TPOT - terrific AutoML tool ! http://epistasislab.github.io/tpot/
  
2. Seq2one ANN models: (dataset was balanced as 3000/2500 -> neg/pos tweets)

  2.1 LSTM Emb-freezed (FastText vecs). F1-score: 0.73; Confusion matrix test: 114/32 49/105
  
  2.2 LSTM. F1-score: 0.74; Confusion matrix: 111/39 39/111
  
  2.3 LSTM (double input) Emb-freezed, Emb. F1-score: 0.73; 116/37 43/104
  
  2.4 CNN char level. F1-score: 0.42
  
  2.5 CNN word level. F1-score: 0.71
  
  2.6 LSTM (double emb. as in 2.3) + CNN. F1-score: 0.77 (0.79 extended data)
  
3. Tensorflow models.
  models was constructed one by one. Each of them is modification of previous one.
  I won't describe features from each of them, everything is in directory 'models tensorflow'
  Also was used Tensorboard for visualisation of training process.
  Sometimes calculations were done on Google cloud by means of using CPU as well as GPU and TPU!
