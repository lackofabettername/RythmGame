package misc

//https://stackoverflow.com/a/55459156
data class NTuple4<T1, T2, T3, T4>(val t1: T1, val t2: T2, val t3: T3, val t4: T4)

data class NTuple5<T1, T2, T3, T4, T5>(val t1: T1, val t2: T2, val t3: T3, val t4: T4, val t5: T5)

data class NTuple6<T1, T2, T3, T4, T5, T6>(val t1: T1, val t2: T2, val t3: T3, val t4: T4, val t5: T5, val t6: T6)

infix fun <T1, T2, T3> Pair<T1, T2>.then(t3: T3): Triple<T1, T2, T3>
{
    return Triple(this.first, this.second, t3)
}

infix fun <T1, T2, T3, T4> Triple<T1, T2, T3>.then(t4: T4): NTuple4<T1, T2, T3, T4>
{
    return NTuple4(this.first, this.second, this.third, t4)
}

infix fun <T1, T2, T3, T4, T5> NTuple4<T1, T2, T3, T4>.then(t5: T5): NTuple5<T1, T2, T3, T4, T5>
{
    return NTuple5(this.t1, this.t2, this.t3, this.t4, t5)
}

infix fun <T1, T2, T3, T4, T5, T6> NTuple5<T1, T2, T3, T4, T5>.then(t6: T6): NTuple6<T1, T2, T3, T4, T5, T6>
{
    return NTuple6(this.t1, this.t2, this.t3, this.t4, this.t5, t6)
}