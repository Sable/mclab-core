function [e_hat,W_hat] = cfastica_public(x,defl)
% Complex FastICA
% Ella Bingham 1999
% Neural Networks Research Centre, Helsinki University of Technology

% This is simple Matlab code for computing FastICA on complex valued signals.
% The algorithm is reported in:
% Ella Bingham and Aapo Hyvärinen, "A fast fixed-point algorithm for 
% independent component analysis of complex valued signals", International 
% Journal of Neural Systems, Vol. 10, No. 1 (February, 2000) 1-8.

% When using the code, please refer to the abovementioned publication.

% Nonlinearity G(y) = log(eps+y) is used in this code; this corresponds to 
% G_2 in the above paper with eps=a_2.

% Some bugs corrected on Oct 2003, thanks to Ioannis Andrianakis for pointing them out

% Compatible with GPLv2 or later. http://www.gnu.org/licenses/gpl-2.0.txt

% Copyright (C) 1999  Ella Bingham
%
% This program is free software; you can redistribute it and/or
% modify it under the terms of the GNU General Public License
% as published by the Free Software Foundation; either version 2
% of the License, or (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with this program; if not, write to the Free Software
% Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
% 02110-1301, USA.
%
%Modification (Zoltan Szabo, Sep. 2012): whitening is carried out as a preprocessing step, separately; source generation, figures, plots: deleted.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

eps = 0.1; % epsilon in G
%defl = 1; % components are estimated one by one in a deflationary manner; set this to 0 if you want them all estimated simultaneously
n = size(x,1);

% FIXED POINT ALGORITHM
if defl % Components estimated one by one
  W = zeros(n,n);
  maxcounter = 40;
  for k = 1:n
    w = rand(n,1) + i*rand(n,1);
    counter = 0;
    wold = zeros(n,1);
    while min(sum(abs(abs(wold) - abs(w))), maxcounter - counter) > 0.001;
      wold = w;
      g = 1./(eps + abs(w'*x).^2);
      dg = -1./(eps + abs(w'*x).^2).^2;
      w = mean(x .* (ones(n,1)*conj(w'*x)) .* (ones(n,1)*g), 2) - ...
	  mean(g + abs(w'*x).^2 .* dg) * w;
      w = w / norm(w);
      % Decorrelation:
      w = w - W*W'*w;
      w = w / norm(w);
      counter = counter + 1;
      G = log(eps + abs(w'*x).^2);
    end
    W(:,k) = w;
    counters(k) = counter;
  end; 

else %symmetric approach, all components estimated simultaneously
  maxcounter = 10;
  counter = 0;
  W = randn(n,n) + i*randn(n,n);
  while counter < maxcounter;
    for j = 1:n
      gWx(j,:) = 1./(eps + abs(W(:,j)'*x).^2);
      dgWx(j,:) = -1./(eps + abs(W(:,j)'*x).^2).^2;
      W(:,j) = mean(x .* (ones(n,1)*conj(W(:,j)'*x)) .* (ones(n,1)*gWx(j,:)),2) - mean(gWx(j,:) + abs(W(:,j)'*x).^2 .* dgWx(j,:)) * W(:,j);
    end;
    % Symmetric decorrelation:
    %W = W * sqrtm(inv(W'*W));
    [E,D] = eig(W'*W);
    W = W * E * inv(sqrt(D)) * E';
    counter = counter + 1;
    GWx = log(eps + abs(W'*x).^2);
  end
  
end

W_hat = W';
e_hat = W_hat * x;
