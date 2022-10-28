package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.logging.Jul;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

import static nu.mine.mosher.logging.Jul.log;

/**
 * For each top-level record (INDI, FAM, SOUR, etc.) that does not have a REFN,
 * add one with a UUID for a value.
 * <p>
 * Created by user on 12/12/16.
 */
public class GedcomRefnSkel {
    public static void main(final String... args) throws InvalidLevel, IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("\n\nUsage:\n    gedcom-refn-skel INPUT.ged [...]\n");
        }

        log();

        for (final String arg : args) {
            createSkelForIndisMissingRefn(arg);
        }

        System.err.flush();
        System.out.flush();
    }

    private static void createSkelForIndisMissingRefn(final String filename) throws IOException, InvalidLevel {
        final File fileInput = Paths.get(filename).toFile().getCanonicalFile().getAbsoluteFile();
        log().info("Begin processing " + fileInput);

        final File fileOutput = Paths.get(filename + ".skel").toFile().getCanonicalFile().getAbsoluteFile();
        if (fileOutput.exists()) {
            log().warning("Output file " + fileOutput + " already exists. Abort processing this input file.");
            return;
        }

        final GedcomTree treeInput = loadTreeFrom(fileInput);
        final GedcomTree treeOutput = processTree(treeInput);
        if (hasIndis(treeOutput)) {
            log().info("writing to " + fileOutput);
            writeTreeTo(treeOutput, fileOutput);
        } else {
            log().info("No INDI records had REFN missing in this input file.\n");
        }
    }

    private static boolean hasIndis(final GedcomTree tree) {
        for (final TreeNode<GedcomLine> rec : tree.getRoot()) {
            final GedcomLine recLn = rec.getObject();
            if (recLn.getTag().equals(GedcomTag.INDI)) {
                return true;
            }
        }
        return false;
    }

    private static GedcomTree loadTreeFrom(final File fileInput) throws IOException, InvalidLevel {
        final GedcomTree gt = Gedcom.readFile(new BufferedInputStream(new FileInputStream(fileInput)));
        new GedcomConcatenator(gt).concatenate();
        gt.setCharset(StandardCharsets.UTF_8);
        return gt;
    }

    private static void writeTreeTo(final GedcomTree treeOutput, final File fileOutput) throws IOException {
        treeOutput.setMaxLength(60);
        new GedcomUnconcatenator(treeOutput).unconcatenate();
        final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileOutput));
        Gedcom.writeFile(treeOutput, out);
        out.flush();
        out.close();
    }

    private static GedcomTree processTree(final GedcomTree treeInput) {
        final GedcomTree treeOutput = GedcomMinimal.minimal(null);
        final TreeNode<GedcomLine> rootOutput = treeOutput.getRoot();
        final TreeNode<GedcomLine> trlrOutput = getTrlr(rootOutput);

        treeInput.getRoot().forEach(top -> {
            final GedcomLine gedcomLine = top.getObject();
            if (gedcomLine != null && gedcomLine.hasID() && gedcomLine.getTag().equals(GedcomTag.INDI)) {
                if (!hasRefn(top)) {
                    log().info("found INDI without REFN: " + gedcomLine);
                    addSkel(top, rootOutput, trlrOutput);
                }
            }
        });

        return treeOutput;
    }

    private static void addRefn(final TreeNode<GedcomLine> indi) {
        indi.addChild(new TreeNode<>(indi.getObject().createChild(GedcomTag.REFN, UUID.randomUUID().toString())));
    }

    private static final Set<GedcomTag> setIndiChildSkel = Collections.unmodifiableSet(new HashSet<GedcomTag>() {{
        add(GedcomTag.NAME);
        add(GedcomTag.SEX);
        add(GedcomTag.RIN);
    }});

    private static void addSkel(final TreeNode<GedcomLine> srcIndi, final TreeNode<GedcomLine> toParentDst, final TreeNode<GedcomLine> beforeChildDst) {
        final GedcomLine srcLn = srcIndi.getObject();
        final TreeNode<GedcomLine> dstIndi = new TreeNode<>(GedcomLine.createEmptyId(srcLn.getID(), GedcomTag.INDI));

        for (final TreeNode<GedcomLine> c : srcIndi) {
            final GedcomLine ln = c.getObject();
            final GedcomTag tag = ln.getTag();
            if (setIndiChildSkel.contains(tag)) {
                dstIndi.addChild(new TreeNode<>(dstIndi.getObject().createChild(tag, ln.getValue())));
            } else if (tag.equals(GedcomTag.BIRT) || tag.equals(GedcomTag.DEAT)) {
                final TreeNode<GedcomLine> node = new TreeNode<>(dstIndi.getObject().createChild(tag, ln.getValue()));
                dstIndi.addChild(node);
                for (final TreeNode<GedcomLine> c2 : c) {
                    if (c2.getObject().getTag().equals(GedcomTag.DATE)) {
                        node.addChild(new TreeNode<>(node.getObject().createChild(GedcomTag.DATE, c2.getObject().getValue())));
                    }
                }
            }
        }
        addRefn(dstIndi);
        toParentDst.addChildBefore(dstIndi, beforeChildDst);
    }

    private static TreeNode<GedcomLine> getTrlr(final TreeNode<GedcomLine> root) {
        for (final TreeNode<GedcomLine> top : root) {
            final GedcomLine gedcomLine = top.getObject();
            if (gedcomLine != null && gedcomLine.getTag().equals(GedcomTag.TRLR)) {
                return top;
            }
        }
        throw new IllegalStateException("Could not find TRLR in minimal GEDCOM file.");
    }


    private static boolean hasRefn(final TreeNode<GedcomLine> top) {
        for (final TreeNode<GedcomLine> attr : top) {
            final GedcomLine gedcomLine = attr.getObject();
            if (gedcomLine != null && gedcomLine.getTag().equals(GedcomTag.REFN)) {
                return true;
            }
        }
        return false;
    }
}
