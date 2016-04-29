function [B] = merge_sort(A, n)
    if n <= 1
        B = A;
    else
        [A_left, A_right] = split(A, n);
        A_left_len = length(A_left);
        A_right_len = length(A_right);
        A_left_sorted = merge_sort(A_left, A_left_len);
        A_right_sorted = merge_sort(A_right, A_right_len);
        B = merge(A_left_sorted, A_right_sorted, A_left_len, A_right_len);
    end
end
