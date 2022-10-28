package nu.mine.mosher.mopper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Arg {
    private final String arg;
    private final List<String> values = new ArrayList<>(2);
    private boolean terminated = false;

    public Arg(final String arg) {
        this.arg = arg;
        if (this.arg == null) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isNamed() {
        return isLongNamed() || isShortNamed();
    }

    public boolean isShortNamed() {
        return this.arg.startsWith("-") && !this.arg.startsWith("--") && !this.arg.equals("-");
    }

    public boolean isLongNamed() {
        return this.arg.startsWith("--") && !this.arg.equals("--");
    }

    public boolean isTerminator() {
        return this.arg.equals("--");
    }

    public String getShortName() {
        assert isShortNamed();
        return this.arg.substring(1);
    }

    public String getLongName() {
        assert isLongNamed();
        final String n = getBeforeEqualOrAll();
        if (n.length() <= 1) {
            throw new IllegalArgumentException(this.arg);
        }
        return n;
    }

    public String getName() {
        if (!this.terminated) {
            if (isShortNamed()) {
                return getShortName();
            }
            if (isLongNamed()) {
                return getLongName();
            }
        }
        return "";
    }

    public boolean isShortWithManyValues() {
        return this.isShortNamed() && this.getShortName().length() > 1;
    }

    public String getFirstShortName() {
        assert isShortWithManyValues();
        return this.getShortName().substring(0, 1);
    }

    public String getShortNameRest() {
        assert isShortWithManyValues();
        return this.getShortName().substring(1);
    }

    public String getValue() {
        if (this.arg.equals("-") || this.arg.equals("--") || !isNamed()) {
            return this.arg;
        }
        if (isLongNamed()) {
            return getAfterEqualOrNothing();
        }
        return "";
    }

    private String getBeforeEqualOrAll() {
        final int i = this.arg.indexOf('=');
        if (i >= 0) {
            return this.arg.substring(2, i);
        }
        return this.arg.substring(2);
    }

    private String getAfterEqualOrNothing() {
        final int i = this.arg.indexOf('=');
        if (i >= 0) {
            return this.arg.substring(i + 1);
        }
        return "";
    }


    public void addValue(final Arg a) {
        this.values.add(a.isNamed() ? a.arg : a.getValue());
    }

    public Object[] getAllValues(final int atLeast) {
        final ArrayList<String> r = new ArrayList<>(this.values.size() + 1);

        if (this.terminated) {
            r.add(this.arg);
        } else {
            if (!getValue().isEmpty()) {
                r.add(getValue());
            }
            r.addAll(this.values);
        }

        final int df = atLeast - r.size();
        if (df > 0) {
            r.addAll(Collections.nCopies(df, null));
        }

        return r.toArray();
    }

    public void terminate() {
        this.terminated = true;
    }
}
