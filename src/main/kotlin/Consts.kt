import java.util.concurrent.TimeUnit

const val UDP_BROADCAST_PORT = 5554
const val RECEIVE_PORT = 5555
const val SEND_PORT = 5556
const val HOST = "192.168.43.255"

const val BUFFER_SIZE = 128

const val NEW_NODE_REQUEST = "NEW_NODE_REQUEST"


var DELAY_MS = TimeUnit.SECONDS.toMillis(5)

// Check Utility
const val POWER_OFF_REQUEST = "POWER_OFF_REQUEST"
const val CHECK_REQUEST = "CHECK_REQUEST"
const val WORKING_ANSWER = "WORKING_ANSWER"

const val CHECK_RECEIVER_PORT = 6666
