package CipherData;

public class CipherKeyStore {
    private static volatile String result;

    private CipherKeyStore() {
    }

    public static void set(String r) {
        result = r;
    }

    public static String get() {
        if (result == null) {
            throw new IllegalStateException("Result not initialized");
        }
        return result;
    }
}
