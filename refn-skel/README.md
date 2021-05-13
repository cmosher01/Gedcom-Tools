# Gedcom-Refn-Skel

Copyright © 2018, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)
[![License](https://img.shields.io/github/license/cmosher01/Gedcom-Refn-Skel.svg)](https://www.gnu.org/licenses/gpl.html)

Reads GEDCOM files, and looks for people (INDI) with no Person ID (REFN).
For those people, it adds a REFN, and writes out a skeleton records for
them. The output file is named as the input file with ".skel" appended
to the file name. This file is intended to be merged (by some other
genealogy program) into a similar genealogy file.
