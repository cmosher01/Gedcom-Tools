package nu.mine.mosher.gedcom;

public class GedcomTagFromNoteOptions extends GedcomOptions {
    public String tag;
    public String cvt;

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-tagfromnote [OPTIONS] <in.ged >out.ged");
        System.err.println("Recovers unknown GEDCOM tags that Ancestry.com moved into NOTEs.");
        System.err.println("Options:");
        System.err.println("-x, --extract=TAG    tag to extract (required)");
        System.err.println("-n, --convert=TAG    optionally convert to TAG");
        options();
    }

    public void x(final String tag) {
        extract(tag);
    }

    public void extract(final String tag) {
        this.tag = tag;
    }

    public void n(final String tag) {
        convert(tag);
    }

    public void convert(final String tag) {
        this.cvt = tag;
    }

    public GedcomTagFromNoteOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.tag == null) {
            throw new IllegalArgumentException("Must specify -x TAG to extract");
        }
        if (this.cvt == null) {
            this.cvt = this.tag;
        }
        return this;
    }
}
