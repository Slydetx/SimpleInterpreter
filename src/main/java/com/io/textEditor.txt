fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }
b = fact_iter(5)