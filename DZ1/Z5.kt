
object TransactionLogger {
    fun Log(message:String){println(message)}
}

class BankAccount(var accountNumber:Int ){
   var bankBalance:Double=0.0
       private set

    init {
        objectCount++
    }

    fun Deposit(amount:Double){
        if(amount>0)
        bankBalance+=amount
        TransactionLogger.Log("Deposited: $amount to account $accountNumber")
    }
    fun Withdraw(amount:Double){
        if(amount<bankBalance) {
            bankBalance -= amount
            TransactionLogger.Log("Withdrew: $amount from account $accountNumber")}
        else TransactionLogger.Log("Not enough funds to withdraw $amount from account $accountNumber")
    }
    fun CheckBalance(){
        println("Bank balance is: $bankBalance on account $accountNumber")
    }

    companion object {
        var objectCount:Int=0
            private set
    }


}

fun main(){
val ac1=BankAccount(accountNumber=1)
    ac1.Deposit(6.0)
    ac1.CheckBalance()
    ac1.Withdraw(5.0)
    ac1.CheckBalance()
    ac1.Withdraw(5.0)

val ac2=BankAccount(accountNumber=2)
    ac2.Deposit(6.34)
    ac2.Withdraw(5.0)
    ac2.CheckBalance()
val ac3=BankAccount(accountNumber=3)
    ac3.Withdraw(2.0)
    ac3.bankBalance

println("Number of accounts: " + BankAccount.objectCount.toString())

}