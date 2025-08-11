package tr.com.havelsan.limanpgtool.gateway.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserroleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Userrole getUserroleSample1() {
        return new Userrole().id(1L).role("role1");
    }

    public static Userrole getUserroleSample2() {
        return new Userrole().id(2L).role("role2");
    }

    public static Userrole getUserroleRandomSampleGenerator() {
        return new Userrole().id(longCount.incrementAndGet()).role(UUID.randomUUID().toString());
    }
}
