for ii = 2:n
    for jj = 2:m
 	f(ii, jj) = f(ii, jj)+mask(ii, jj)*(0.25*(f(ii-1, jj)+f(ii+1, jj)+f(ii, jj-1)+f(ii, jj+1))-f(ii, jj));
    end;
end;
