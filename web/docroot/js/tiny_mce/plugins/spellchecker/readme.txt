The Moxiecode spell check plugin distribution
has been slightly modified for SSF deployments.  You
can replace this with the Moxiecode version if you
wish to use it directly.

The following changes were made:

1. All .php scripts were removed for security reasons.
We don't think there's anything wrong with them, but
we don't use PHP and have no reason to invoke them.

2. Changed the spellcheck AJAX interface to use a
configurable URL instead of a fixed one.  THis can
then be set to a custom spell check backend.
