package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomEdOptions extends GedcomOptions {
    public GedcomDataRef ref;
    public String update;
    public boolean delete;
    public boolean recurse;

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-ed [OPTIONS] <in.ged >out.ged");
        System.err.println("Performs edits on a GEDCOM file.");
        System.err.println("Options:");
        System.err.println("-w, --where=EXPR   Lines to match.");
        System.err.println("-d, --delete       Deletes matched lines, and all children.");
        System.err.println("-t, --update=VALUE Updates values of matched lines.");
        System.err.println("                   With -R, deletes children.");
        System.err.println("-R, --recurse      Affects children of matched rows.");
        options();
    }

    public void w(final String expr) throws GedcomDataRef.InvalidSyntax {
        where(expr);
    }

    public void where(final String expr) throws GedcomDataRef.InvalidSyntax {
        this.ref = new GedcomDataRef(expr);
    }

    public void d() {
        delete();
    }

    public void delete() {
        this.delete = true;
    }

    public void t(final String value) {
        update(value);
    }

    public void update(final String value) {
        this.update = value;
    }

    public void R() {
        recurse();
    }

    private void recurse() {
        this.recurse = true;
    }

    public GedcomEdOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.ref == null) {
            throw new IllegalArgumentException("Missing -w (for example -w '.HEAD.SOUR')");
        }
        if (this.update != null && this.delete) {
            throw new IllegalArgumentException("Cannot specify both -t and -d.");
        }
        return this;
    }
}
