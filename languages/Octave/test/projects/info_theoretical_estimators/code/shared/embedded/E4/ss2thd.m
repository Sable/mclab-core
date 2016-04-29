function [theta, din, thetalab] = ss2thd(PHI, GAMMA, E, H, D, C, Q, S, R,sseason)
% SS2THD   - Converts a SS formulation to the equivalent THD format. 
%    [theta, din, lab] = ss2thd(Phi, Gam, E, H, D, C, Q, S, R)
% The SS formulation is:
%    x(t+1) = Phi·x(t) + Gam·u(t) + E·w(t)
%    y(t)   = H·x(t)   + D·u(t)   + C·v(t)
%    V(w(t) v(t)) = [Q S; S' R];
%
% 29/4/97
% Copyright (c) Jaime Terceiro, 1997

% DIN STRUCTURE OF AN SS MODEL
% din(1:H_D) : common header
% din(H_D+1:H_D+3): private header
%    din(H_D+1): diag flag for Phi, Q and R
%    din(H_D+2): Q size
%    din(H_D+3): R size
% din(20:?) : vectorized matrices

if nargin < 7, e4error(3); end

if (size(PHI,1) ~= size(PHI,2) & size(PHI,2) > 1), e4error(15); end
if ((size(GAMMA,1) > 0 & (size(GAMMA,1) ~= size(PHI,1)) | (size(D,2) > 0 & size(D,2)~=size(GAMMA,2)))), e4error(15); end
if (size(E,1) > 0 & size(E,1) ~= size(PHI,1)), e4error(15); end
if (size(E,1) == 0 & size(Q,1) > 0 & size(Q,1) ~= size(PHI,1)), e4error(15); end
if (size(E,2) > 0 & size(E,2) ~= size(Q,1)), e4error(15); end
if (size(Q,1) ~= size(Q,2) & size(Q,2) ~= 1), e4error(15); end
if (size(H,2) ~= size(PHI,1)), e4error(15); end
if (size(D,1) > 0 & size(D,1) ~= size(H,1)), e4error(15); end
if (size(C,1) > 0 & size(C,1) ~= size(H,1)), e4error(15); end

if nargin > 7
   if (size(C,1) == 0 & size(R,1) > 0 & size(R,1) ~= size(H,1)), e4error(15); end
   if (size(C,2)  > 0 & size(C,2) ~= size(R,1)), e4error(15); end
   if (size(R,1) ~= size(R,2) & size(R,2) ~= 1), e4error(15); end
   if (size(S,1) > 0 & (size(S,1) ~= size(Q,1) | size(S,2) ~= size(R,1))), e4error(15); end
else
   if (size(C,1) == 0 & size(Q,1) > 0 & size(Q,1) ~= size(H,1)), e4error(15); end
   if (size(C,2) > 0 & size(C,2) ~= size(Q,1)), e4error(15); end
end

if nargin < 10, sseason = 1; end

vars  = e4strmat('PHI','GAMMA','E','H','D','C','Q','S','R');

issym = zeros(9,1);
issym([7,9]) = [1;1];

din = zeros(3,1);
din(2) = size(Q,1);

if (size(Q,2) == 1)
   Q = diag(Q) + tril(ones(size(Q,1))*NaN,-1) + triu(ones(size(Q,1))*NaN,1);
   din(1) = 1;
end
if nargin > 7
   if (size(R,2) == 1)
      R = diag(R) + tril(ones(size(R,1))*NaN,-1) + triu(ones(size(R,1))*NaN,1);
      din(1) = din(1) + 2;
   end
   din(3) = size(R,1);
else
   R = [];
   S = [];
   din(3) = din(2);
end
n = size(PHI,1);
m = size(H,1);
if (size(PHI,2) == 1)
   PHI = diag(PHI) + tril(ones(n)*NaN,-1) + triu(ones(n)*NaN,1);
   din(1) = din(1) + 4;
end

theta = [];
thetalab = [];

for i = 1:min(nargin,9)
    [theta0, din0, labl] = e4vec(eval(vars(i,:)), deblank(vars(i,:)), issym(i));
    theta = [theta;theta0];
    if size(labl,1) > 0, thetalab = e4strmat(thetalab, labl); end
    din = [din;size(theta0,1);din0];
end

if (nargin < 8 & size(Q,1) == m)
   innov = [1 size(din,1)-size(din0,1)];
else
   innov = [0 din(2:3)'];
end
din = [din;zeros(nargin-9,1)];

if size(theta,1) < 1, e4error(5); end
thetalab = thetalab(2:size(thetalab,1), :);
din0 = e4sthead(7, m, max([size(GAMMA,2),size(D,2)]), sseason, n, size(theta,1), 0, [], innov, [size(din,1);3]);
din = [din0;din];