function [result] = u0_auto(n)
  x0 = single([0.844421851525]*n);
  x1 = single([0.844421851525]*n);
  result = colon(x0,x1);
end
