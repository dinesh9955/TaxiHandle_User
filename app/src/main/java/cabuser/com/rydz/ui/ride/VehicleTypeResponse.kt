package cabuser.com.rydz.ui.ride

data class VehicleTypeResponse(
      public  var cardList: List<Any> = listOf(),
      public var promo: Promo = Promo(),
      public var rating: Double = 0.0, // 4.5
      public var success: Boolean = false, // true
      public var vehicleTypeList: List<VehicleTypeX> = listOf(),
      public var walletAmount: Double = 0.0 ,// 0,
              public var pending: Double = 0.0 ,// 0
              public var tax: Double = 0.0 // 0

)