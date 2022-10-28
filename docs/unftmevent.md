# Gedcom-UnFtmEvent

Copyright © 2017, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)
[![License](https://img.shields.io/github/license/cmosher01/Gedcom-UnFtmEvent.svg)](https://www.gnu.org/licenses/gpl.html)

Undoes Family Tree Maker’s turning unknown event tags (from imported
GEDCOM files) into `EVEN` records.

When importing a GEDCOM file with unknown
individual event tags, such as this:

    0 INDI
         1 _FOO value

into Family Tree Maker,
upon exporting to GEDCOM, will become this:

    0 INDI
         1 EVEN value
              2 TYPE _FOO

This program undoes that action, thus restoring the original tag.
In the example above, you would pass `--tag=_FOO` as the option
to the program.
