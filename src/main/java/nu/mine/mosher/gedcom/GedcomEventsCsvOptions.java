package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomEventsCsvOptions extends GedcomOptions {
    public boolean prv;

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-events-csv [OPTIONS] <in.ged >out.csv");
        System.err.println("Extracts all events from GEDCOM file, as CSV.");
        System.err.println("Options:");
        System.err.println("-p                   Include private events.");
        options();
    }

    public void p() {
        this.prv = true;
    }

    public GedcomEventsCsvOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.concToWidth == null) {
            throw new IllegalArgumentException("The -c option is required.");
        }
        return this;
    }
}
