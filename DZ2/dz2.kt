import kotlin.properties.Delegates
import kotlin.random.Random

data class Magazine(val maxCapacity: Int, val caliber:String){
    var currentCapacity = 0

    fun load(ammunition:Int){
        var i=ammunition
        while ((currentCapacity<maxCapacity)&&(i!=0))
        {
            i--
            currentCapacity++

        }
    }

    fun removeBullet(){
        if(currentCapacity>0)
            currentCapacity--
    }
}

abstract class Gun(){
    abstract val caliber:String
    var magazine:Magazine?=null
    abstract val damage: Int
    abstract val statusEffectChance: List<Float>

    fun fire(){
        magazine?.let{
            if(it.currentCapacity>0){
                println("Fires a bullet in the air")
                it.currentCapacity--
            }
        }

    }

    fun fireAtTarget(enemy:Enemy){
        magazine?.let {
            if (it.currentCapacity > 0) {
                it.currentCapacity--

                if (enemy.health <= damage) {
                    println("Enemy killed")
                    enemy.health = 0
                } else {
                    enemy.health -= damage
                    println("Enemy took $damage damage (HP: ${enemy.health})")
                }

            } else {
                println("No bullets left")
            }
        } ?: println("No magazine")
    }



    fun reload(newMagazine: Magazine){

        if (newMagazine.caliber==caliber)
        {
            magazine=newMagazine
            println("New magazine loaded")
        }
        else
        {
            println("Magazine is wrong caliber")
        }
    }

}

class AK74N():Gun(){
    override val caliber = "5.45x39mm"
    override val damage = 36
    override val statusEffectChance=listOf(0.3f,0.15f,0.05f)
}

class ASH12():Gun(){
    override val caliber = "12.7x55mm"
    override val damage = 36
    override val statusEffectChance=listOf(0.2f,0.3f,0.15f)
}

enum class StatusEffect(val condition:Int){
    UNIMPEDED(0),
    LIGHTBLEED(1),
    HEAVYBLEED(2),
    BROKENLIMB(3)

}

fun getRandomStatusEffect(probabilities:List<Float>):StatusEffect{
    var roll = Random.nextFloat()
    val result = when {
        roll < probabilities[0] -> StatusEffect.LIGHTBLEED
        roll < probabilities[0] + probabilities[1] -> StatusEffect.HEAVYBLEED
        roll < probabilities[0] + probabilities[1] + probabilities[2] -> StatusEffect.BROKENLIMB
        else -> StatusEffect.UNIMPEDED
    }
    return result
}

sealed class Medicine{
    abstract fun heal(player:Player)

    data object Bandage:Medicine(){
        override fun heal(player:Player){
            println("Healed light bleed")
            player.status = StatusEffect.UNIMPEDED
        }
    }

    data object Salewa:Medicine(){
        override fun heal(player: Player){
            println("Healed heavy bleed")
            player.status = StatusEffect.UNIMPEDED
        }
    }

    data object Splint:Medicine(){
        override fun heal(player: Player){
            println("Healed broken bone")
            player.status = StatusEffect.UNIMPEDED
        }
    }

}

abstract class Enemy(){
   abstract var health: Int
   abstract val baseDamage: Int
   abstract val statusEffectChance:List<Float>
   abstract fun attack(player:Player)
}

class Boar():Enemy(){
    override var health=HEALTH
    override val baseDamage=BASE_DAMAGE
    override val statusEffectChance=STATUS_EFFECTS
    override fun attack(player:Player) {
        player.health -= baseDamage
        player.status=getRandomStatusEffect(statusEffectChance)

    }

    companion object{
        const val BASE_DAMAGE = 25
        const val  HEALTH = 15
        val STATUS_EFFECTS = listOf(0.1f,0.2f,0.3f)

        fun getStats(){
            val lightBleedChance=STATUS_EFFECTS[0]
            val heavyBleedChance=STATUS_EFFECTS[1]
            val brokenBoneChance=STATUS_EFFECTS[2]
            println("Boar stats: \n->Damage: $BASE_DAMAGE " +
                    "\n-> Health: $HEALTH \nStatus effect chances: " +
                    "\n -Light bleed: $lightBleedChance" +
                    "\n -Heavy bleed chance: $heavyBleedChance" +
                    "\n -Broken bone chance: $brokenBoneChance  ")
        }
    }

}

class Backpack(){
    private val items: MutableMap<String, Float> = mutableMapOf()

    fun addItem(name: String, weight: Float) {
        items[name] = weight
    }

    fun removeItem(name: String) {
        if (items.remove(name) != null) {
            println("$name removed")
        } else {
            println("$name not found")
        }
    }

    fun listAll() {
        if (items.isEmpty()) {
            println("Backpack is empty")
        } else {
            items.forEach { (name, weight) ->
                println("$name -> $weight kg")
            }
        }
    }

    fun currentWeight(): Float {
        return items.values.sum()
    }

    fun searchItem(name: String) {
        val weight = items[name]
        if (weight != null) {
            println("$name weighs $weight kg")
        } else {
            println("$name not found")
        }
    }

    fun byWeightPrint() {
        val sorted = items.toList().sortedBy { it.second }

        sorted.forEach { (name, weight) ->
            println("$name -> $weight kg")
        }
    }


}

interface FpsMainCharacter{
    fun reloadWeapon(magazine:Magazine)
    fun fireWeapon()
    fun checkMagazine()
}

object Player:FpsMainCharacter{


    val backpack=Backpack()
    private val mainWeapon=AK74N()
    var health:Int by Delegates.observable(100){
        property, oldValue, newValue ->
        println("Player took damage: $oldValue -> $newValue")
        if (newValue<=0)
            println("Player died")
    }
    var status:StatusEffect by Delegates.observable(StatusEffect.UNIMPEDED){
        property, oldValue, newValue ->
        println("Player status changed: $oldValue -> $newValue")
        when(newValue){
            StatusEffect.LIGHTBLEED -> println("Player is bleeding lightly")
            StatusEffect.HEAVYBLEED -> println("Player is bleeding heavily")
            StatusEffect.BROKENLIMB -> println("Player has broken limb")
            StatusEffect.UNIMPEDED -> println("Player is feeling fine")
        }
    }

    fun heal(){
        val medicine = when (status) {
            StatusEffect.LIGHTBLEED -> Medicine.Bandage
            StatusEffect.HEAVYBLEED -> Medicine.Salewa
            StatusEffect.BROKENLIMB -> Medicine.Splint
            StatusEffect.UNIMPEDED -> null
        }

        if (medicine != null) {
            medicine.heal(this)
        } else {
            println("No healing needed")
        }

    }

    override fun reloadWeapon(magazine:Magazine){
        mainWeapon.reload(magazine)
    }

    override fun fireWeapon(){
        if (mainWeapon.magazine!=null)
        {
            mainWeapon.fire()
        }
        else
        {
            println("No magazine in gun")
        }
    }

    fun fireWeapon(target: Enemy) {
        if (mainWeapon.magazine != null) {
            mainWeapon.fireAtTarget(target)

        } else {
            println("No magazine in gun")
        }
    }

    override fun checkMagazine(){

        if (mainWeapon.magazine!=null)
        {
            val bullets=mainWeapon.magazine?.currentCapacity
            println("gun has magazine with $bullets bullets remaining")
        }
        else
            println("no magazine in gun")
    }

}

fun main() {

    println("=== GAME START ===")


    println("\n--- Player Setup ---")
    val mag = Magazine(30, "5.45x39mm")
    mag.load(20)

    Player.reloadWeapon(mag)
    Player.checkMagazine()


    println("\n--- Backpack ---")
    Player.backpack.addItem("Bandage", 0.5f)
    Player.backpack.addItem("Ammo", 1.0f)
    Player.backpack.listAll()
    println("Total weight: ${Player.backpack.currentWeight()}")


    println("\n--- Enemy Spawn ---")
    val boar = Boar()
    Boar.getStats()


    println("\n--- Player Attacks ---")
    Player.fireWeapon(boar)


    val effect = getRandomStatusEffect(listOf(0.3f, 0.15f, 0.05f))
    println("Random effect applied: $effect")
    Player.status = effect

/*
    println("\n--- Enemy Attacks ---")
    boar.attack(Player)
*/

    println("\n--- Healing ---")
    Player.heal()


    println("\n--- Firing Until Empty ---")
    repeat(25) {
        Player.fireWeapon()
    }

    Player.checkMagazine()

}