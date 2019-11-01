package com.jlua.internal;

// Despite the fact that I'm going for .NET's Func implementation I'll limit the number of parameters to 5 because
// this technique is messy enough as it is

public interface Func<T1, T2, T3, T4, T5, TResult> {
    TResult Invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
}

