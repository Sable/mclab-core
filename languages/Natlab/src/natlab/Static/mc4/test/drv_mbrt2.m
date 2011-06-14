% =========================================================================== %
%                                                                             %
% Copyright 2011 Anton Dubrau and McGill University.                          %
%                                                                             %
%   Licensed under the Apache License, Version 2.0 (the "License");           %
%   you may not use this file except in compliance with the License.          %
%   You may obtain a copy of the License at                                   %
%                                                                             %
%       http://www.apache.org/licenses/LICENSE-2.0                            %
%                                                                             %
%   Unless required by applicable law or agreed to in writing, software       %
%   distributed under the License is distributed on an "AS IS" BASIS,         %
%   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  %
%   See the License for the specific language governing permissions and       %
%  limitations under the License.                                             %
%                                                                             %
% =========================================================================== %

function  [time, output, valid] = drv_mbrt2(scale)
  mc_t2 = sqrt(scale);
  mc_t16 = 6000;
  [mc_t1] = mtimes(mc_t16, mc_t2);
  N = round(mc_t1);
  mc_t17 = 10;
  mc_t18 = 3;
  [mc_t4] = mpower(mc_t17, mc_t18);
  mc_t5 = sqrt(scale);
  [mc_t3] = mtimes(mc_t4, mc_t5);
  Nmax = round(mc_t3);
  t1 = clock();
  mand_N = N;
  mand_Nmax = Nmax;
  mc_t31 = sqrt(mand_N);
  side = round(mc_t31);
  mc_t43 = 1;
  [ya] = uminus(mc_t43);
  yb = 1;
  mc_t44 = 1.5;
  [xa] = uminus(mc_t44);
  xb = .5;
  [mc_t32] = minus(xb, xa);
  mc_t45 = 1;
  [mc_t33] = minus(side, mc_t45);
  [dx] = mrdivide(mc_t32, mc_t33);
  [mc_t34] = minus(yb, ya);
  mc_t46 = 1;
  [mc_t35] = minus(side, mc_t46);
  [dy] = mrdivide(mc_t34, mc_t35);
  mand_set = zeros(side, side);
  mc_t47 = 1;
  [s] = minus(side, mc_t47);
  for x = (0 : s)
    for y = (0 : s)
      [mc_t42] = mtimes(x, dx);
      [mc_t37] = plus(xa, mc_t42);
      mc_t39 = i();
      [mc_t41] = mtimes(y, dy);
      [mc_t40] = plus(ya, mc_t41);
      [mc_t38] = mtimes(mc_t39, mc_t40);
      [mc_t36] = plus(mc_t37, mc_t38);
      iter_x = mc_t36;
      max = mand_Nmax;
      c = iter_x()
      iter_i = 0;
      mc_t52 = abs(iter_x);
      mc_t57 = 2;
      [mc_t50] = lt(mc_t52, mc_t57);
      [mc_t51] = lt(iter_i, max);
      [cond] = and(mc_t50, mc_t51);
      mc_t56 = abs(iter_x);
      mc_t58 = 2;
      [mc_t54] = lt(mc_t56, mc_t58);
      [mc_t55] = lt(iter_i, max);
      [cond] = and(mc_t54, mc_t55);
      while cond
        [mc_t53] = mtimes(iter_x, iter_x);
        [iter_x] = plus(mc_t53, c);
        mc_t59 = 1;
        [iter_i] = plus(iter_i, mc_t59);
        mc_t56 = abs(iter_x);
        mc_t60 = 2;
        [mc_t54] = lt(mc_t56, mc_t60);
        [mc_t55] = lt(iter_i, max);
        [cond] = and(mc_t54, mc_t55);
      end
      out = iter_i();
      mc_t28 = out;
      mc_t48 = 1;
      [mc_t29] = plus(y, mc_t48);
      mc_t49 = 1;
      [mc_t30] = plus(x, mc_t49);
      mand_set(mc_t29, mc_t30) = mc_t28;
    end
  end
  mand_set;
  set = mand_set;
  t2 = clock();
  [mc_t6] = minus(t2, t1);
  mc_t20 = 0;
  mc_t21 = 0;
  mc_t22 = 86400;
  mc_t23 = 3600;
  mc_t24 = 60;
  mc_t25 = 1;
  mc_t8 = horzcat(mc_t20, mc_t21, mc_t22, mc_t23, mc_t24, mc_t25);
  [mc_t7] = transpose(mc_t8);
  [time] = mtimes(mc_t6, mc_t7);
  mc_t9 = set(:);
  mc_t10 = mean(mc_t9);
% Store the benchmark output
  output = {mean(mc_t10)};
  [t] = mtimes(Nmax, N);
  mc_t15 = sum(set);
  mc_t14 = sum(mc_t15);
  [mc_t13] = mrdivide(mc_t14, t);
  mc_t26 = 0.37429481997515;
  [mc_t12] = minus(mc_t13, mc_t26);
  mc_t11 = abs(mc_t12);
  mc_t27 = 0.01;
  [mc_t0] = lt(mc_t11, mc_t27);
  if mc_t0
    valid = 'PASS';
  else 
    valid = 'FAIL';
  end
end

