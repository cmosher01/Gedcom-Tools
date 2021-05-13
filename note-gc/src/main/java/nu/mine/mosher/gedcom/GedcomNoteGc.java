package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.logging.Jul;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static nu.mine.mosher.logging.Jul.log;

/**
 * Removes unreferenced top-level NOTE records.
 */
public class GedcomNoteGc implements Gedcom.Processor {
    private final List<TreeNode<GedcomLine>> deletes = new ArrayList<>(256);

    public static void main(final String... args) throws InvalidLevel, IOException {
        if (args.length > 0) {
            throw new IllegalArgumentException("\n\nUsage:\n    gedcom-note-gc <in.ged >out.ged\n");
        }

        log();

        final GedcomOptions options = new GedcomOptions();
        options.timestamp();
        new Gedcom(options, new GedcomNoteGc()).main();

        System.err.flush();
        System.out.flush();
    }

    @Override
    public boolean process(final GedcomTree tree) {
        final Set<String> idsReferenced = new HashSet<>(100);
        findAllReferencedIds(tree, idsReferenced);
        flagUnreferencedNotes(idsReferenced, tree);
        this.deletes.forEach(TreeNode::removeFromParent);
        return true;
    }

    private void findAllReferencedIds(final GedcomTree tree, final Set<String> idsReferenced) {
        tree.getRoot().forAll((node) -> {
            final GedcomLine line = node.getObject();
            if (Objects.nonNull(line) && line.isPointer()) {
                idsReferenced.add(line.getPointer());
            }
        });
    }

    private void flagUnreferencedNotes(final Set<String> idsReferenced, final GedcomTree tree) {
        tree.getRoot().forEach((top) -> {
            final GedcomLine line = top.getObject();
            if (Objects.nonNull(line) && line.getTag().equals(GedcomTag.NOTE) && line.hasID() && !idsReferenced.contains(line.getID())) {
                this.deletes.add(top);
            }
        });
    }
}
