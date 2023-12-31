package org.open.solution.distributed.test.idempotent;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName IdempotenBlocktApp
 * @Deacription TODO
 * @Author 王浩
 * @Date 2023/6/16 23:10
 * @Version 1.0
 **/
@RestController
@RequiredArgsConstructor
public class IdempotenBlockTestApp {

    ExecutorService executor = Executors.newFixedThreadPool(10);

    @Autowired
    private IdempotentApp idempotentApp;

    @PostMapping("/idempotent/test")
    public String idempotentBlocks(@RequestBody UiIdempotent uiIdempotent) throws InterruptedException {
        AtomicInteger success = new AtomicInteger();

        AtomicInteger fail = new AtomicInteger();
        for (int i = 0; i < 100000; i++) {
            Runnable worker = () -> {
                try {
                    idempotentApp.idempotentState(uiIdempotent);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    fail.incrementAndGet();
                }
            };
            executor.execute(worker);
        }

        Thread.sleep(120 * 1000);

        return "success: " + success.get() + " ,fail: " + fail.get();
    }
}
