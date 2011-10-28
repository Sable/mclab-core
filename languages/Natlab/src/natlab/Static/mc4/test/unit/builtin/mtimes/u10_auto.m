function [result] = u10_auto(n)
  x0 = double([0.956034271889;0.947827487059;0.0565513677268]*n);
  x1 = double([0.956034271889;0.947827487059;0.0565513677268]*n);
  result = mtimes(x0,x1);
end
