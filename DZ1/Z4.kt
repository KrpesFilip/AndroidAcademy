import java.util.Locale
import java.util.Locale.getDefault

fun CheckName(name:String){
var validity = false
var editedName= name.lowercase()
editedName= editedName.trim()

if(name.isBlank()) {
    println("Name is blank")
    return}
if(editedName.length !in 5..15) {
    println(name + " is too short")
    return}

if(editedName.all{ it.isLetter() || it.isDigit() || it=='_' } && editedName.all { it!=' ' }  )
    validity=true
    else println(name + "contains illegal characters")
if(validity)
    println(name + "is valid")

}





fun main(){

    CheckName(" John_Does67 ")
    CheckName("  ")
    CheckName(" John_Does*67 ")
    CheckName(" d")


}