MODULE vercat
IMPLICIT NONE


contains
!This is the subroutine that concatanate 2 matrices of interger 
SUBROUTINE vertcati2i2(A1,A2,C)
INTEGER,DIMENSION(:,:)::A1
INTEGER,DIMENSION(:,:)::A2
INTEGER,DIMENSION(SIZE(A1(:,1))+SIZE(A2(:,1)),SIZE(A1(1,:)))::C
INTEGER I

DO I=1,SIZE(A1(:,1))
   C(I,:)=A1(I,:)
END DO


DO I=SIZE(A1(:,1))+1,SIZE(A1(:,1)+SIZE(A2(:,1))
   C(I+SIZE(A1(:,1)),:)=B(I,:)
END DO

END SUBROUTINE vertcati2i2


END MODULE vercat
