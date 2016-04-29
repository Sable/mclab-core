function [wo,epsreturn] = calculateweight(T,d,sk,k,dp)



%alternate form - tries to minimize epsilon
%%%%%%compute the optimal weight%%%%%%%%%%%%%%

%%%Input
%T = sample size
%d = dimension
%dp = number of bias terms we seek to minimize (min = 1, max=d)
%sk-k = range of k values

%%%Output
%wo = optimal weight
%epsreturn = corresponding value of epsilon

kvec=sk:k;
N=floor(T/2);
M=T-N;

%optimal weight - kks
%coeff = [0 1 2 3 4 5 6 7 8 9 10];
%x = [1 0 0 0 0 0 0 0 0 0 0]';

coeff = (1:(floor(dp)+2))-1;
%coeff = (1:(d/2+1))-1;
x = zeros(size(coeff));x(1) = 1;

kmat(1,:) = ones(size(kvec));
sc = 1*(1/T)^(1/2);
Aeq(1,:) = kmat(1,:);
Aeq(1,end+1) = 0;
Beq = 1;
count = 0;

%query for the current working environment:
  environment_Matlab = working_environment_Matlab;
    if environment_Matlab%Matlab
        %options=optimset('Display','off');
        options=optimset('Display','off','Algorithm','interior-point');%'interior-point','sqp','active-set','trust-region-reflective' (default)
    end
    
for i=2:length(x)
    %kmat(i,:) = ((gamma(kvec+1-al+coeff(i)/d)./gamma(kvec))) * (gamma(T)/gamma(T+coeff(2)/d));
    kmat(i,:) = (kvec).^(coeff(i)/d) * (M^(-coeff(i)/d));
    count=count+1;
    A(count,1:length(kvec)) = kmat(i,:);
    A(count,length(kvec)+1) = -1;
    B(count) = 0;
    count=count+1;
    A(count,1:length(kvec)) = -kmat(i,:);
    A(count,length(kvec)+1) = -1;
    B(count) = 0;
end

kap=10;%10*sqrt(length(kvec));%*T^(0.4);
winit = ones(size(kvec))/length(kvec);
wleft = winit;wleft(end+1) = -100;0;
wright = winit;wright(end+1) = 100;%((k/M)^(2/d))/kap;
winit(end+1) = sc;
if environment_Matlab%Matlab
    wo = fmincon(@(ws) ws(end), winit, A, B', Aeq, Beq,-kap*abs(wleft),kap*abs(wright),[],options);
else%Octave
    wo = sqp(winit.',@(w) w(end),@(w)(Aeq*w-Beq),@(w)(B'-A*w),-kap*abs(wleft.'),kap*abs(wright.'));%x_0,phi(x),g [g(x)=0],h[h(x)>=0].lb,ub[lb<=x<=ub]
    wo = wo.';
end
epsreturn = wo(end);
wo(end)=[];

