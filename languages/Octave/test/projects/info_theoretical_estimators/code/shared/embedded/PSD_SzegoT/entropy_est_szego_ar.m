function [H_g] = entropy_est_szego_ar(x,p_max,K)
% ENTROPY_EST_SZEGO: Estimates the entropy based on the relation between the DEP and the FDP using the Szeg�'s Theorem, assuming an
%                    AR model (Long AR + Regularization) for the DEP
%
%           [H_g] = entropy_est_szego(x,p,L)
%           x = samples of the random variable
%           p_max = maximum order of the AR process. The actual order (p) is selected according to the corrected AIC criterion
%           L = size of the toeplitz matrix (L>=p). If L>p, we extrapolate the correlation using its AR structure
%
%           H_g = Shannon entropy estimate (in bits)

% David Ramirez Garcia
% Advanced Signal Processing Group (G.T.A.S.)
% University of Cantabria (Spain)
% 2008

x = x(:).';

%a = 1/(8*std(x));
a = 1/(2*max(abs(x)));
% a = 1;

x = x*a;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%        Estimation of the AR parameters          %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

all_a_AR = zeros(p_max-1,p_max);
all_e = zeros(1,p_max-1);

phi_x = mean(exp(1i*2*pi*(0:p_max)'*x),2);

for order = 1:p_max       

    % LONG AR
    R = toeplitz(phi_x(1:order),phi_x(1:order)');
    r = phi_x(2:order+1);
    Delta = diag((1:order).^(2*2));
    a_AR = [1 ; -(R + (1e-5)*Delta)\r].';    
    all_a_AR(order,1:order+1) = a_AR;
    all_e(order) = real(a_AR*conj(phi_x(1:order+1)));        
end

MDL = length(x)*log(all_e) + (1:length(all_e))*log(length(x));

%[~,p] = min(MDL);
[min_value,p] = min(MDL);

a_AR = all_a_AR(p,1:p+1);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%     Extrapolation of the autocorrelation        %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

L = K*(p+1);

phi_x_tilde = zeros(1,L);
phi_x_tilde(1:p+1) = phi_x(1:p+1);

for kk = p+2:L
    phi_x_tilde(kk) = -a_AR(2:end)*phi_x_tilde(kk-1:-1:kk-p).';
end

R = toeplitz(phi_x_tilde,phi_x_tilde');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Estimation of the Entropy using Szeg�'s Theorem %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

lambdas = eig(R);

H_g = -mean(real(log2(lambdas).*lambdas));

H_g = H_g - log2(a);