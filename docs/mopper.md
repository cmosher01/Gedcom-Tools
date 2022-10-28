# Mopper

Copyright © 2017, 2019, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)
[![License](https://img.shields.io/github/license/cmosher01/mopper.svg)](https://www.gnu.org/licenses/gpl.html)

This project is now _deprecated_, and is being archived. (Artifacts are still available at
[Bintray](https://bintray.com/cmosher01/maven/mopper).)

Work had started on a Version 2, in the Arg2.java source file. But that stalled, and the entire project is (for now, at least) on hold.

[Gnopt](https://github.com/cmosher01/Gnopt) is the replacement library. It handles GNU-style arguments like `--log=debug`

---

Mopper is a command-line option processor for Java programs.

This software is distributed under the
[GPLv3](http://www.gnu.org/licenses/gpl-3.0-standalone.html)
license.

Include as dependency in gradle:

```groovy
repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation group: 'nu.mine.mosher.mopper', name: 'mopper', version: 'latest.integration'
}
```

Example:

```java
import nu.mine.mosher.mopper.ArgParser;

public class Foobar {
    public static void main(String[] args) {
        FoobarOpts opts = new ArgParser<>(new FoobarOpts()).parse(args);
        if (opts.foo == null) {
            System.err.println("-f not present");
        } else if (opts.foo.isEmpty()) {
            System.err.println("-f present, but without a value");
        } else {
            System.err.println("-f present, with value: "+opts.foo);
        }
        System.err.flush();
    }

    public static class FoobarOpts {
        public String foo;
        // each method becomes a command line option
        public void f(String val) {
            foo = val;
        }
        public void x() {
            System.err.println("(-x processed)");
        }
    }
}
```

```shell
$ javac -cp example/optvalue:src/main/java example/optvalue/Foobar.java
$ java  -cp example/optvalue:src/main/java Foobar -x
(-x processed)
-f not present
$ java  -cp example/optvalue:src/main/java Foobar -f -x
-f present, with value: -x
$ java  -cp example/optvalue:src/main/java Foobar -f bar -x
(-x processed)
-f present, with value: bar
$ java  -cp example/optvalue:src/main/java Foobar -f
-f present, but without a value
```

More examples are available in the [example](example) directory.
