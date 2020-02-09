import java.net.DatagramPacket
import java.net.DatagramSocket

class MyRunnable(
    private val packet: DatagramPacket
) : Runnable{
    override fun run() {
        val answerAddress = packet.address
        val answerPort = packet.port
        val modifiedSentence = String(packet.data)
        println("FROM SERVER:$modifiedSentence")
        println("$answerAddress $answerPort")
    }
}
