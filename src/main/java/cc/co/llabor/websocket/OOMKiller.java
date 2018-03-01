package cc.co.llabor.websocket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;

public class OOMKiller {

	static int allocated = 0;
	static int toAllocate = 1; // Mb
	
	public static void main(String[] args) {
        initWatchDog();
        
        tryToDoSomeDangerous();
 
	}

	private static void tryToDoSomeDangerous() {
		String mystuff ="";
		for (int i = 0; i< 1000000 ;i++) {
			
			mystuff += new String (new byte[toAllocate * 1000*1000]);
			allocated += toAllocate;
			toAllocate *=2;
			System.out.println("LEN:"+mystuff.length());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	private static void initWatchDog() {
		ThreadFactory myFactory= new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r,"The OOM watchDog is running here...");
				t.setPriority(Thread.MAX_PRIORITY);
				return t;
			}
		}; 
		final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(myFactory);
        
        ses.scheduleWithFixedDelay(new Runnable() {
     	
            private String myBuff;

			@Override
            public void run() {
            	try {
            		allocTOALLOCMb();
            		System.err.println("check alloc "+toAllocate+"Mb is done.");
            	}catch (OutOfMemoryError e) {
					e.printStackTrace();
					shutDown();
				}
            }

			private void shutDown() {
				System.err.println("this is the latest output from me... C. U. Done:"+allocated+"Mb");
				System.exit(-1);
				
			}

			private void allocTOALLOCMb() {
				setMyBuff(new String (new byte[toAllocate*1000*1000]));
				
			}

			public String getMyBuff() {
				return myBuff;
			}

			public void setMyBuff(String myBuff) {
				this.myBuff = myBuff;
			}
        }, 0, 13, TimeUnit.SECONDS ); //1, TimeUnit.MINUTES);
	}
        
	
}
