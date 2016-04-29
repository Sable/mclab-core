function [x] = bubble(A)
        x = A;
        n=length(x);
        for j=1:n-1
                % comparing each number with the next and swapping
                for i=1:n-1
                        if x(i)>x(i+1)
                                temp=x(i);
                                x(i)=x(i+1);
                                x(i+1)=temp;
                        end
                end
        end
end
