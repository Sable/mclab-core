% AROFAC demo on toydata 
% (n1=50, n2=60, n3=70, noise_level=0.1, rank=10);
% 
% For details consult the related publication:
%
% Kiraly FJ, Ziehe A. Approximate Rank-Detecting Factorization of Low-Rank Tensors. ICASSP 2013.
% http://arxiv.org/abs/1211.7369

disp('generating toydata')

[M, U, V] = toydata_simdiag(50,60,70,0.1,10);

disp('running AROFAC')

[UU,VV,estrank]=arofac_cluster(M,50);
disp([' '  num2str(estrank) ' components found.'])

figure(1)

subplot(121)
imagesc((abs(pinv(UU)*U)))

subplot(122)
imagesc((abs(pinv(VV)*V)))

colormap (1-gray)


