## Test cell construction operator {}

get = {1 2 {3 4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1, 2 {3 4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1 2, {3 4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1 2 {3, 4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1, 2, {3 4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1 2,{3 4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1 2,{3,4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1,2,{3 4}};
expect = {1,2,{3,4}};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
