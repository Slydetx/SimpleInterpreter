fun fact_rec(n) { if n <= 0 then return 1 else return n*fact_rec(n-1) }
a = fact_rec(5)