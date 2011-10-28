function [result] = u7_auto(n)
  x0 = uint64([0.237964627092,0.544229225296;0.369955166548,0.603920038596]*n);
  result = conj(x0);
end
