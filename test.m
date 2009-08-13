for i = 2:2:10      
          t = data(j-1);
          data(j-1) = data(i-1);
          data(i-1) = t;          
          t = data(j);
          data(j) = data(i);
          data(i) = t;
 end
