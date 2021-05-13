package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static nu.mine.mosher.gedcom.GedcomTag.*;

public class GedcomCull {
    public static void main(final String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("\n\nUsage:\n    gedcom-cull INPUT.ged [...]\n");
        }

        Arrays.stream(args).forEach(GedcomCull::cull);

        System.err.flush();
        System.out.flush();
    }

    private static void cull(final String filepathInput) {
        final Optional<File> fileInput = fileForPath(filepathInput);
        if (!fileInput.isPresent() || !fileInput.get().canRead() || !fileInput.get().isFile()) {
            System.err.println("ERROR: Cannot read file "+filepathInput+"; skipping it.");
            return;
        }

        final File fileOutput = new File(fileInput.get().getName()+".cull");
        if (fileOutput.exists()) {
            System.err.println("ERROR: File "+fileOutput.getAbsolutePath()+" already exists; skipping.");
            return;
        }

        final Optional<GedcomTree> treeInput = readGedcom(fileInput.get());
        if (!treeInput.isPresent()) {
            System.err.println("ERROR: Error reading "+fileInput.get().getAbsolutePath()+"; skipping.");
            return;
        }

        final GedcomTree treeOutput = processTree(treeInput.get());

        writeTreeTo(treeOutput, fileOutput);
    }

    private static void writeTreeTo(final GedcomTree treeOutput, final File fileOutput) {
        treeOutput.setMaxLength(60);
        new GedcomUnconcatenator(treeOutput).unconcatenate();
        try {
            final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileOutput));
            Gedcom.writeFile(treeOutput, out);
            out.flush();
            out.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static GedcomTree processTree(GedcomTree treeInput) {
        final GedcomTree treeOutput = GedcomMinimal.minimal(null);
        final TreeNode<GedcomLine> trlrOutput = getTrlr(treeOutput.getRoot());
        treeInput.getRoot().forEach(top -> {
            final GedcomLine gedcomLine = top.getObject();
            if (gedcomLine != null && gedcomLine.hasID()) {
                if (gedcomLine.getTag().equals(GedcomTag.INDI) || gedcomLine.getTag().equals(GedcomTag.FAM)) {
                    addCulled(top, treeOutput, trlrOutput);
                }
            }
        });
        return treeOutput;
    }

    private static final Set<GedcomTag> SKEL = Set.of(NAME, SEX, REFN, RIN, BIRT, DEAT, FAMC, FAMS, HUSB, WIFE, CHIL);

    private static void addCulled(TreeNode<GedcomLine> indi, GedcomTree treeOutput, final TreeNode<GedcomLine> beforeChildDst) {
        final TreeNode<GedcomLine> indiOutput = new TreeNode<>(indi.getObject());
        treeOutput.getRoot().addChildBefore(indiOutput, beforeChildDst);
        for (final TreeNode<GedcomLine> c : indi) {
            if (SKEL.contains(c.getObject().getTag())) {
                final TreeNode<GedcomLine> attrOutput = new TreeNode<>(c.getObject());
                indiOutput.addChild(attrOutput);
                if (c.getObject().getTag().equals(BIRT) || c.getObject().getTag().equals(DEAT)) {
                    for (final TreeNode<GedcomLine> c2 : c) {
                        if (c2.getObject().getTag().equals(GedcomTag.DATE) || c2.getObject().getTag().equals(PLAC)) {
                            attrOutput.addChild(new TreeNode<>(c2.getObject()));
                        }
                    }
                }
            }
        }
    }

    private static Optional<GedcomTree> readGedcom(File fileInput) {
        try {
            return Optional.of(Gedcom.readFile(new BufferedInputStream(Files.newInputStream(fileInput.toPath()))));
        } catch (final Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Optional<File> fileForPath(final String filepath) {
        try {
            return Optional.of(Paths.get(filepath).toFile().getCanonicalFile().getAbsoluteFile());
        } catch (final IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
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
}
