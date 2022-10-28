package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-09-16

public class GedcomTagFromNote implements Gedcom.Processor {
    private final GedcomTagFromNoteOptions options;
    private final List<ChildToBeAdded> newNodes = new ArrayList<>(256);

    private static class ChildToBeAdded {
        TreeNode<GedcomLine> parent;
        TreeNode<GedcomLine> child;

        ChildToBeAdded(TreeNode<GedcomLine> parent, TreeNode<GedcomLine> child) {
            this.parent = parent;
            this.child = child;
        }
    }



    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomTagFromNoteOptions options = new ArgParser<>(new GedcomTagFromNoteOptions()).parse(args).verify();
        new Gedcom(options, new GedcomTagFromNote(options)).main();
        System.out.flush();
        System.err.flush();
    }



    private GedcomTagFromNote(final GedcomTagFromNoteOptions options) {
        this.options = options;
    }



    @Override
    public boolean process(final GedcomTree tree) {
        tree.getRoot().forAll(this::processNode);
        this.newNodes.forEach(a -> a.parent.addChild(a.child));
        return true;
    }

    private void processNode(final TreeNode<GedcomLine> node) {
        final GedcomLine line = node.getObject();
        if (line != null && line.getTag().equals(GedcomTag.NOTE)) {
            recoverTagFromNote(node);
        }
    }

    private void recoverTagFromNote(final TreeNode<GedcomLine> node) {
        final String note = fixSpacing(node.getObject().getValue());
        new BufferedReader(new StringReader(note)).lines().forEach(n -> recoverTagFromLine(node, n));
    }

    private void recoverTagFromLine(final TreeNode<GedcomLine> node, String n) {
        final String value = getTagValue(n);
        if (value != null) {
            if (this.options.verbose) {
                log().info("Found tag in note: "+value);
            }
            final TreeNode<GedcomLine> parent = node.parent();
            if (parent == null || parent.getObject() == null) {
                log().warning("cannot find parent for: "+node);
            }
            this.newNodes.add(new ChildToBeAdded(parent, new TreeNode<>(parent.getObject().createChild(this.options.cvt, value))));
        }
    }

    private String getTagValue(String n) {
        n = n.trim();
        if (n.equals(this.options.tag)) {
            return "";
        }
        if (n.startsWith(this.options.tag + " ")) {
            return n.substring(this.options.tag.length() + 1);
        }
        return null;
    }

    private static String fixSpacing(String value) {
        while (value.contains("  ")) {
            value = value.replace("  ", "\n");
        }
        while (value.contains("\n\n\n")) {
            value = value.replace("\n\n\n", "\n\n");
        }
        while (value.contains("\n ")) {
            value = value.replace("\n ", "\n");
        }
        while (value.contains(" \n")) {
            value = value.replace(" \n", "\n");
        }
        return value;
    }
}
