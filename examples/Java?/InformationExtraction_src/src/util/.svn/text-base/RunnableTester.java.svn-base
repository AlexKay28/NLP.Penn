package util;

  // Fig. 23.5: RunnableTester.java
  // Multiple threads printing at different intervals.
  import java.util.concurrent.Executors;
  import java.util.concurrent.ExecutorService;
  import java.util.Random;
  
  
  class PrintTask implements Runnable
    {
       private int sleepTime; // random sleep time for thread
       private String threadName; // name of thread
       private static Random generator = new Random();
       
      // assign name to thread
      public PrintTask( String name )
      {
         threadName = name; // set name of thread
   
         // pick random sleep time between 0 and 5 seconds
         sleepTime = generator.nextInt( 5000 );
      } // end PrintTask constructor
   
      // method run is the code to be executed by new thread
      public void run()                                     
      {
         try // put thread to sleep for sleepTime amount of time
         {
            System.out.printf( "%s going to sleep for %d milliseconds.\n",
               threadName, sleepTime );
               
            Thread.sleep( sleepTime ); // put thread to sleep
         } // end try
         // if thread interrupted while sleeping, print stack trace
         catch ( InterruptedException exception )
         {
            exception.printStackTrace();
         } // end catch
   
         // print thread name
         System.out.printf( "%s done sleeping\n", threadName );
      } // end method run
   } // end class PrintTask<
  
  
  
  
  
  
  
  
  
  
  
  public class RunnableTester
  {
     public static void main( String[] args )
     {
        // create and name each runnable             
        PrintTask task1 = new PrintTask( "thread1" );
        PrintTask task2 = new PrintTask( "thread2" );
        PrintTask task3 = new PrintTask( "thread3" );
          
        System.out.println( "Starting threads" );
          
        // create ExecutorService to manage threads                        
        ExecutorService threadExecutor = Executors.newFixedThreadPool( 3 );
  
        // start threads and place in runnable state   
        
        threadExecutor.execute( task1 ); // start task1
        threadExecutor.execute( task2 ); // start task2
        threadExecutor.execute( task3 ); // start task3
  
        threadExecutor.shutdown(); // shutdown worker threads
          
        System.out.println( "Threads started, main ends\n" );
     } // end main
 } // end class RunnableTester