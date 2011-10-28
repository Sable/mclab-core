function [result] = u12_auto(n)
  x0 = double([0.237964627092,0.544229225296,0.369955166548]*n);
  x1 = double([0.844421851525]*n);
  result = mod(x0,x1);
end
