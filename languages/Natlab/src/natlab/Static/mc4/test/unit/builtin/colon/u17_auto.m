function [result] = u17_auto(n)
  x0 = int8([0.956034271889]*n);
  x1 = ([0.62290169489]*n > .5);
  result = colon(x0,x1);
end
