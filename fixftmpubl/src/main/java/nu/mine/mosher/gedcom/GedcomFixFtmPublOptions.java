package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomFixFtmPublOptions extends GedcomOptions {
    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-fixftmpubl [OPTIONS] <in.ged >out.ged");
        System.err.println("Removes Name prepended by FTM from PUBL values.");
        System.err.println("Options:");
        options();
    }

    public GedcomFixFtmPublOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.concToWidth == null) {
            throw new IllegalArgumentException("Must specify -c.");
        }
        return this;
    }
}
