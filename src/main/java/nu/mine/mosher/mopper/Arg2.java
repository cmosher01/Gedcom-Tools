package nu.mine.mosher.mopper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Arg2 {
    private final String org;

    private boolean terminated;

    private String ind = "";
    private String opt = "";
    private String val = "";
    private String rst = "";
    private boolean trm;
    private boolean pxt;
    private boolean pvn;

    private final List<String> values = new ArrayList<>(2);



    private static final boolean USE_REGEX_PARSER = false;
    public Arg2(final String arg) {
        this.org = arg;
        if (USE_REGEX_PARSER) {
            parseRegEx();
        } else {
            parse();
        }
    }

    public String ind() {
        return this.ind;
    }

    public String opt() {
        return this.opt;
    }

    public String val() {
        return this.val;
    }

    public String rst() {
        return this.rst;
    }

    public boolean trm() {
        return this.trm;
    }

    public boolean pxt() {
        return this.pxt;
    }

    public boolean pvn() {
        return this.pvn;
    }

    public boolean terminated() {
        return this.terminated;
    }



    public void terminate() {
        this.val = org;
        this.ind = this.opt = this.rst = "";
        this.terminated = this.trm = this.pxt = true;
        this.pvn = false;
    }



    private static final Pattern ARG = Pattern.compile("(-(?:-|W|))?([^=]*)(=?[^=]*)");

    private void parseRegEx() {
        final Matcher m = ARG.matcher(this.org);
        if (m.matches()) {
            this.ind = m.group(1);
            this.opt = m.group(2);
            this.val = m.group(3);
        }
        if (this.val.startsWith("=")) {
            this.val = this.val.substring(1);
        }
        if (this.ind == null || this.ind.equals("-W")) {
            this.ind = "";
        }
        // TODO fixups, but is it worth it?
    }

    private void parse() {
        if (this.org.startsWith("--")) {
            this.ind = "--";
            optval();
        } else if (this.org.startsWith("-")) {
            if (this.org.equals("-")) {
                this.val = this.org;
                this.pxt = true;
            } else {
                this.ind = "-";
                this.opt = this.org.substring(1,2);
                this.rst = this.org.substring(2);
                vendor();
            }
        } else {
            if (this.org.startsWith("=") || !this.org.contains("=")) {
                this.val = this.org;
                this.pxt = true;
            } else {
                this.opt = beforeEq(this.org);
                this.val = afterEq(this.org);
            }
        }
        // TODO should we allow long options to be one and/or zero characters long?
    }

    private void vendor() {
        if (this.opt.equals("W")) {
            if (this.rst.isEmpty()) {
                this.pvn = true;
            } else {
                this.ind = this.rst = "";
                optval();
            }
        }
    }

    private void optval() {
        final String s = this.org.substring(2);
        this.opt = beforeEq(s);
        this.val = afterEq(s);
        if (this.opt.isEmpty() && this.val.isEmpty() && !this.org.contains("=")) {
            this.trm = this.pxt = true;
        }
    }

    static String afterEq(final String s) {
        int i = s.indexOf('=');
        if (i < 0) {
            i = s.length()-1;
        }
        return s.substring(i+1);
    }

    static String beforeEq(final String s) {
        int i = s.indexOf('=');
        if (i < 0) {
            i = s.length();
        }
        return s.substring(0,i);
    }


    @Override
    public String toString() {
        String s = "";

        if (!this.opt.isEmpty()) {
            s += this.opt + "=";
        }
        s += "'" + this.val + "'";

        s += "  {" + this.ind + "|" + this.opt + "|" + this.val + "}";
        if (!this.rst.isEmpty()) {
            s += " [" + this.rst + "]";
        }
        if (this.trm) {
            s += " GNU_TERM";
        }
        if (this.pxt) {
            s += " POSIX_TERM";
        }
        if (this.pvn) {
            s += " POSIX_VENDOR_OPT";
        }

        return s;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Arg2)) {
            return false;
        }
        final Arg2 that = (Arg2)obj;
        return this.org.equals(that.org) && (this.terminated == that.terminated);
    }

    @Override
    public int hashCode() {
        return this.org.hashCode() * 31 + (this.terminated ? 31 : 0);
    }
}
