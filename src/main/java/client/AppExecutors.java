package client;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * It is normally used instead of explicitly creating threads for each of a set of tasks.
 * Using a singleton class ensures that we have only one instance of our executor object which is just beautiful, when you consider memory resources on android
 *
 * @see <a href="https://github.com/android/architecture-components-samples/blob/main/BasicSample/app/src/main/java/com/example/android/persistence/AppExecutors.java">...</a>
 */
public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    /**
     * A private constructor that instantiate the class and set attributes
     * Can be accessed only the within the class (for singleton design pattern)
     *
     * @param diskIO Executor for disk I/O threads (e.g., save or read files, query local database)
     * @param networkIO Executor for network I/O threads (e.g., communicate with the internet)
     * @param mainThread Executor for the main thread (e.g., change text on the user interface)
     */
    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    /**
     * A static function that serves as a getter for the class instance
     *
     * @return The singleton class instance
     */
    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    /**
     * Getter for disk thread executor
     * @return disk IO thread executor
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * Getter for main thread executor
     * @return main thread executor
     */
    public Executor mainThread() {
        return mainThread;
    }

    /**
     * Getter for network thread executor
     * @return network IO thread executor
     */
    public Executor networkIO() {
        return networkIO;
    }

    /**
     * A subclass that links main thread executor a main looper of the Android application
     */
    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}