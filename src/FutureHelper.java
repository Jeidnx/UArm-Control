import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A helper class when working with future and time
 */
public class FutureHelper {

    /**
     * Blocks the current thread for {@code millis} milliseconds
     * @param millis the milliseconds to block for
     */
    public static void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for the given future to complete and then returns its result.
     * @return the result of the given future
     */
    public static <T> T get(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
