/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args) throws Exception {
//        ThreadPoolExecutor executor =
//                (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
//        executor.submit(() -> {
//            Thread.sleep(1000);
//            System.out.println("Slept for 1000 1");
//            return null;
//        });
//        executor.submit(() -> {
//            Thread.sleep(1000);
//            System.out.println("Slept for 1000 2");
//            return null;
//        });
//        executor.submit(() -> {
//            Thread.sleep(1000);
//            System.out.println("Slept for 1000 3");
//            return null;
//        });


        ConsoleMenu menu = new ConsoleMenu();
        menu.run();
    }
}
