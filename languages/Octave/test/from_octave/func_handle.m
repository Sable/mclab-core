
## Check that a cell array containing function handles is parsed
## correctly with or without commas.
a = {1, @sin, 2, @cos};
b = {1 @sin 2 @cos};
get = a;
expect = b;
#{
if (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
#}