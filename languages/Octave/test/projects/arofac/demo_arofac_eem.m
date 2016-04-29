%
%  AROFAC demo on realworld example data of fluorescence spectroscopy taken from:  
%  
%  http://www.mathworks.com/matlabcentral/fileexchange/1088-the-n-way-toolbox/
%
%   EEM data from file  "dorrit.mat"
%   "A Perkin-Elmer LS50 B fluorescence spectrometer was used to measure fluorescence landscapes using excitation 
%    wavelengths between 200-350 nm with 5 nm intervals. The emission wavelength range was 200-750 nm. 
%    Excitation and emission monochromator slit widths were set to 5 nm, respectively. Scan speed was 1500 nm/min. 
%    The data have been published in [J. Riu and R. Bro. Jack-knife for estimation of standard errors and 
%                                     outlier detection in PARAFAC models. Chemom.Intell.Lab.Syst., 2002.] 
%    but were originally measured by Dorrit Baunsgaard."
%    see http://www.models.life.ku.dk/dorrit  for details.
%
%
%   excitation wavelengths between 230 -315 nm
%   emission   wavelengths between 251- 481 nm
% 
% For further details consult the related publication:
%
% Kiraly FJ, Ziehe A. Approximate Rank-Detecting Factorization of Low-Rank Tensors. ICASSP 2013.
% http://arxiv.org/abs/1211.7369
%

load dorrit_eem_data

[UU,VV,estrank] = arofac_cluster(EEM);

idx=find(mean(cumsum(VV))<0); 
if ~isempty(idx),
VV(:,idx)=-VV(:,idx);
end

idx=find(mean(cumsum(UU))<0); 
if ~isempty(idx),
UU(:,idx)=-UU(:,idx);
end

disp([num2str(estrank) ' components found.'])
figure
subplot(121)                                         
plot(f1,UU,'linewidth',2)
xlabel('wavelength (nm)')
ylabel('intensity')
title('Excitation loadings')
grid on
axis('tight')
subplot(122)
plot(f2,VV,'linewidth',2)   
xlabel('wavelength (nm)')
ylabel('intensity')
 title('Emission loadings')
grid on
axis('tight')

%disp(description)
