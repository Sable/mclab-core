% File: ode.m
% Euler method calculates y'=x*squt(y), y(2)=4, in [2, 3]
% step size h=0.1
% Euler solutions in y, analytical solutions in ya,
% Error = ya - y, relative rer = error/ya
% maximum relative error in maxrer
% File name: ode.m

close all
clear all

do_predictor_corrector = 1 ; 
do_euler = 1 ; 
do_runge_kutta = 1 ; 

upper_range = 3.0 ; 
lower_range = 2.0 ; 

h= 0.1; 
n= (upper_range - lower_range)/h;
x= lower_range:h:upper_range;

y=zeros(size(x));y(1)=4;

ya=(1+0.25*x.^2).^2; % Analytical solution y=(1+0.25*x^2)^2

for i=1:n
      ya(i)=power((1+0.25*x(i)*x(i)),2) ;   
     fi=x(i)*sqrt(y(i));
   
   y(i+1)=y(i)+h*fi; % Euler approximation
   
end;

   error =ya - y ; 
   rer = error./ya; % relative error

   error(n+1) =ya(n+1) - y(n+1);   
   rer(n+1) = error(n+1)/ya(n+1); % relative error


maxrer=max(rer); % maximum relative error

if(do_predictor_corrector == 1) 
pc_y=zeros(size(x));pc_y(1)=4;

for i=1:n
fi=x(i)*sqrt(pc_y(i));
yiplus1star= pc_y(i)+h*fi; % provisional estimate
fiplus1=x(i+1)*sqrt(yiplus1star);
pc_y(i+1)=pc_y(i)+0.5*h*(fi+fiplus1); % better estimate
end;

pc_error= ya - pc_y; % Error between Analytical solution and Euler solution
pc_rer= pc_error./ya; % relative error
pc_maxrer=max(pc_rer); % maximum relative error

end


if(do_runge_kutta == 1) 

% Runge-Kutta method calculates y'=x*squt(y), y(2)=4, in [2, 3]
% step size h=0.1
% Runge-Kutta solutions in y, analytical solutions in ya,
% Error = ya - y, relative rer = error/ya
% maximum relative error in maxrer
% File name: euler1.m
h=0.1; n=(3-2)/h; x=2:h:3;
rk_y=zeros(size(x)); rk_y(1)=4;

for i=1:n
A1=x(i)*sqrt(rk_y(i)); % provisional estimate
A2=(x(i)+h/2)*sqrt(rk_y(i)+0.5*h*A1); % provisional estimate
A3=(x(i)+h/2)*sqrt(rk_y(i)+0.5*h*A2); % provisional estimate
A4=(x(i)+h)*sqrt(rk_y(i)+h*A3); % provisional estimate
rk_y(i+1)=rk_y(i)+h*(A1+2*A2+2*A3+A4)/6; % better estimate
end;
ya=(1+0.25*x.^2).^2; % Analytical solution y=(1+0.25*x^2)^2
rk_error=ya - rk_y; % Error between Analytical solution and Euler solution
rk_rer=rk_error./ya; % relative error
rk_maxrer=max(rk_rer); % maximum relative error
end


if( (do_euler  == 1)  & (do_predictor_corrector == 0 ) ) 

figure
plot( x, ya,'k') ; 
hold on
plot(x,y,'r*') ;
xlabel('x') 
ylabel('function value') 

figure
plot(x(2:n+1),log10(abs(rer(2:n+1))),'r')
title('Relative error of Euler method') 
xlabel('x') 
ylabel('log10 rel error') ;

end


if( (do_predictor_corrector == 1) & ( do_runge_kutta == 0 ) ) 
   
   figure
plot( x, ya,'k') ; 
hold on
plot(x,y,'r*') ;
plot(x,pc_y,'c*') ;
xlabel('x') 
ylabel('function value') 
legend('Exact','Euler','PC Euler') ;


figure
plot(x(2:n+1),log10(abs(rer(2:n+1))),'r')
hold on
plot(x(2:n+1),log10(abs(pc_rer(2:n+1))),'c')
title('Relative error of Euler method') 
xlabel('x') 
ylabel('log10 rel error') ;
legend('Euler','PC_Euler') ;


end

if( (do_predictor_corrector == 1) & ( do_runge_kutta == 1 ) )    
   
   figure
plot( x, ya,'k') ; 
hold on
plot(x,y,'r*') ;
plot(x,pc_y,'c*') ;
plot(x,rk_y,'*') ;
xlabel('x') 
ylabel('function value') 
legend('Exact','Euler','PC Euler','Runge Kutta') ;


figure
plot(x(2:n+1),log10(abs(rer(2:n+1))),'r')
hold on
plot(x(2:n+1),log10(abs(pc_rer(2:n+1))),'c')
plot(x(2:n+1),log10(abs(rk_rer(2:n+1))))

title('Relative error of Euler method') 
xlabel('x') 
ylabel('log10 rel error') ;
legend('Euler','PC Euler', 'Runge Kutta') ;


end







