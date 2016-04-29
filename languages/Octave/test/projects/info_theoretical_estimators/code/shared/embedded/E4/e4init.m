% E4INIT   - Initializes the global variables in the toolbox.
%    e4init
% This function must be executed once at the toolbox operation beginning.
%
% 11/3/97

% Copyright (C) 1997 Jaime Terceiro
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

function e4init

global E4OPTION ERRORSTR WARNSTR DISPSTR
if isempty(E4OPTION)
   E4OPTION = zeros(1,51);
end

fprintf(1,'\n');
disp('                      EEEEEEEEE   444  444'); 
disp('                     EEEEEEEEEEE  444  444');
disp('                     EEE          44444444');          
disp('                     EEE           4444444');
disp('                     EEEEEEE           444');
disp('                     EEEEEEE           444');
disp('                     EEE');
disp('                     EEE');
disp('                     EEEEEEEEEE'); 
disp('                      EEEEEEEE');

fprintf(1,'\n');
disp('Toolbox for State Space Estimation of Econometric Models');
disp('                   Version  JAN-2012');
fprintf(1,'\n');
disp('Web: www.ucm.es/info/icae/e4');

sete4opt;
% Initialization of error, warning and display strings
ERRORSTR = ['1.  THETA and DIN do not fit                                                   ';
            '2.  i inconsistent with THETA (out of range)                                   ';
            '3.  Incorrect number of arguments                                              ';
            '4.  Badly conditioned covariance matrix                                        ';
            '5.  Incorrect model specification                                              ';
            '6.  Only one series allowed                                                    ';
            '7.  Should be more than 1 observation                                          ';
            '8.  Model %1d inconsistent                                                     ';
            '9.  Endogenous variables model should be simple                                ';
            '10. Model not identified                                                       ';
            '11. Inconsistent input arguments                                               ';
            '12. Inconsistent error model                                                   ';
            '13. User function should be passed as argument in user models                  ';
            '14. Incorrect model                                                            ';
            '15. Inconsistent system matrix dimension                                       ';
            '16. Impossible to compute with missing data                                    ';
            '17. Invalid number of lags                                                     ';
            '18. File not found: %s                                                         ';
            '19. The equation has no solution                                               ';
            '20. SETE4OPT. Unrecognized option %s                                           ';
            '21. SETE4OPT. Unrecognized value %s                                            ';
            '22. SETE4OPT. Invalid value for %s                                             ';
            '23. Run E4INIT before using E4                                                 ';
            '24. Use ARMA2THD for ARMA models                                               ';
            '25. Initial conditions are meaningless                                         ';
            '26. Non-stationary system. Initial conditions not compatible with Chandrasekhar';
            '27. E4MIN. No decision variables; check second column of THETA                 ';
            '28. E4LNSRCH. THETA vector is meaningless                                      ';
            '29. Multivariate time-varying parameters models are not supported              ';
            '30. The sample size should be an integer multiple of the seasonal period       ';
            '31. SETE4OPT. If vcond=De Jong, filter must be Kalman                          ';
            '32. Argument should be scalar                                                  ';
            '33. E4MIN. Objective function not found                                        ';
            '34. For this type of model ARMA2THD or STR2THD should be used                  ';
            '35. Not enough data for using e4preest()                                       ';
            '36. User function not defined for the analytic gradient                        ';
            '37. Function only compatible with new versions of matlab (uses cell arrays)    ';            
            ];

WARNSTR = ['1.  Should be one title per series                                         ';
           '2.  Invalid number of lags                                                 ';
           '3.  Invalid %s option                                                      ';
           '4.  PLOTSERS. A maximum of seven series can be represented in mode 2       ';
           '5.  RMEDSERS. Invalid group length                                         ';
           '6.  LFMODINI. Roots within the circle of radius 1                          ';
           '7.  Approximate computation of information matrix                          ';
           '8.  Information matrix sd+ o d-. Pseudo-inverse computed                   ';
           '9.  E4MIN. Surpassed the maximum number of iterations                      ';
           '10. - - - - - empty - - - - -                                              ';
           '11. E4MIN. Hessian reinitialized                                           ';
           '12. - - - - - empty - - - - -                                              ';
           '13. E4LNSRCH. Precision problem                                            ';
           '14. CHOLP. Matrix not square                                               ';
           '15. Kalman filter will be used                                             ';
           '16. E4MIN. Analytic gradient function not found. Numeric approximation used';
           '17. ARE has no solution because system is non detectable                   ';           
           '18. Newton algorithm applied in ARE. NOT convergence after %2d iterations  ';
           '19. Newton algorithm applied in ARE. Convergence achieved after %2d iter.  '];           
           

DISPSTR = ['\nIteration: %4d | Objf: %8.4f        ';
           '   x =                                ';
           '   g =                                ';
           '  x0 =                                ';
           '  g0 =                                ';
           'Maximum step length: %8.4f            ';
           'Step =                                ';
           'E4MIN. Convergence reached in x       ';
           'E4MIN. Convergence reached in gradient';
           ];
