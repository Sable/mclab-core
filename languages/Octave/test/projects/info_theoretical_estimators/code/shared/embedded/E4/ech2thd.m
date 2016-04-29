function [theta, din, labtheta] = ech2thd(k, FR, AR, V, s, G, r)
% ECH2THD - Converts an VARMAX ECHELON model to the THD format.
%    [theta, din, lab] = ech2thd(k, FR, AR, V, s, G, r)
% The ECHELON formulation is:
% (FR0 + FR1·B + ... +FRp·B^p) y(t) = 
%   (G0 + G1·B + ... + Gt·B^l) u(t) + (AR0 + AR1·B + ... + ARq·B^q) a(t)
% where FR0 = AR0
% The function arguments are:
% k < the kronecker indices vector
% FR = [FR0 | ... | FRp], G = [G0 | G1 | ... | Gg], AR = [AR1 | ... | ARq]
%
% The function checks the consistence between the matrices and the kronecker
% indices structure. Use KRON2STR() for build the echelon formulation
%
  if nargin < 5, e4error(3); end
  if nargin < 7
     r = 0;
     G = [];
  end
  k = k(:);
  m = size(k,1);
  n = max(k);
  if size(FR,1) ~= m | (size(AR,1) > 0 & size(AR,1) ~= m) | ( r & size(G,1) ~= m), e4error(5); end
  if size(FR,2) > m*(n+1) | size(FR,2) < m | size(AR,2) > m*n | size(G,2) > r*(n+1), e4error(5); end
  if sum(diag(FR(1:m,1:m)) > 1+100*eps | diag(FR(1:m,1:m)) < 1-100*eps) > 0, e4error(5); end

  [strF, strA, strG] = kron2str(k,r);

  iF = ~strF(:,1:size(FR,2));
  iA = ~strA(:,1:size(AR,2));
  iG = ~strG(:,1:size(G,2));

  if sum(~isnan(FR(iF)))+sum(~isnan(AR(iA)))+sum(~isnan(G(iG))) > 0, e4error(5); end

  FR(1:m,1:m) = FR(1:m,1:m)+diag(NaN*ones(m,1));
  [theta, din, labtheta] = str2thd(FR, [], [NaN*ones(m) AR], [], V, s, G, r);
  [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);
  din0 = e4sthead(4, m, r, s, sum(k), np, 0, [], [innov(1) innov(2)+size(k,1)], [H_D+szpriv(1)+size(k,1);size(k,1)]);
  din = [din0;k;din];
