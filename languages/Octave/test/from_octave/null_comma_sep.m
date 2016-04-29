## bug #43113 using null comma-separated list in constructor
z = cell (1,2,3,0,5);
get = {1, z{:}, 2};
expect = {1, 2};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1; z{:}; 2};
expect = {1; 2};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1 2; z{:}; 3 4};
expect = {1, 2; 3 4};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}
get = {1 2; 5 z{:} 6; 3 4};
expect = {1, 2; 5 6; 3 4};
#{
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}