function  [pcnew,iter,fnew,gnew,hessian] = e4min(func,p,dfunc,P1,P2,P3,P4,P5,P6,P7,P8)
% E4MIN    - General purpose minimizer.
%    [pnew, iter, fnew, g, h] = e4min(func, p, dfunc, P1,P2,P3,P4,P5,P6,P7,P8)
% From an initial point p, iterates over func using the gradient computed by
% dfunc or by fstofd if dfunc is an empty string.
% This function calls e4lnsrch to perform unidimensional searches.
% func     > name of the objective function.
% p        > initial point (usually an initial estimation of the parameters).
% dfunc    > name of the function that computes the analytical gradient.
%            If the value is '', the gradient is computed by finite differences.
% pnew     < vector of decision variables at the optimum.
% iter     < number of iterations.
% fnew     < value of the objective function at the optimum.
% g        < gradient at the optimum.
% h        < hessian (or numerical approximation) at the optimum.
% The optional parameters P1-P5 are feed to the objective function without
% modification.
%
% 11/3/97
% Copyright (c) Jaime Terceiro, 1997

% Based on: NR in C, 2nd. ed., pag. 428-9. Dennis & Schnabel BFGSUNFAC pag. 355

global E4OPTION

if nargin < 2
    e4error(3);
else
    analgrad = 0;
    if ~exist(func), e4error(33); end
    if (nargin >= 3) & ~isempty(dfunc)
        if exist(dfunc)
            analgrad = 1;
        else
            e4warn(16);
        end
    end
end
parms  = getparms(nargin-3);

method = E4OPTION(6); % 1-BFGS, 2-Newton
stpmx  = E4OPTION(7);
gtol   = E4OPTION(8);
itmax  = E4OPTION(9);

zeps  = eps;
tolx  = 4*zeps;  % Stop tolerance on x
rnoise= 1.0e-12; % Evaluation noise of the objective function

pc    = p;
pcnew = p;

n = size(p,1);
if ~n, e4error(27); end
if size(p,2) > 1, index = find(p(:,2) == 0);
else              index = (1:n)'; end

if size(index,1)
    p = p(index,1);
    n = size(p,1);
else
    e4error(27);
end

if stpmx == 0, stpmx = 0.1; end
stpmax = stpmx*max(sqrt(p'*p), n);
sx = ones(n,1);
if any( p(:,1)~=0.0 )
   sx( p(:,1)~=0.0 ) = 1.0./abs(p( p(:,1)~=0.0 , 1));
end

if analgrad
    g_str = [dfunc '(pcnew' parms ')'];
    h_str = ['fstofd(pcnew,index,dfunc,gnew,sx,rnoise,3' parms ')'];
else
    g_str = ['fstofd(pcnew,index,func,fnew,sx,rnoise,1' parms ')'];
    h_str = ['sndofd(pcnew,index,func,fnew,sx,rnoise' parms ')'];
end

fp   = eval([func '(pcnew' parms ')']);
fnew = fp;
g    = eval(g_str);
gnew = g;
e4disp(1, 0, fp); e4disp(4, p); e4disp(5, g); e4disp(6, stpmax);

if method == 1     % BFGS
   hessian = abs(fp)*diag(sx.*sx);
elseif method == 2 % Newton
   hessian = eval(h_str);
end
r  = hessp(hessian);
xi = r\ (r'\(-gnew));

% Main loop
for its = 1:itmax
    iter = its;
    eval([ '[pnew,fnew,check] = e4lnsrch(func,pc,index,fp,g,xi,stpmax' parms ');']);
    pcnew(index,1) = pnew;
    e4disp(1, iter, fnew);  e4disp(2, pnew);
    if check == 2
        e4warn(11);
        hessian = eye(n,n);
    end
    xi = pnew-p;

    test = max( abs(xi)./max(abs(pnew),1.0) );
    if test < tolx, e4disp(8); return; end
    den  = max(fnew,1.0);

    gnew  = eval(g_str);
    e4disp(3, gnew);
    test = max( abs(gnew).*(max(abs(pnew),1.0)./den) );
    if test < gtol, e4disp(9); return; end

    if method == 1
       dg    = gnew - g;
       tmp1  = dg'*xi;
       if tmp1 >= sqrt(eps)*sqrt(dg'*dg)*sqrt(xi'*xi)
          skipup = 1;
          if    analgrad, tol = rnoise;
          else  tol = sqrt(rnoise); end
          t = hessian*xi;
          if any( abs(dg-t) >= tol*max(abs(g),abs(gnew)) )
             skipup = 0;
          end
          if ~skipup
             tmp2 = xi'*t;
             hessian = hessian + (dg*dg')/tmp1 - (t*t')/tmp2;
          end
       end
    elseif method == 2   % Newton
       hessian = eval(h_str); 
    end
    r  = hessp(hessian);
    xi = r\ (r'\(-gnew));
    p  = pnew;
    pc = pcnew;
    fp = fnew;
    g  = gnew;
end
e4warn(9);