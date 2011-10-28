function [result] = u9_auto(n)
  x0 = ([0.62290169489,0.741786989261;0.795193565566,0.942450283777]*n > .5);
  result = sinh(x0);
end
