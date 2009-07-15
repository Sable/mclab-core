for i = 1:1:10
   for j=i:1:i+4
     for k=j:1:i+2	
	 a(i,j,k)=a(j+10,i+11,k);
         a(i,j,k)=b(i); 		
     end	
   end 	
end

