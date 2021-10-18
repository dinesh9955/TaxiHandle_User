package cabuser.com.rydz.ui.ride

data class ScheduleRideRequest (var adminId: String,var userId:String,var vehicleType:String, var source:RideSource,var destination:RideSource, var fare:Double,var scheduleTime:Long,var timeSlot:String,var paymentMode:String,
                                var firstName: String,var lastName: String,var phone: String,var notes:String,var subtotalFare:Double,var couponAmount:Double,var couponCode:String,var tax:Double,var pending:Double,var walletAmountUsed:Double,var isPaid:Double
,var distance:Double
)


