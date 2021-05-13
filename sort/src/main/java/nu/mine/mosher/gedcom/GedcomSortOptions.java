package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomSortOptions extends GedcomOptions {

    public void help() {
        this.help = true;
        System.err.println("Usage: java -jar gedcom-sort-all.jar [OPTIONS] <in.ged >out.ged");
        System.err.println("Sort a GEDCOM file.");
        System.err.println("Options:");
        options();
    }

    public GedcomSortOptions verify() {
        if (this.help) {
            return this;
        }
        return this;
    }
}
