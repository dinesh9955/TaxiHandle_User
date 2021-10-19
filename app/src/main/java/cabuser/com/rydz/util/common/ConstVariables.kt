package cabuser.com.rydz.util.common


interface ConstVariables {

    companion object {

        //Socket endpoints
        const val GETDRIVERS = "getDrivers"
        const val USERRIDESTATUS = "userRideStatus"
        const val SENDBOOKING = "sendBooking"
        const val USERCHANGESTATUS = "userChangeStatus"
        const val NEWMESSAGE = "newmessage"
        const val CANCELREQUEST="cancelRequest"
        const val  JOINPOOLRIDE="joinPoolRide";
        const val READMESSAGE = "readMessage"

        // params Key
        const val DRIVER_ID = "driverId"
        const val MESSAGE = "message"
        const val MESSAGE_BY = "messageBy"
        const val OPPONENT_ID = "opponentChatId"
        const val MY_CHAT_ID = "myChatId"
        const val BOOKING_ID = "bookingId"
        const val ADMIN_ID = "adminId"

      const val STRIPEPUBLISHKEY="pk_test_00ZCZYW3EpLGlhmzmwH3VvUC"
//      const val STRIPEPUBLISHKEY="pk_live_XHiPxly2V9VEdn5fiF1qXsBz"


    }
}