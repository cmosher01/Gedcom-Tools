package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomEventizeOptions extends GedcomOptions {
    public GedcomDataRef ref;
    public String typeEvent;

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-eventize [OPTIONS] <in.ged >out.ged");
        System.err.println("Handles Family Tree Maker event anomalies.");
        System.err.println("Options:");
        System.err.println("-w, --where=EXPR     Lines to change.");
        System.err.println("-t, --type=event     Optional type of event to convert to.");
        options();
    }

    public void w(final String expr) throws GedcomDataRef.InvalidSyntax {
        where(expr);
    }

    public void where(final String expr) throws GedcomDataRef.InvalidSyntax {
        this.ref = new GedcomDataRef(expr);
    }

    public void t(final String value) {
        type(value);
    }

    public void type(final String value) {
        this.typeEvent = value;
    }

    public GedcomEventizeOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.concToWidth == null) {
            throw new IllegalArgumentException("The -c option is required.");
        }
        if (this.ref == null) {
            throw new IllegalArgumentException("Missing -w (for example -w '.INDI.RESI')");
        }
        return this;
    }
}
