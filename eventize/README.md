# Gedcom-Eventize

Copyright © 2017, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)
[![License](https://img.shields.io/github/license/cmosher01/Gedcom-Eventize.svg)](https://www.gnu.org/licenses/gpl.html)

Turn GEDCOM tags into generic events, creating
standard `EVEN`, `TYPE`, and `NOTE` tags.

## custom tag

This example shows how to convert a user-define tag into
a standard `EVEN` structure. It will convert all
`_MILT` events into `EVEN`s of `TYPE` "military".
Any value on the `_MILT` line will get converted
into a (new) `NOTE` for the `EVEN`.

With this input:

    0 @I1@ INDI
         1 _MILT Age: 23

the command:

    gedcom-eventize  --where=".*._MILT"  --type="military"

produces:

    0 @I1@ INDI
         1 EVEN
              2 TYPE military
              2 NOTE Age: 23



## standardize values

This example shows how to convert values on lines
into (new) subordinate `NOTE`s.
This is mostly intended to be used on events where
the GEDCOM standard prohibits values, in order to
move the values into a standard position (on a `NOTE`).
This example moves the (unallowed) value from any `RESI`
event lines into a subordinate `NOTE`.

With this input:

    0 @I1@ INDI
         1 RESI Age: 23

the command:

    gedcom-eventize  --where=".*.RESI"

produces:

    0 @I1@ INDI
         1 RESI
              2 NOTE Age: 23
