function [result] = u2_auto(n)
  x0 = double([0.956034271889;0.947827487059;0.0565513677268]*n);
  result = atan2(x0);
end
