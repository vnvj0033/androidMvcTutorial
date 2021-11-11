package com.example.mvctutorial.designpattern._13chain_of_resposibility.part2

fun main() {

    val attack = Attack(100)

    val armor1 = Armor(10)
    val armor2 = Armor(15)

    armor1.setNextDefense(armor2)
    armor1.defense(attack)

    println(attack.amount)
}
