package machine

fun main() {
    val coffeeMachine = CoffeeMachine()
    do {
        print("Write action (buy, fill, take, remaining, exit):")
        when (readLine()!!) {
            "buy" -> {
                print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:")
                val userChoice = readLine()!!
                if (userChoice == "back") continue
                coffeeMachine.readyToCook()
                println(coffeeMachine.processInput(userChoice))
                coffeeMachine.sleep()
            }
            "fill" -> {
                println("Write how many ml of water do you want to add: ")
                val water = readLine()!!.toInt()
                println("Write how many ml of milk do you want to add: ")
                val milk = readLine()!!.toInt()
                println("Write how many grams of coffee beans do you want to add: ")
                val beans = readLine()!!.toInt()
                println("Write how many disposable cups of coffee do you want to add: ")
                val cups = readLine()!!.toInt()
                coffeeMachine.readyToRefill()
                println(coffeeMachine.processInput(water = water, milk = milk, beans = beans, cups = cups))
                coffeeMachine.sleep()
            }
            "take" -> {
                coffeeMachine.readyToReturn()
                println(coffeeMachine.processInput("Money back"))
                coffeeMachine.sleep()
            }
            "remaining" -> {
                coffeeMachine.readyToShow()
                println(coffeeMachine.processInput("Show reserve"))
                coffeeMachine.sleep()
            }

            "exit" -> break
        }
    } while (true)
}

enum class Coffee(
    val waterPerCup: Int,
    val milkPerCup: Int,
    val coffeeBeansPerCup: Int,
    val cost: Int
) {
    ESPRESSO(250, 0, 16, 4),
    LATTE(350, 75, 20, 7),
    CAPPUCCINO(200, 100, 12, 6);
}

enum class State {
    STANDBY, MAKE_COFFEE, REFILL, RETURN_MONEY, SHOW_INFO
}

class CoffeeMachine(
    private var money: Int = 550,
    private var disposableCups: Int = 9,
    private var water: Int = 400,
    private var milk: Int = 540,
    private var coffee: Int = 120,
    private var state: State = State.STANDBY,
) {
    fun processInput(input: String = "", water: Int = 0, milk: Int = 0, beans: Int = 0, cups: Int = 0): String =
        when (state) {
            State.MAKE_COFFEE -> {
                when (input) {
                    "1" -> buy(Coffee.ESPRESSO)
                    "2" -> buy(Coffee.LATTE)
                    "3" -> buy(Coffee.CAPPUCCINO)
                    else -> "no such coffee"
                }
            }
            State.RETURN_MONEY -> take()
            State.REFILL -> {
                fill(water, milk, beans, cups)
            }
            State.SHOW_INFO -> remain()
            else -> "PROCESSING ERROR"
        }

    fun readyToCook() {
        state = State.MAKE_COFFEE
    }

    fun sleep() {
        state = State.STANDBY
    }

    fun readyToRefill() {
        state = State.REFILL
    }

    fun readyToReturn() {
        state = State.RETURN_MONEY
    }

    fun readyToShow() {
        state = State.SHOW_INFO
    }

    private fun buy(
        coffeeChoosen: Coffee
    ): String {
        val rs = mutableListOf<Int>()
        rs.add(water / coffeeChoosen.waterPerCup)

        if (coffeeChoosen != Coffee.ESPRESSO) {
            rs.add(milk / coffeeChoosen.milkPerCup)
        }
        rs.add(coffee / coffeeChoosen.coffeeBeansPerCup)
        rs.add(disposableCups / 1)
        val minCups = rs.minOrNull()
        //println(rs)
        if (minCups != null) {
            when {
                minCups >= 1 -> {
                    makeCoffee(coffeeChoosen)
                    return "I have enough resources, making you a coffee!"
                }
                else -> {
                    if (water < coffeeChoosen.waterPerCup)
                        return "Sorry, not enough water!"
                    if (milk < coffeeChoosen.milkPerCup)
                        return "Sorry, not enough milk!"
                    if (coffee < coffeeChoosen.coffeeBeansPerCup)
                        return "Sorry, not enough coffee!"
                    if (disposableCups < 1)
                        return "Sorry, not enough disposable cups!"

                }
            }
        }
        return ""
    }

    private fun makeCoffee(coffeeChoiced: Coffee) {
        water -= coffeeChoiced.waterPerCup
        milk -= coffeeChoiced.milkPerCup
        coffee -= coffeeChoiced.coffeeBeansPerCup
        disposableCups -= 1
        money += coffeeChoiced.cost
    }

    private fun fill(water: Int = 0, milk: Int = 0, coffee: Int = 0, disposableCups: Int = 0): String {
        this.water += water
        this.milk += milk
        this.coffee += coffee
        this.disposableCups += disposableCups
        return "fill successful"
    }

    private fun take(): String {
        val withdraw = money.toString()
        money = 0
        return withdraw
    }

    private fun remain(): String {
        return """
            The coffee machine has:
            $water of water
            $milk of milk
            $coffee of coffee beans
            $disposableCups of disposable cups
            $$money of money
            """.trimIndent()
    }
}