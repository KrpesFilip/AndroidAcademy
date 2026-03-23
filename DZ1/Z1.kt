
class User(val name:String, val surname:String, var age:Int?, var email:String?){

    init {
            age?.let{
                if(it <= 0) age = 0;
            }
    }

    constructor(name: String, surname: String):this(name, surname, 0, null){}

    constructor(name: String, surname: String, age: Int):this(name, surname, age, null){}

    constructor(name: String, surname: String, email:String):this(name, surname, 0, email){}

    fun SetEmail(email:String){
        this.email = email;
    }

    fun WriteEmailLenght(){
        if(this.email!=null){
            println("Email lenght:" + this.email?.length);
        }
        else println("Email invalid");
    }

}

fun main() {

val user = User("John", "Doe");
user.WriteEmailLenght();
user.SetEmail("john.does@mail.com")
user.WriteEmailLenght();
}