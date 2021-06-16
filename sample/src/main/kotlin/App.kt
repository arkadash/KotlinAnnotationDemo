import me.arkadash.example.MyConstant
import me.arkadash.example.MyFaker
import me.arkadash.sdk.genereted.GeneratedConstants.Dinora

fun main() {
    println(Dinora)
}

@MyConstant(propName = "Dinora", "Backend")
fun Quala() {
}

@MyFaker
class Ball(
    val color: String,
    val owner: String,
    val size: Int
)