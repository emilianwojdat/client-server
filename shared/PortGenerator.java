package shared;

import java.util.Random;

public final class PortGenerator {

    public static final int generate(int min, int max) {
        return generator.nextInt(max - min + 1) + min;
    }

    private static final Random generator = new Random();
}
