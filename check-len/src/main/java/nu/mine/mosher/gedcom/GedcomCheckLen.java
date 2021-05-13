package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;

import static nu.mine.mosher.logging.Jul.log;

/**
 * Created by Christopher Alan Mosher, 2018-10-09
 */
public class GedcomCheckLen implements Gedcom.Processor {
    private final GedcomCheckLenOptions options;


    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomCheckLenOptions options = new ArgParser<>(new GedcomCheckLenOptions()).parse(args).verify();
        new Gedcom(options, new GedcomCheckLen(options)).main();
        System.out.flush();
        System.err.flush();
    }


    private GedcomCheckLen(final GedcomCheckLenOptions options) {
        this.options = options;
    }

    @Override
    public boolean process(final GedcomTree tree) {
        this.options.ref.forEach(tree, c -> {
            final int actual = c.getObject().getValue().length();
            final boolean overrideShowAll = (this.options.length==0);
            if (overrideShowAll || (this.options.length-this.options.range <= actual && actual <= this.options.length+this.options.range)) {
                log().warning(findContainingRecord(c).getObject() + ": " + c.getObject());
            }
        });
        return false;
    }

    private static TreeNode<GedcomLine> findContainingRecord(final TreeNode<GedcomLine> n) {
        if (n.parent().parent() == null) {
            return n;
        }
        return findContainingRecord(n.parent());
    }
}
