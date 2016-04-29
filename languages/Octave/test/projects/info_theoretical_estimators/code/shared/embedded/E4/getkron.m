function ki = getkron(Phi,H)
% getkron  - Gets the Krocneker indices (observability indices) from
% matrices Phi and H of SS representation
%    ki = getkron(Phi,H)
% Internal function called from detcomp. The purpose of this function is't
% to identify the Kronecker indices from the observability matrix may be
% contaminated by noise. This function assume that matrices Phi and H are
% known and just evaluates the linear dependence among the rows of the
% obsevability matrix O = [H' (H*Phi)' (H*Phi^2)' ... (H*Phi^(n-1))]'.
% The function returns a vector ki with the Kronecker indices, so that
% sum(ki) <= n = size(Phi,1). If (H,Phi) is observable, then sum(ki) = n.
%
% 7/4/2006

% Copyright (C) 2006 José Casals, Miguel Jerez, Sonia Sotoca
% 
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2, or (at your option)
% any later version.
% 
% This program is distributed in the hope that it will be useful, but
% WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% General Public License for more details. 
% 
% You should have received a copy of the GNU General Public License
% along with this file.  If not, write to the Free Software Foundation,
% 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

n = size(Phi,1);
m = size(H,1);
ki = zeros(m,1);
nk = 0;
k = (1:m)';
m2 = m;

if n == 0, return; end

C = e4obsv(Phi,H);

for i=1:n
    ik = ones(m2,1) == 1;
    for j=1:m2
        r = rank(C(1:(i-1)*m+k(j),:));
        if r > nk
           ki(j) = ki(j) + 1;
           nk = nk + 1;
        else
           ik(j) = 0;
        end        
    end
    if nk == n, break; end
    m2 = sum(ik);
    k = k(ik);
end          

