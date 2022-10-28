package nu.mine.mosher.gedcom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "access", "WeakerAccess", "unused" })
public class GedcomRestoreHeadOptions extends GedcomOptions {
    public File source;
    // TODO use this (make it generic instead of hardcoding COPR and SUBM)
    public List<GedcomDataRef> refs = new ArrayList<>(2);

    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-restorehead [OPTIONS] <in.ged -g source.ged >out.ged");
        System.err.println("Restores COPR and SUBM.");
        System.err.println("Options:");
        System.err.println("-g, --gedcom=FILE    GEDCOM file to extract from.");
//        System.err.println("-w, --where=EXPR     Item to copy. For example, '.HEAD.COPR'");
        options();
    }

    public void g(final String file) throws IOException {
        gedcom(file);
    }

    public void gedcom(final String file) throws IOException {
        this.source = new File(file);
        if (!this.source.canRead()) {
            throw new IllegalArgumentException("Cannot read GEDCOM input file: " + this.source.getCanonicalPath());
        }
    }

    public void w(final String expr) throws GedcomDataRef.InvalidSyntax {
        where(expr);
    }

    public void where(final String expr) throws GedcomDataRef.InvalidSyntax {
        this.refs.add(new GedcomDataRef(expr));
    }

    public GedcomRestoreHeadOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.concToWidth == null) {
            throw new IllegalArgumentException("Missing required -c option.");
        }
        if (this.source == null) {
            throw new IllegalArgumentException("Missing required GEDCOM input file.");
        }
        if (this.refs.isEmpty()) {
//            throw new IllegalArgumentException("Missing -w (for example -w '.*.REFN')");
        }
        return this;
    }
}
