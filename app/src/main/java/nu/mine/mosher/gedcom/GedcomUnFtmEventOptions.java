package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomUnFtmEventOptions extends GedcomOptions {
    public String tag = "";

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-unftmevent [OPTIONS] <in.ged >out.ged");
        System.err.println("Undoes Family Tree Maker's turning unknown event tags from imported GEDCOM files to EVEN records.");
        System.err.println("Options:");
        System.err.println("-t, --tag=TAG        INDI tag to extract (from INDI.EVEN.TYPE)");
        options();
    }

    public void t(final String tag) {
        tag(tag);
    }

    public void tag(final String tag) {
        this.tag = tag;
    }

    public GedcomUnFtmEventOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.tag.isEmpty()) {
            throw new IllegalStateException("Missing --tag argument.");
        }
        return this;
    }
}
