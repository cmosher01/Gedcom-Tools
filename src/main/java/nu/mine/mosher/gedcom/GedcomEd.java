package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-09-08

public class GedcomEd implements Gedcom.Processor {
    private final GedcomEdOptions options;
    private ArrayList<TreeNode<GedcomLine>> deletes = new ArrayList<>();



    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomEdOptions options = new ArgParser<>(new GedcomEdOptions()).parse(args).verify();
        new Gedcom(options, new GedcomEd(options)).main();
        System.out.flush();
        System.err.flush();
    }



    private GedcomEd(final GedcomEdOptions options) {
        this.options = options;
    }



    @Override
    public boolean process(final GedcomTree tree) {
        this.options.ref.forEach(tree, this::processLine);
        this.deletes.forEach(TreeNode::removeFromParent);
        return true;
    }

    private void processLine(final TreeNode<GedcomLine> node) {
        if (this.options.update != null) {
            node.setObject(node.getObject().replaceValue(this.options.update));
            if (this.options.recurse) {
                node.forEach(this.deletes::add);
            }
        }
        if (this.options.delete) {
            this.deletes.add(node);
        }
    }
}
