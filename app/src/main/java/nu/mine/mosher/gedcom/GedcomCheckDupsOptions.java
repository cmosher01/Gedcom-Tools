package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomCheckDupsOptions extends GedcomOptions {
    public GedcomDataRef ref;

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-check-dups [OPTIONS] <in.ged >out.ged");
        System.err.println("Checks GEDCOM file for duplicate records.");
        System.err.println("Options:");
        System.err.println("-w, --where=EXPR     Lines to check.");
        options();
    }

    public void w(final String expr) throws GedcomDataRef.InvalidSyntax {
        where(expr);
    }

    public void where(final String expr) throws GedcomDataRef.InvalidSyntax {
        this.ref = new GedcomDataRef(expr);
    }

    public GedcomCheckDupsOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.ref == null) {
            throw new IllegalArgumentException("Missing -w (for example -w '.INDI.RESI')");
        }
        return this;
    }
}
