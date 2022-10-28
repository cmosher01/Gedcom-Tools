package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomCheckLenOptions extends GedcomOptions {
    public GedcomDataRef ref;
    public int length = 256;
    public int range = 3;

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-check-len [OPTIONS] <in.ged");
        System.err.println("Checks for lines of a given type and length.");
        System.err.println("Options:");
        System.err.println("-w, --where=EXPR     Tag path to show, ex.: .INDI.NAME");
        System.err.println("-l, --length=n       length (characters) (default: 256)");
        System.err.println("-r, --range=n        plus-or-minus (characters) (default: 3)");
        options();
    }

    public GedcomCheckLenOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.concToWidth == null) {
            throw new IllegalArgumentException("The -c option is required.");
        }
        if (this.ref == null) {
            throw new IllegalArgumentException("Missing required -w tag path.");
        }
        if (this.length < 0) {
            throw new IllegalArgumentException("Invalid length.");
        }
        if (this.range < 0) {
            throw new IllegalArgumentException("Invalid length.");
        }
        return this;
    }

    public void w(final String expr) throws GedcomDataRef.InvalidSyntax {
        where(expr);
    }

    public void where(final String expr) throws GedcomDataRef.InvalidSyntax {
        this.ref = new GedcomDataRef(expr);
    }

    public void l(final String len) {
        length(len);
    }

    public void length(final String len) {
        this.length = Integer.parseInt(len);
    }

    public void r(final String range) {
        range(range);
    }

    public void range(final String range) {
        this.range = Integer.parseInt(range);
    }
}
