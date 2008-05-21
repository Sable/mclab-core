# this is single line comment
% this is another % single line comment
% java -classpath .;../../../lib/beaver-rt.jar Main Test.m >log.txt
		a = 5;
		matrixA = [1, 2; 3, a=5; a, a+6];
		matrixA = [1, 2, 3, a=5];

		matrixC = 1:9;
		matrixD = 1:a+7;
		matrixC(1:5) = a+6;		
		
		matrixX = [];
		matrixY = [;];
		matrixZ = [1,];
		matrixA = [1, 2; 3, a=5; [a, a+6]];
		Mb = matrixA;
		Mc = matrixA(1);
		Mc = matrixA(1,:);
		Mc = matrixA();		% RHS, OK, matrix_access ==  method_invocation

		matrixA(1, 1) = a;
		matrixA(:) = [a, 9];
		matrixA(1, :) = a;

%		 matrixA() = a;		% LHS, report error
		