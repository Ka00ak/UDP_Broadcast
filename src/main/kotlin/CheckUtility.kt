import java.net.*
import kotlin.system.exitProcess

class CheckUtility {
    fun work() {
        // Hack to get current ip
        val testSocket = Socket("www.google.com", 80)
        val ip = testSocket.localAddress
        testSocket.close()

        receiveMessages()

        // Work
        while (true) {
            readLine()?.let {
                val args = it.split(" ")

                when (args[0]) {
                    "check" -> {
                        val byteArray = "$CHECK_REQUEST ${ip.hostAddress} $CHECK_RECEIVER_PORT".toByteArray()

                        val packet = DatagramPacket(byteArray, byteArray.size, InetAddress.getByName(HOST), UDP_BROADCAST_PORT)
                        val socket = DatagramSocket(SEND_PORT)
                        socket.send(packet)
                        socket.close()
                    }
                    "poweroff" -> {
                        val clientSocket = Socket(args[1], RECEIVE_PORT)
                        clientSocket.getOutputStream().write(POWER_OFF_REQUEST.toByteArray())
                        clientSocket.close()
                    }
                }
            }
        }
    }

    private fun receiveMessages() {
        Thread(Runnable {
            val serverSocket = ServerSocket(CHECK_RECEIVER_PORT)
            while (true) {
                val clientSocket = serverSocket.accept()
                Thread(Runnable {

                    val request = clientSocket.getInputStream().bufferedReader().readText().split(" ")
                    if (WORKING_ANSWER == request[0])
                        println("[${request[1]}] node is working")
                    clientSocket.close()
                }).start()
            }
        }).start()
    }
}