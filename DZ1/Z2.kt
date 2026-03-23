import kotlin.io.println
import kotlin.math.abs

class VendingMachine(var beverageCode:Int, var money:Double){

    var beveragePrice=0.0
        private set
    var beverageName=""
        private set
init {


    if(money<0) {
        money = 0.0
        println("Negative numbers not accepted credits set to 0") }
    else println("Balance is: " + money)

    when (beverageCode) {
        1 -> {
            beveragePrice = 1.01
            beverageName= "Water" }
        2 -> {
            beveragePrice = 1.50
            beverageName= "Cola" }
        3 -> {
            beveragePrice = 1.33
            beverageName= "Juice" }
        4 -> {
            beveragePrice = 1.60
            beverageName= "Coffe" }

    }

    if((beverageCode > 4)&&(beverageCode < 0)) {
        beverageCode = 1
        println("Water Selected")

    }
    else println(beverageName+" Selected")

}

    fun AddMoney(monex:Double){
        money+=monex
        println("Your balance is: "+money)
    }

    fun BuyDrink(){

        if((money-beveragePrice)>=0) {
            println("Your change is: " + (money-beveragePrice))
            money = 0.0
            println("Drink purchased: " + beverageName)}
        else println("To buy the selected drink you are missing: " + abs((money-beveragePrice)) + " credits")
    }




}

fun main() {
val theMachine=VendingMachine(1,-9.0)
theMachine.BuyDrink()
theMachine.AddMoney(1.01)
theMachine.BuyDrink()


}

