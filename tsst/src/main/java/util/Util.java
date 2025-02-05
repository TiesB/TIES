package util;

public final class Util {
    public static String methodName(String packageName, String className, String methodName) {
        return packageName + "_" + className + "_" + methodName;
    }

    public static int fib(int i) {
        if (i == 0) {
            return 0;
        }

        if (i == 1) {
            return 1;
        }

        return fib(i - 1) + fib(i - 2);
    }
}
