function [result] = u0_auto(n)
  x0 = double([0.844421851525]*n);
  x1 = double([0.844421851525]*n);
  result = minus(x0,x1);
end
