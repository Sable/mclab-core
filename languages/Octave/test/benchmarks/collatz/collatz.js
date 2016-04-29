function collatz(n) {
    var y = 0;
    while (n != 1) {
        if (n % 2 === 0) {
            n = n / 2;
        } else {
            n = 3*n + 1;
        }
        y = y + 1;
    }
    return y;
}

function main(size) {
    var t1 = Date.now();
    var max_length = 0;
    var max_num = 0;
    for (var i = 1; i <= size; ++i) {
        var length = collatz(i);
        if (length > max_length) {
            max_length = length;
            max_num = i;
        }
    }
    var t2 = Date.now();
    console.log('OUT');
    console.log(max_num);
    console.log(max_length);
    console.log('TIME');
    console.log((t2 - t1) / 1000.0);
}
