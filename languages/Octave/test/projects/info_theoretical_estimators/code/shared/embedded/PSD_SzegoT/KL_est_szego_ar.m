function [KL_g] = KL_est_szego_ar(x,y,p_max,K)
% KL_EST_SZEGO: Estimates the KL based on the relation between the DEP and the FDP using the Szeg�'s Theorem, assuming an
%               AR process for the DEP
%
%           [KL_g] = KL_est_szego(x,y,p,L)
%           x = samples of the random variable 1
%           y = samples of the random variable 2
%           p = order of the AR process
%           L = size of the toeplitz matrix (L>=p). If L>p, we extrapolate the correlation using its AR structure
%
%           KL_g = Kullback-Leibler divergence estimation

% David Ramirez Garcia
% Advanced Signal Processing Group (G.T.A.S.)
% University of Cantabria (Spain)
% 2008

x = x(:).';
y = y(:).';

%a1 = 1/(8*std(x));
a1 = 1/(2*max(abs(x)));
% a1 = 1;
%a2 = 1/(8*std(y));
a2 = 1/(2*max(abs(y)));
% a2 = 1;

a = min(a1,a2);

x = x*a;
y = y*a;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%        Estimation of the AR parameters for x    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

all_a_AR_x = zeros(p_max-1,p_max);
all_e_x = zeros(1,p_max-1);

phi_x = mean(exp(1i*2*pi*(0:p_max)'*x),2);

for order = 1:p_max    
    R = toeplitz(phi_x(1:order),phi_x(1:order)');
    r = phi_x(2:order+1);
    Delta = diag((1:order).^(2*2));
    a_AR = [1 ; -(R + (1e-5)*Delta)\r].';    
    all_a_AR_x(order,1:order+1) = a_AR;
    all_e_x(order) = real(a_AR*conj(phi_x(1:order+1)));        
end

MDL_x = length(x)*log(all_e_x) + (1:length(all_e_x))*log(length(x));

%[~,p_x] = min(MDL_x);
[min_value,p_x] = min(MDL_x);

a_AR_x = all_a_AR_x(p_x,1:p_x+1);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%        Estimation of the AR parameters for y    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

all_a_AR_y = zeros(p_max-1,p_max);
all_e_y = zeros(1,p_max-1);

phi_y = mean(exp(1i*2*pi*(0:p_max)'*y),2);

for order = 1:p_max    
    R = toeplitz(phi_y(1:order),phi_y(1:order)');
    r = phi_y(2:order+1);
    Delta = diag((1:order).^(2*2));
    a_AR = [1 ; -(R + (1e-5)*Delta)\r].';    
    all_a_AR_y(order,1:order+1) = a_AR;
    all_e_y(order) = real(a_AR*conj(phi_y(1:order+1)));        
end

MDL_y = length(y)*log(all_e_y) + (1:length(all_e_y))*log(length(y));

%[~,p_y] = min(MDL_y);
[min_value,p_y] = min(MDL_y);

a_AR_y = all_a_AR_y(p_y,1:p_y+1);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%     Extrapolation of the autocorrelation        %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

L = K*(max([p_x p_y])+1);

phi_x_tilde = zeros(1,L);
phi_x_tilde(1:p_x+1) = phi_x(1:p_x+1);

for kk = p_x+2:L
    phi_x_tilde(kk) = -a_AR_x(2:end)*phi_x_tilde(kk-1:-1:kk-p_x).';
end

phi_y_tilde = zeros(1,L);
phi_y_tilde(1:p_y+1) = phi_y(1:p_y+1);

for kk = p_y+2:L
    phi_y_tilde(kk) = -a_AR_y(2:end)*phi_y_tilde(kk-1:-1:kk-p_y).';
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Estimation of the KL diver using Szeg�'s Theorem %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Rx = toeplitz(phi_x_tilde,phi_x_tilde');
Ry = toeplitz(phi_y_tilde,phi_y_tilde');

[V1,lambdas1] = eig(Rx);
[V2,lambdas2] = eig(Ry);

lambdas1 = diag(lambdas1).*(diag(lambdas1)>=0)+eps;
lambdas2 = diag(lambdas2).*(diag(lambdas2)>=0)+eps;

H = mean(real(log(lambdas1).*lambdas1));

log_Ry = V2*diag(log(lambdas2))*V2';

H_cross = mean(diag(Rx*log_Ry));

KL_g = real(H - H_cross);
