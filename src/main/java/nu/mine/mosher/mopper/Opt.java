package nu.mine.mosher.mopper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

class Opt {
    private final Method method;


    public Opt(final Method method) {
        this.method = method;
    }


    public Method getMethod() {
        return this.method;
    }

    public int nVals() {
        return this.method.getParameterCount();
    }

    public int nValsRequired() {
        return (int) Arrays.stream(this.method.getParameters()).filter(Opt::paramIsRequired).count();
    }

    private static boolean paramIsRequired(final Parameter param) {
        return param.getAnnotation(Optional.class) == null;
    }

    public int nValsOptional() {
        return nVals() - nValsRequired();
    }
}
