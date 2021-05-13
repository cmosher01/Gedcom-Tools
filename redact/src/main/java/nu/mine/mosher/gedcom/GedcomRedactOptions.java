package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomRedactOptions extends GedcomOptions {
    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-redact [OPTIONS] <in.ged >out.ged");
        System.err.println("Redacts recent, private information from a GEDCOM file.");
        System.err.println("Options:");
        options();
    }
    public GedcomRedactOptions verify() {
        return this;
    }
}
