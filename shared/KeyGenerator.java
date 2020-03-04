package shared;

import java.util.Random;

public final class KeyGenerator {

    public static final String generate() {
        return String.valueOf(generator.nextLong());
    }

    private static final Random generator = new Random();
}
