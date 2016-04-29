## Level 5 (element-wise and)
get = 0 & 1|1;
expect = true;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = [0 1] & 1&&1;
expect = false;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 0 & 1||1;
expect = true;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
a = 2;
a *= 3 & 1;
get = a;
expect = 2;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif


clear;
## Level 5 (element-wise and)
## No tests possible.  Only one operator (&) at this precedence level
## and operation is associative.