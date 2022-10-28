package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static nu.mine.mosher.logging.Jul.log;

/**
 * Created by Christopher Alan Mosher on 2017-10-13
 */
public class GedcomShowAll implements Gedcom.Processor {
    private final GedcomShowAllOptions options;
    private GedcomTree tree;

    public static void main(final String... args) throws InvalidLevel, IOException, GedcomDataRef.InvalidSyntax {
        log();
        final GedcomShowAllOptions options = new ArgParser<>(new GedcomShowAllOptions()).parse(args).verify();
        new Gedcom(options, new GedcomShowAll(options)).main();
        System.out.flush();
        System.err.flush();
    }

    private GedcomShowAll(final GedcomShowAllOptions options) {
        this.options = options;
    }


    @Override
    public boolean process(final GedcomTree gedcomTree) {
        this.tree = gedcomTree;

        this.options.ref.forEach(this.tree, c -> {
            System.out.println(findContainingRecord(c).getObject().getID()+": "+deref(c).getObject().getValue());
        });
        return false;
    }

    private TreeNode<GedcomLine> deref(final TreeNode<GedcomLine> node) {
        TreeNode<GedcomLine> r = node;
        if (r.getObject().isPointer()) {
            r = this.tree.getNode(node.getObject().getPointer());
            if (r == null) {
                log().severe("Cannot find record for ID: "+node.getObject().getPointer());
            }
        }
        return r;
    }

    private static TreeNode<GedcomLine> findContainingRecord(final TreeNode<GedcomLine> n) {
        if (n.parent().parent() == null) {
            return n;
        }
        return findContainingRecord(n.parent());
    }
}
