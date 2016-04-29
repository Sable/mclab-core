function matmul(A, B, m, k, n) {
    C = new Float64Array(m*n);
    for (var j = 0; j < n; ++j) {
        for (var h = 0; h < k; ++h) {
            for (var i = 0; i < m; ++i) {
                C[i*n + j] = C[i*n + j] + A[i*k + h]*B[h*n + j];
            }
        }
    }
    return C;
}

function main(size) {
    var m = size;
    var k = size / 2;
    var n = size;

    var A = new Float64Array(m*k);
    var B = new Float64Array(k*n);

    for (var i = 0; i < m*k; ++i)
        A[i] = Math.random();

    for (var i = 0; i < m*k; ++i)
        B[i] = Math.random();

    var t1 = Date.now();
    var C = matmul(A, B, m, k, n);
    var t2 = Date.now();

    console.log('OUT');
    console.log('1');
    console.log('TIME');
    console.log((t2 - t1) / 1000.0);
}
