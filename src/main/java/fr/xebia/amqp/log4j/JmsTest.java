import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class JmsTest {

	private static Logger logger = Logger.getLogger(JmsTest.class);
	private static final int NB_THREAD = 1;
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(NB_THREAD);
	private static int LOOP_SIZE = 20000;
	private static int callNumber = 0;
	private static long topDepart, topFinal;
	private static List<Future<?>> futures = new ArrayList<Future<?>>();

	public void newThreadLogger() {
		futures.add(executorService.submit(new ThreadLogger(callNumber++)));
	}

	public static void main(String[] args) {
		topDepart = System.currentTimeMillis();
		logger.info("C'est par ici que ça se passe. " + topDepart);
		JmsTest loggerTest = new JmsTest();
		for (int i = 0; i < NB_THREAD; i++) {
			loggerTest.newThreadLogger();
		}
		for (int i = 0; i < futures.size(); i++) {
			try {
				futures.get(i).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		topFinal = System.currentTimeMillis();
		System.out.println("STOP!!! ");
		long totalTime = topFinal - topDepart;
		System.out.println("Hé bé, ça a pris " + totalTime + " ms tout de même");
		System.exit(1);

	}

	private class ThreadLogger implements Callable<Integer> {
		private Logger loggerThread = Logger.getLogger(ThreadLogger.class);
		private int id;

		public ThreadLogger(int id) {
			super();
			this.id = id;
		}

		@Override
		public Integer call() throws Exception {
			for (int i = 0; i < LOOP_SIZE; i++) {
				loggerThread
						.info(id
								+ "=l'infini c'est long, surtout vers la fin (W. Allen), "
								+ i);
			}
			return id;
		}
	}
}
