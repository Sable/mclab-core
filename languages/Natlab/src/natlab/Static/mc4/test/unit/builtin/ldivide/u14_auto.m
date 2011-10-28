function [result] = u14_auto(n)
  x0 = double([0.237964627092,0.544229225296,0.369955166548]*n);
  x1 = double([0.956034271889;0.947827487059;0.0565513677268]*n);
  result = ldivide(x0,x1);
end
