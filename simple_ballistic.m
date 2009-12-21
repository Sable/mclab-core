% Code to solve for projectile travelling in y-direction. 
close all
clear all
g = 9.81 ; % gravitational constant. 

y0  = 0.0 ; %initial position
vy0 = 100 ; %initial speed in y direction 
x0 = 0.0 ; 

delta_t = 1 ;  % time step 
N = 20; % number of steps 

exact_y = zeros(N,1) ; 
exact_vy = zeros(N,1) ; 
euler_y = zeros(N,1) ; 
euler_vy = zeros(N,1) ; 
x = zeros(N,1) ; 

time = zeros(N,1) ; 

exact_y(1,1) = y0 ; 
euler_y(1,1) = y0 ; 
exact_vy(1,1) = vy0 ; 
euler_vy(1,1) = vy0 ; 
x(1,1) = x0 ; 


time(1,1) = 0.0 ; 

figure
 plot(x(1,1),euler_y(1,1),'*') 
axis([ -1 1 0 600]) 
M(1,1) = getframe ;


for(ct  = 1:N) 
    time(ct+1,1) = (ct)*delta_t ; 
    x(ct+1,1) = x0  ;
    
    
    f_value = euler_vy(ct,1) ; 
    euler_y(ct +1,1) = euler_y(ct,1) + delta_t*f_value ;  % Euler applied to y 
    exact_y(ct +1,1) = y0 + vy0*time(ct+1,1) - 0.5*g*power(time(ct+1,1),2) ; 

    plot(x(ct+1,1),euler_y(ct+1,1),'*') 
    axis([ -1 1 0 600]) 
    M(ct+1,1) = getframe ;
    
  
    
    f_value = -g ; 
    euler_vy(ct+1,1) = euler_vy(ct,1) + delta_t*f_value ; % Euler applied to vy 
    exact_vy(ct +1,1) =  vy0 - g*time(ct+1,1) ; 
    
  
end

movie(M,1) 

figure
plot(time,euler_y) 
hold on
plot(time,exact_y,'r-.') 
title('displacement versus time') 
xlabel('time')
ylabel('displacment') ; 
legend('euler','exact') ; 


figure
plot(time,euler_vy) 
hold on
plot(time,exact_vy,'r-.') 
title('velocity versus time') 
xlabel('time')
ylabel('velocity') ; 
legend('euler','exact') ; 