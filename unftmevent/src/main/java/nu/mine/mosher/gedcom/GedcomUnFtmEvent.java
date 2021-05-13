package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-08-29

public class GedcomUnFtmEvent implements Gedcom.Processor {
    private final GedcomUnFtmEventOptions options;

    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomUnFtmEventOptions options = new ArgParser<>(new GedcomUnFtmEventOptions()).parse(args).verify();
        new Gedcom(options, new GedcomUnFtmEvent(options)).main();
        System.out.flush();
        System.err.flush();
    }

    private GedcomUnFtmEvent(final GedcomUnFtmEventOptions options) {
        this.options = options;
    }

    @Override
    public boolean process(final GedcomTree tree) {
        tree.getRoot().forEach(indi -> {
            if (indi.getObject().getTag().equals(GedcomTag.INDI)) {
                indi.forEach(even -> {
                    if (even.getObject().getTag().equals(GedcomTag.EVEN)) {
                        undoIfFtmEvent(even);
                    }
                });
            }
        });
        return true;
    }

    private void undoIfFtmEvent(final TreeNode<GedcomLine> even) {
        final TreeNode<GedcomLine> type = even.getFirstChildOrNull();
        if (type == null) {
            return;
        }

        final GedcomLine lineType = type.getObject();
        if (!lineType.getTag().equals(GedcomTag.TYPE)) {
            return;
        }

        if (!lineType.getValue().equals(this.options.tag)) {
            return;
        }

        final GedcomLine lineEven = even.getObject();

        even.setObject(GedcomLine.createUser(lineEven.getLevel(), lineType.getValue(), lineEven.getValue()));
        type.removeFromParent();
    }
}
