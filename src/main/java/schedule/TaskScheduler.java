package schedule;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import asset_reader.BybitReader;
import asset_reader.SSIReader;

public class TaskScheduler {
	public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        Runnable task1 = () -> {
            try {
				BybitReader reader = new BybitReader("ngbao128@gmail.com");
				reader.run();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        };
        
        Runnable task2 = () -> {
            try {
				SSIReader reader = new SSIReader("ngbao128@gmail.com");
				reader.run();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        };

        // Desired start time: 2025-01-18 09:00:00
//        LocalDateTime targetTime = LocalDateTime.of(2025, 1, 18, 9, 0, 0);
        long initialDelay = 0;

        long interval = 300;    //(in seconds)
        
        scheduler.scheduleAtFixedRate(task1, initialDelay, interval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(task2, initialDelay, interval, TimeUnit.SECONDS);
    }
	
//    private static long calculateInitialDelay(LocalDateTime targetTime) {
//        ZonedDateTime now = ZonedDateTime.now();
//        ZonedDateTime target = targetTime.atZone(ZoneId.systemDefault());
//        long delayMillis = target.toInstant().toEpochMilli() - now.toInstant().toEpochMilli();
//        return Math.max(0, TimeUnit.MILLISECONDS.toSeconds(delayMillis));
//    }
}
