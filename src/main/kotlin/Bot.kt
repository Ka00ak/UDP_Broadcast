import java.lang.Thread.sleep
import java.net.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


class Bot {

    private lateinit var ip: InetAddress
    private var otherNodes = mutableSetOf<Node>()

    fun work() {
        // Hack to get current ip
        val testSocket = Socket("www.google.com", 80)
        ip = testSocket.localAddress
        println("Current IP: $ip")
        testSocket.close()

        //
        receiveUdp()
        receiveMessages()

        sendUdp()
        sendMessages()
    }


    private fun sendUdp() {
        Thread(Runnable {
            val byteArray = "$NEW_NODE_REQUEST ${ip.hostAddress}".toByteArray()

            val packet = DatagramPacket(byteArray, byteArray.size, InetAddress.getByName(HOST), UDP_BROADCAST_PORT)
            val socket = DatagramSocket(SEND_PORT)
            socket.send(packet)
            socket.close()
        }).start()
    }

    private fun receiveUdp() {
        Thread(Runnable {
            val socket = DatagramSocket(UDP_BROADCAST_PORT)
            while (true) {
                val buf = ByteArray(BUFFER_SIZE)

                val packet = DatagramPacket(buf, buf.size)
                socket.receive(packet)
                val clientDatas = String(packet.data).replace("\u0000", "").split(" ")
                val clientAddress = packet.address
                if (NEW_NODE_REQUEST == clientDatas[0]) {
                    if (ip.hostAddress == clientDatas[1]) continue

                    println("New node [$clientAddress] connects by UDP_BROADCAST_PORT($UDP_BROADCAST_PORT)")
                    otherNodes.add(Node(clientAddress))
                    println("Current list of nodes: $otherNodes")
                } else if (CHECK_REQUEST == clientDatas[0]) {
                    val sock = Socket(clientDatas[1], clientDatas[2].toInt())
                    sock.getOutputStream().write("$WORKING_ANSWER ${ip.hostAddress}".toByteArray())
                    sock.close()
                }
            }
        }).start()
    }

    private fun receiveMessages(){
        Thread(Runnable {
            val serverSocket = ServerSocket(RECEIVE_PORT)
            while(true) {
                val clientSocket = serverSocket.accept()
                Thread(Runnable {
                    val clientAddress = clientSocket.inetAddress

                    val request = clientSocket.getInputStream().bufferedReader().readText()
                    if (POWER_OFF_REQUEST == request) {
                        println("Power off request from [$clientAddress]")
                        exitProcess(0)
                    } else {
                        println("Request $request from [$clientAddress]")
                        otherNodes.add(Node(clientSocket.inetAddress))
                    }

                    clientSocket.close()
                }).start()
            }
        }).start()
    }

    private fun sendMessages() {
        Thread(Runnable {
            while (true) {
                sleep(DELAY_MS)
                println("Current nodes: $otherNodes")

                if (otherNodes.isNotEmpty()) {
                    val index = Random(Date().time).nextInt(otherNodes.size)
                    val node = otherNodes.elementAt(index)

                    try {
                        val clientSocket = Socket(node.address, RECEIVE_PORT)
                        clientSocket.getOutputStream().write("Request".toByteArray())
                        clientSocket.close()
                        println("Sent Request to ${node.address}")
                    }catch (exception: Exception) {
                        otherNodes.remove(node)
                        println("Old node [${node.address}] disconnected and removed")
                    }
                }
            }
        }).start()
    }
}

private data class Node(
    val address: InetAddress
//    val port: Int
)
