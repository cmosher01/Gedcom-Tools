package nu.mine.mosher.gedcom;

import com.google.common.base.CaseFormat;

import java.lang.reflect.*;
import java.util.*;

public class GedcomTools {
    private static final Set<String> COMMANDS = Set.of(
        "Attach",
        "CheckDups",
        "CheckLen",
        "Cull",
        "DisplayRaw",
        "Ed",
        "EncodingDetector",
        "Eventize",
        "EventsCsv",
        "Extract",
        "FixDate",
        "FixFtmPubl",
        "Minimal",
        "Notary",
        "NoteGc",
        "Pedigree",
        "Pointees",
        "Redact",
        "Refn",
        "RefnSkel",
        "RestoreHead",
        "RestoreIds",
        "Select",
        "SharedEvents",
        "ShowAll",
        "Sort",
        "TagFromNote",
        "Tools",
        "Uid",
        "UnFtmEvent",
        "UnNote"
        );

    public static void main(String... args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (args.length == 0) {
            die();
        }
        String cmd = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        cmd = cmd.trim();
        cmd = cmd.toLowerCase();
        cmd = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, cmd);
        if (!cmd.equals("Gedcom")) {
            if (!COMMANDS.contains(cmd)) {
                die();
            }
            cmd = "Gedcom" + cmd;
        }
        cmd = GedcomTools.class.getPackageName()+"."+cmd;
        final var cls = Class.forName(cmd);
        final var submain = cls.getMethod("main", String[].class);

        submain.invoke(null, (Object)args);
    }

    private static void die() {
        System.err.println("usage: gedcom-tools command [OPTIONS] ");
        System.err.println("commands:");
        COMMANDS
            .stream()
            .sorted()
            .forEach(c ->
                System.err.println("    "+CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, c)));
        System.err.flush();
        System.exit(1);
    }
}
