package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-10-29

public class GedcomRestoreHead implements Gedcom.Processor {
    private final GedcomRestoreHeadOptions options;
    private GedcomTree tree;
    private GedcomTree source;


    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomRestoreHeadOptions options = new ArgParser<>(new GedcomRestoreHeadOptions()).parse(args).verify();
        new Gedcom(options, new GedcomRestoreHead(options)).main();
        System.out.flush();
        System.err.flush();
    }



    private GedcomRestoreHead(final GedcomRestoreHeadOptions options) {
        this.options = options;
    }



    @Override
    public boolean process(final GedcomTree tree) {
        try {
            this.tree = tree;
            getSourceTree();
            copyCopr();
            copySubm();
            return true;
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private void getSourceTree() throws IOException, InvalidLevel {
        this.source = Gedcom.readFile(new BufferedInputStream(new FileInputStream(this.options.source)));
        new GedcomConcatenator(this.source).concatenate();
    }

    private void copyCopr() {
        final String copr = getCopr();
        this.tree.getRoot().forEach(nodeHead -> {
            final GedcomLine lineHead = nodeHead.getObject();
            if (lineHead.getTag().equals(GedcomTag.HEAD)) {
                GedcomTree.addOrUpdateChild(nodeHead, GedcomTag.COPR, copr);
            }
        });
    }

    private String getCopr() {
        final List<GedcomLine> coprs = new ArrayList<>();

        this.source.getRoot().forEach(nodeHead -> {
            final GedcomLine lineHead = nodeHead.getObject();
            if (lineHead.getTag().equals(GedcomTag.HEAD)) {
                nodeHead.forEach(nodeCopr -> {
                    final GedcomLine lineCopr = nodeCopr.getObject();
                    if (lineCopr.getTag().equals(GedcomTag.COPR)) {
                        coprs.add(lineCopr);
                    }
                });
            }
        });

        if (coprs.isEmpty()) {
            return "";
        }

        return coprs.get(0).getValue();
    }

    private void copySubm() {
        final TreeNode<GedcomLine> subm = getSubm();
        if (subm == null) {
            log().warning("No .HEAD.SUBM found in source file; therefore, one will not be inserted.");
            return;
        }

        removeSubm();
        addSubm(subm);
    }

    private TreeNode<GedcomLine> getSubm() {
        final ArrayList<TreeNode<GedcomLine>> subms = new ArrayList<>();

        this.source.getRoot().forEach(nodeHead -> {
            final GedcomLine lineHead = nodeHead.getObject();
            if (lineHead.getTag().equals(GedcomTag.HEAD)) {
                nodeHead.forEach(nodeSubm -> {
                    final GedcomLine lineSubm = nodeSubm.getObject();
                    if (lineSubm.getTag().equals(GedcomTag.SUBM)) {
                        final TreeNode<GedcomLine> node = this.source.getNode(lineSubm.getPointer());
                        if (node != null) {
                            subms.add(node);
                        }
                    }
                });
            }
        });

        if (subms.isEmpty()) {
            return null;
        }

        return subms.get(0);
    }

    private void removeSubm() {
        for (final TreeNode<GedcomLine> nodeHead : this.tree.getRoot()) {
            final GedcomLine lineHead = nodeHead.getObject();
            if (lineHead.getTag().equals(GedcomTag.HEAD)) {
                final ListIterator<TreeNode<GedcomLine>> liHead = nodeHead.childrenList();
                while (liHead.hasNext()) {
                    final TreeNode<GedcomLine> nodeSubm = liHead.next();
                    final GedcomLine lineSubm = nodeSubm.getObject();
                    if (lineSubm.getTag().equals(GedcomTag.SUBM)) {
                        final TreeNode<GedcomLine> nodeTopSubm = this.tree.getNode(lineSubm.getPointer());
                        if (nodeTopSubm == null) {
                            log().severe("Cannot find top-level SUBM record: " + lineSubm.getPointer());
                        } else {
                            nodeTopSubm.removeFromParent();
                            liHead.remove();
                            return; // break out, because we modified the tree's top-level collection
                        }
                    }
                }
            }
        }
    }

    private void addSubm(final TreeNode<GedcomLine> subm) {
        addSubmRecord(subm);
        addHeadSubm(subm);
    }

    private void addSubmRecord(final TreeNode<GedcomLine> subm) {
        TreeNode<GedcomLine> before = null;
        boolean foundHead = false;
        for (final TreeNode<GedcomLine> node : this.tree.getRoot()) {
            if (node.getObject().getTag().equals(GedcomTag.HEAD)) {
                foundHead = true;
            } else if (foundHead) {
                before = node;
                break;
            }
        }
        this.tree.getRoot().addChildBefore(subm, before);
    }

    private void addHeadSubm(final TreeNode<GedcomLine> subm) {
        for (final TreeNode<GedcomLine> nodeHead : this.tree.getRoot()) {
            final GedcomLine lineHead = nodeHead.getObject();
            if (lineHead.getTag().equals(GedcomTag.HEAD)) {
                nodeHead.addChild(new TreeNode<>(lineHead.createChild(GedcomTag.SUBM, "@" + subm.getObject().getID() + "@")));
                return; // quick exit
            }
        }
    }
}
