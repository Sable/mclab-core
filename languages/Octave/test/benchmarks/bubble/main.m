function main(size)
  A = rand(1, size);
  tic();
  y = bubble(A);
  t = toc();

  disp('OUT');
  for i = 1:size
      disp(y(i));
  end
  disp('TIME');
  disp(t);
end
