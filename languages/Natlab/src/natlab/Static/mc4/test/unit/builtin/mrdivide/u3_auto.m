function [result] = u3_auto(n)
  x0 = double([0.844421851525]*n);
  x1 = double([0.237964627092,0.544229225296,0.369955166548]*n);
  result = mrdivide(x0,x1);
end
