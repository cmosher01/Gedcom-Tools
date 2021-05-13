package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomFixDateOptions extends GedcomOptions {

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-fixdate [OPTIONS] <in.ged >out.ged");
        System.err.println("Fixes incorrectly formatted dates in a GEDCOM file.");
        System.err.println("Options:");
        options();
    }

    public GedcomFixDateOptions verify() {
        if (this.help) {
            return this;
        }
        // nothing to verify (yet?)
        return this;
    }
}
