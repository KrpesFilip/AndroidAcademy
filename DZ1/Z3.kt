

fun SumSteps(weeklySteps: IntArray):Int{
    var sum=0
for (dailyStep in weeklySteps){
    sum+=dailyStep
}
    return sum
}

fun First10KDay(weeklySteps: IntArray){
var i=0
    while (i<weeklySteps.size){
        if (weeklySteps[i]>=10000) break
        i++
    }
    println("The first 10k day was day " + (i+1).toString() )
}

fun main(){

   val weeklySteps = intArrayOf(4500,12000,8000,15000,3000,11000,9500)
    println("This week i walked " + SumSteps(weeklySteps) + " steps")
    First10KDay(weeklySteps)
}