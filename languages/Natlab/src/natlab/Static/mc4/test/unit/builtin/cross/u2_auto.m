function [result] = u2_auto(n)
  x0 = double([0.844421851525]*n);
  x1 = double([0.956034271889;0.947827487059;0.0565513677268]*n);
  result = cross(x0,x1);
end
