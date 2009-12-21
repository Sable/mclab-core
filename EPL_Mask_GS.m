function[x,y,T_C]=EPL_Mask_GS(M,N)
%[]=EPL_Mask_GS
%
% This function determines the temperature distribution in an EPL_Mask using Gauss-Seidel iteration
%
% Inputs:
% M - number of nodes in the x-direction (-)
% N - number of nodes in the y-direction (-)
    %INPUTS
   
    W=0.001;                            % width of membrane (m)
    th=0.75e-6;                         % thickness of membrane (m)
    q_dot_flux=5000;                    % incident energy (W/m^2)
    k=150;                              % conductivity (W/m-K)
    T_mh=20+273.2;                      % mask holder temperature (K)
    T_infinity=20+273.2;                % ambient air temperature (K)
    h_bar=20;                           % heat transfer coefficient (W/m^2-K)
    Biot=h_bar*th/k;                    % Biot number
    T=ones(M,N)*T_mh; % initialize temperature matrix
    %Setup grid
    for i=1:M
             x(i,1)=(i-1)*W/(M-1);
    end
    DELTAx=W/(M-1);
    for j=1:N
             y(j,1)=(j-1)*W/(N-1);
    end
    DELTAy=W/(N-1);
        % boundary conditions
for j=1:M
     T(1,j)=T_mh;
     T(M,j)=T_mh;
end
for i=2:(M-1)
     T(i,1)=T_mh;
     T(i,N)=T_mh;
end
tol=0.00001;   % stopping criteria – max. change in temperature (K)
err=999;       % initial value of the error - set > tol
while(err>tol)
    err_max=0;           % initialize maximum value of the error
% internal node energy balances
for i=2:(M-1)
     for j=2:(N-1)
          T_old=T(i,j); % old value of temperature
          T(i,j)=(2*h_bar*DELTAx*DELTAy*T_infinity+q_dot_flux*...
                   pd_f(x(i),y(j),W)*DELTAx*DELTAy+k*DELTAy*th*...
                   (T(i-1,j)+T(i+1,j))/DELTAx+k*DELTAx*th*...
                   (T(i,j-1)+T(i,j+1))/DELTAy)/(2*k*DELTAy*...
                   th/DELTAx+2*k*DELTAx*th/DELTAy+2*h_bar*DELTAx*DELTAy);
          err=abs(T_old-T(i,j)); % compute error between old and new temps.
          if (err>err_max)
                   err_max=err;
          end
     end
end

end   
    T_C=T-273.15; % temperature in C
end



    
function[pd]=pd_f(x,y,W)
% [pd]=pd_f(x,y,W)
%
% This sub-function returns the pattern density of the EPL mask
%
% Inputs:
% x - x-position (m)
% y - y-position (m)
% W - dimension of mask (m)
% Output:
% pd - pattern density (-)
    pd=0.1+0.5*x*y/W^2;
end
