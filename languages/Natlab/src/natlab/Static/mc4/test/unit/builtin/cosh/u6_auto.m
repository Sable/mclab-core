function [result] = u6_auto(n)
  x0 = int8([0.956034271889,0.947827487059;0.0565513677268,0.0848719951589]*n);
  result = cosh(x0);
end
