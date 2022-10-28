package nu.mine.mosher.mopper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Options<T> {
    public static final String METHOD_NAME_FOR_UNNAMED_ARGS = "__";

    private final T opts;
    private final Map<String, Opt> mapNameOpt = new HashMap<>(16, 1.0f);

    public static <T> Options<T> compile(final T optionProcessor) {
        final Options<T> options = new Options<>(optionProcessor);
        Arrays.stream(optionProcessor.getClass().getMethods()).filter(Options::nonObject).forEach(options::add);
        return options;
    }

    public T getObject() {
        return this.opts;
    }

    private static boolean nonObject(final Method m) {
        return !m.getDeclaringClass().equals(Object.class);
    }

    private Options(final T opts) {
        this.opts = opts;
    }

    private void add(final Method m) {
        if (this.mapNameOpt.containsKey(m.getName())) {
            throw new IllegalStateException("duplicate option method name: " + m.getName());
        }

        // TODO check for: only String args
        // TODO check for: required before optional values

        this.mapNameOpt.put(m.getName(), new Opt(m));
    }

    public boolean has(final String name) {
        return this.mapNameOpt.containsKey(filterName(name));
    }

    public Opt get(final String name) {
        if (!has(name)) {
            throw new IllegalArgumentException(name);
        }
        return this.mapNameOpt.get(filterName(name));
    }

    public boolean optionTakesValues(final String name) {
        return has(name) && get(name).nVals() > 0;
    }

    public void process(final Arg a) {
        try {
            tryProcess(a);
        } catch (final Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void tryProcess(final Arg a) throws IllegalAccessException, InvocationTargetException {
        final Opt o = get(a.getName());
        o.getMethod().invoke(this.opts, a.getAllValues(o.nVals()));
    }

    private String filterName(final String name) {
        if (name.isEmpty()) {
            return Options.METHOD_NAME_FOR_UNNAMED_ARGS;
        }
        return name;
    }

    @Override
    public String toString() {
        return this.mapNameOpt.toString();
    }
}
