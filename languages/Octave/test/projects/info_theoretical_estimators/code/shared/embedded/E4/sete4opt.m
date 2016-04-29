function [opt] = sete4opt(o1,v1,o2,v2,o3,v3,o4,v4,o5,v5,o6,v6, ...
                   o7,v7,o8,v8,o9,v9,o10,v10)
% SETE4OPT - Allows user to modify the global toolbox options.
%    opt = sete4opt('option', 'value', ...)
% Different calls have different effects:
%   sete4opt         set default options
%   sete4opt('show') displays established options (only argument)
% The general syntax is:
%   sete4opt('option', 'value', ...)
% up to a maximum of 5 pairs option-value (ten arguments in total).
% Option   Admissible values
% ------   --------------------------------------------------------------
% filter   kalman, chandrashekhar.
% scale    Matrix scaling during filtering operations: 'yes', 'no'.
% vcond    initial conditions for the variance of the state vector:
%           'lyapunov', 'zero', 'idej' (Inverse De Jong).
% econd    initial conditions for the expectation of the state vector:
%           'ml', 'zero', 'au', 'iu', 'auto'.
% var      optimize with respect to the noise covariances or the
%          corresponding Cholesky factors.
% algorit  optimization algorithm. Choices are: 'bfgs' (Broyden-Fletcher-
%           Goldfarb-Shanno) or 'newton' (Newton-Raphson).
% step     maximum step length in each iteration.
% toler    tolerance for stop criteria.
% maxiter  maximum number of iterations.
% verbose  shows the iterative output of the optimizer ('yes','no').
%
% 7/3/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

nargin2 = nargin;

if ~exist('E4OPTION'), e4error(23); end
if ((nargin2 > 1) & (rem(nargin2,2) ~= 0)) | (nargin2 > 20)
   e4error(3);
end

if nargin2 == 0 % Default values
   E4OPTION(1) = 1;   % Kalman filter
   E4OPTION(2) = 0;   % No scaling of B
   E4OPTION(3) = 4;   % Inverse De Jong
   E4OPTION(4) = 5;   % Automatic selection
   E4OPTION(5) = 0;   % Variances
   E4OPTION(6) = 1;   % BFGS algorithm
   E4OPTION(7) = 0.1; % step length
   E4OPTION(8) = 1.0e-5; % stop tolerance
   E4OPTION(9) = 75;  % Maximum number of iterations
   E4OPTION(10)= 1;   % Verbose optimization

   % Options not user modifiable
   E4OPTION(11)= 1.0e-5;  % Box-Cox trans. mimimum value
   E4OPTION(12)= 1.0 - 1.0e-5; % Tolerance for unitary eigenvalues
   E4OPTION(13)= 1.0e-10; % DJCCL: pseudoinverse tolerance for M
   E4OPTION(14)= 1.0e-4;  % DJCCL: pseudoinverse tolerance for Q
   E4OPTION(15)= 1.0e-10; % pseudoinverse general tolerance (LFMOD)
   E4OPTION(16)= 1.0e5;   % LFMODINI: De Jong's k
   E4OPTION(17)= 1.0e-6;  % FISMOD: pseudoinverse tolerance
   if exist('OCTAVE_VERSION')
      E4OPTION(18) = 1; % OCTAVE compatibility
      eval('warning(''off'',''Octave:possible-matlab-short-circuit-operator'');');
    % eval('do_braindead_shortcircuit_evaluation(1)');
   else
      E4OPTION(18) = 0; % MATLAB compatibility 
   end
   v = version;
   p = findstr(v,'.');
   vv = str2num(v(1:p(min(2,size(p,2)))-1)); % Version number
   if isempty(vv), vv = str2num(v(1:p(1)-1)); end
   E4OPTION(19) = vv;   
   opt = E4OPTION;
   disp(' ');
%  disp('************************ Default values ************************');
%  disp(' ');
   nargin2 = 1; o1 = 'show'; % Default
end

if nargin2 == 1
   if size(o1,2) < 3, e4error(20, o1); end
   if (o1(1:3) == 'sho')
%   disp(' '); disp(' ');
    disp('*********************** Options set by user ********************');
    if E4OPTION(1) == 1, s1 = 'KALMAN'; else s1 = 'CHANDRASEKHAR'; end
    if E4OPTION(2) == 1, s2 = 'YES'; else s2 = 'NO'; end
    if E4OPTION(3) == 1, s3 = 'LYAPUNOV'; elseif E4OPTION(3) == 2, s3 = 'ZERO';
       elseif E4OPTION(3) == 3 s3 = 'DJONG';
       else s3 = 'IDEJONG'; end
    if E4OPTION(4) == 1, s4 = 'u0 = EXOGENOUS MEAN';
    elseif E4OPTION(4) == 2, s4 = 'MAXIMUM LIKELIHOOD';
    elseif E4OPTION(4) == 3, s4 = 'ZERO';
    elseif E4OPTION(4) == 4, s4 = 'u0 = EXOGENOUS FIRST VALUE (u1)';
    elseif E4OPTION(4) == 5, s4 = 'AUTOMATIC SELECTION';
    end
    if E4OPTION(5) == 1, s5 = 'FACTOR'; else s5 = 'VARIANCE'; end
    if E4OPTION(6) == 1, s6 = 'BFGS'; else s6 = 'NEWTON'; end
    if E4OPTION(10) == 1, s10 = 'YES'; else s10 = 'NO'; end
    
    disp(['Filter. . . . . . . . . . . . . : ' s1]);
    disp(['Scaled B and M matrices . . . . : ' s2]);
    disp(['Initial state vector. . . . . . : ' s4]);
    disp(['Initial covariance of state v.  : ' s3]);
    disp(['Variance or Cholesky factor? .  : ' s5]);
    disp(['Optimization algorithm. . . . . : ' s6]);
    disp(['Maximum step length . . . . . . : ' sprintf('%8.6f', E4OPTION(7))]);
    disp(['Stop tolerance. . . . . . . . . : ' sprintf('%8.6f', E4OPTION(8))]);
    disp(['Max. number of iterations . . . : ' sprintf('%8i', E4OPTION(9))]);
    disp(['Verbose iterations. . . . . . . : ' s10]);
    disp('****************************************************************');
    disp(' '); disp(' ');
   else
    e4error(20, o1);
   end
   return
end

disp(' '); disp(' ');
disp('************** The following options are modified **************');
for i=1:(nargin2/2)
   optstr = lower(eval(['o' int2str(i)]));
   optval = eval(['v' int2str(i)]);
   if size(optstr,2) < 3, e4error(20, optstr); end
   if size(optval,2) < 1, e4error(22, optsrt); end
   if optstr(1:3) == 'fil'
      optval = lower(optval);
      if optval(1) == 'k', E4OPTION(1) = 1;
      elseif optval(1) == 'c'
         if E4OPTION(3) == 3
	        e4error(31);
	     else
            E4OPTION(1) = 2;
	     end
      else e4error(21, optval); end
      disp(['Filter. . . . . . . . . . . . . : ' upper(optval) ]);
      
   elseif (optstr(1:3) == 'esc') | (optstr(1:3) == 'sca')
      optval = lower(optval);
      if (optval(1) == 's') | (optval(1) == 'y'), E4OPTION(2) = 1;
      elseif optval(1) == 'n', E4OPTION(2) = 0;
      else e4error(21, optval); end
      disp(['Scaled B and M matrices . . . . : ' upper(optval)]);
      
   elseif optstr(1:3) == 'vco'
      optval = lower(optval);
      if optval(1) == 'l', E4OPTION(3) = 1;
      elseif (optval(1) == 'c') | (optval(1) == 'z'), E4OPTION(3) = 2;
%      elseif optval(1) == 'd' | optval(1) == 'j'
%         if E4OPTION(1) == 2
%	        e4error(31);
%	     else
%            E4OPTION(3) = 3;
%         end
      elseif optval(1) == 'i' E4OPTION(3) = 4;
      else e4error(21,optval); end
      disp(['Initial covariance of state v.  : ' upper(optval)]);

      
   elseif optstr(1:3) == 'eco'
      optval = lower(optval);
      if optval(1) == 'a' & size(optval,2) < 3, E4OPTION(4) = 1;
      elseif optval(1) == 'm', E4OPTION(4) = 2;
      elseif (optval(1) == 'c') | (optval(1) == 'z'), E4OPTION(4) = 3;
      elseif optval(1) == 'i', E4OPTION(4) = 4;
      elseif optval(1:3) == 'aut', E4OPTION(4) = 5;
      else e4error(21,optval); end
      disp(['Initial state vector. . . . . . : ' upper(optval)]);
      
   elseif optstr(1:3) == 'var'
      optval = lower(optval);
      if optval(1) == 'v', E4OPTION(5) = 0;
      elseif optval(1) == 'f', E4OPTION(5) = 1;
      else e4error(21,optval); end
      disp(['Variance or Cholesky factor? .  : ' upper(optval)]);
      
   elseif optstr(1:3) == 'alg'
      optval = lower(optval);
      if optval(1) == 'b', E4OPTION(6) = 1;
      elseif optval(1) == 'n', E4OPTION(6) = 2;
      else e4error(21,optval); end
      disp(['Optimization algorithm. . . . . : ' upper(optval)]);

   elseif (optstr(1:3) == 'pas') | optstr(1:3) == 'ste'
      s4 = sprintf('%8.6f',optval);
      if optval > 0
            E4OPTION(7) = optval;
            disp(['Maximum step length . . . . . . : ' s4]);
      else e4error(22,s4);
      end

   elseif optstr(1:3) == 'tol'
      s4 = sprintf('%8.6f',optval);
      if optval > 0
            E4OPTION(8) = optval;
            disp(['Stop tolerance. . . . . . . . . : ' s4]);
      else e4error(22,s4);
      end
      
   elseif optstr(1:3) == 'max'
      s4 = sprintf('%8i', fix(optval));
      if optval > 0
            E4OPTION(9) = fix(optval);
            disp(['Max. number of iterations . . . : ' s4]);
      else e4error(22,s4);
      end

   elseif optstr(1:3) == 'ver'
      optval = lower(optval);
      if (optval(1) == 's') | (optval(1) == 'y'), E4OPTION(10) = 1;
      elseif optval(1) == 'n', E4OPTION(10) = 0;
      else e4error(21,optval); end
      disp(['Verbose iterations. . . . . . . : ' upper(optval)]);

   else
      e4error(20, optstr);
   end
end
opt = E4OPTION;
disp('****************************************************************');
disp(' '); disp(' ');
