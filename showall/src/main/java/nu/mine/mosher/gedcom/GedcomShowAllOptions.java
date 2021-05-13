package nu.mine.mosher.gedcom;

@SuppressWarnings({ "access", "WeakerAccess", "unused" })
public class GedcomShowAllOptions extends GedcomOptions {
    public GedcomDataRef ref;

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-showall -w .INDI.NAME <in.ged");
        System.err.println("Shows matching lines from a GEDCOM file.");
        System.err.println("Options:");
        System.err.println("-w, --where=EXPR     Tag path to show, ex.: .INDI.NAME");
        options();
    }

    public void w(final String expr) throws GedcomDataRef.InvalidSyntax {
        where(expr);
    }

    public void where(final String expr) throws GedcomDataRef.InvalidSyntax {
        this.ref = new GedcomDataRef(expr);
    }

    public GedcomShowAllOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.concToWidth == null) {
            throw new IllegalArgumentException("The -c option is required.");
        }
        if (this.ref == null) {
            throw new IllegalArgumentException("Missing required -w tag path.");
        }
        return this;
    }
}
