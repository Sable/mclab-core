% ***** BEGIN LICENSE BLOCK *****
% Version: MPL 1.1/GPL 2.0/LGPL 2.1
% 
% The contents of this file are subject to the Mozilla Public License Version
% 1.1 (the "License"); you may not use this file except in compliance with
% the License. You may obtain a copy of the License at
% http://www.mozilla.org/MPL/
% 
% Software distributed under the License is distributed on an "AS IS" basis,
% WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
% for the specific language governing rights and limitations under the
% License.
% 
% The Original Code is Fast Kernel Independent Component Analysis using
% an Approximate Newton Method.
% 
% The Initial Developers of the Original Code are
% Stefanie Jegelka, Hao Shen,  Arthur Gretton, and Francis Bach.
% Portions created by the Initial Developers are Copyright (C) 2007
% the Initial Developers. All Rights Reserved.
% 
% Contributors:
% Stefanie Jegelka,
% Hao Shen,
% Arthur Gretton,
% Francis Bach
% 
% Alternatively, the contents of this file may be used under the terms of
% either the GNU General Public License Version 2 or later (the "GPL"), or
% the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
% in which case the provisions of the GPL or the LGPL are applicable instead
% of those above. If you wish to allow use of your version of this file only
% under the terms of either the GPL or the LGPL, and not to allow others to
% use your version of this file under the terms of the MPL, indicate your
% decision by deleting the provisions above and replace them with the notice
% and other provisions required by the GPL or the LGPL. If you do not delete
% the provisions above, a recipient may use your version of this file under
% the terms of any one of the MPL, the GPL or the LGPL.
% 
% ***** END LICENSE BLOCK *****


function [outScore]=hsicChol(ks,N,m)
%
% function [outScore]=hsicChol(ks,N,m)
%
% HSIC of the estimate represented by ks
% ks:   array, where ks{i} is an N x d_i matrix R such that RR' is the kernel matrix for
%       source i
% N:    number of samples
% m:    number of sources


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

dimZ=[0];
allZMatrices=[];

% centre matrices
for i=1:m
    G=centerpartial(ks{i}); 
    %note: R=G(Pvec,:) is defined such
    %that RR'=K (kernel matrix). This is
    %then fed into centering program
    %size of G is N * d_i, where d_i << N
    allZMatrices=[allZMatrices G];
    dimZ=[dimZ;size(G,2)];
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%construct the contrast matrix 
dimZcumulative=cumsum(dimZ);

outScore = 0;
for k=1:m-1
    for l=k+1:m
        Zk = allZMatrices(:,dimZcumulative(k)+1:dimZcumulative(k+1));
        Zl = allZMatrices(:,dimZcumulative(l)+1:dimZcumulative(l+1));
        outScore = outScore + trace ( (Zl'*Zk)*(Zk'*Zl) ); 
    end
end
outScore = outScore*1/N^2;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function G2=centerpartial(G1)
% CENTERPARTIAL - Center a gram matrix of the form K=G*G'

[N,NG]=size(G1);
G2 = G1 - repmat(mean(G1,1),N,1);
