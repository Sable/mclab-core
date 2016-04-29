function bubble(A) {
    var x = new Float64Array(A);
    var n = A.length;
    for (var j = 0; j < n-2; ++j) {
        for (var i = 0; i < n-2; ++i) {
            if (x[i] > x[i+1]) {
                var t = x[i];
                x[i] = x[i+1];
                x[i+1] = t;
            }
        }
    }
    return x;
}

function random_array(size) {
    var a = new Float64Array(size);
    for (var i = 0; i < size; ++i) {
        a[i] = Math.random();
    }
    return a;
}

function main(size) {
    var A = random_array(size);
    var t1 = Date.now();
    var y = bubble(A);
    var t = Date.now() - t1;

    console.log("OUT");
    for (var i = 0; i < size; ++i) {
        console.log(y[i]);
    }
    console.log("TIME");
    console.log(t / 1000);
}
