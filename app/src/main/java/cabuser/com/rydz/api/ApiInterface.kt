package cabuser.com.rydz.api

import cabuser.com.rydz.ui.chat.ChatHistoryRequest
import cabuser.com.rydz.ui.chat.MessageHistory
import cabuser.com.rydz.ui.freerides.RewardsResponse
import cabuser.com.rydz.ui.history.History
import cabuser.com.rydz.ui.history.RefundResponse
import cabuser.com.rydz.ui.home.SendOtpRequest
import cabuser.com.rydz.ui.home.SendSmsOtpResponse
import cabuser.com.rydz.ui.home.VerifyOtpRequest
import cabuser.com.rydz.ui.home.VerifySmsOtpResponse
import cabuser.com.rydz.ui.home.payment.ChargeWalletResponse
import cabuser.com.rydz.ui.home.payment.addcard.AddCardResponse
import cabuser.com.rydz.ui.home.payment.addcard.CardsListResponse
import cabuser.com.rydz.ui.home.payment.setupintent.SetUpIntentResponse
import cabuser.com.rydz.ui.profile.*
import cabuser.com.rydz.ui.register.*
import cabuser.com.rydz.ui.ride.DriverRatingResponse
import cabuser.com.rydz.ui.ride.ScheduleRideRequest
import cabuser.com.rydz.ui.settings.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @POST("user/register")
    fun onRegisteration(@Body registrationRequest: RegistrationRequest): Call<RegistrationResponse>


    /**
     * User  Login Api
     * @param adminId
     * @param email
     * @param password
     * @param deviceId
     * @param devicetype
     * @return
     */
    @FormUrlEncoded
    @POST("user/login")
    fun onEmailLogin(@Field("adminId") adminId: String,
                     @Field("email") email: String,
                     @Field("password") password: String,
                     @Field("deviceId") deviceId: String,
                     @Field("deviceType") devicetype: String): Call<ProfileResponse>


    /**
     * Social Login Api
     * @param inviteCodeUsed
     * @param adminId
     * @param email
     * @param firstName
     * @param deviceId
     * @param devicetype
     * @param lastName
     * @param isVerified
     * @return
     */
    @FormUrlEncoded
    @POST("user/sociallogin")
    fun onSocialMediaLogin(@Field("adminId") adminId: String,
                           @Field("firstName") firstName: String,
                           @Field("lastName") lastName: String,
                           @Field("email") email: String,
                           @Field("facebookId") facebookId: String,
                           @Field("deviceId") deviceId: String,
                           @Field("deviceType") devicetype: String,
                           @Field("phone") mobile: String,
                           @Field("countryCode") contryCode: String,
                           @Field("inviteCodeUsed") inviteCodeUsed: String,
                           @Field("isVerified") isVerified: String): Call<RegistrationResponse>


    /**
     * Google Login Api
     * @param inviteCodeUsed
     * @param adminId
     * @param email
     * @param firstName
     * @param deviceId
     * @param devicetype
     * @param lastName
     * @param isVerified
     * @return
     */
    @FormUrlEncoded
    @POST("user/sociallogin")
    fun onGoogleLogin(@Field("adminId") adminId: String,
                      @Field("firstName") firstName: String,
                      @Field("lastName") lastName: String,
                      @Field("email") email: String,
                      @Field("googleId") googleId: String,
                      @Field("deviceId") deviceId: String,
                      @Field("deviceType") devicetype: String,
                      @Field("phone") mobile: String,
                      @Field("countryCode") contryCode: String,
                      @Field("inviteCodeUsed") inviteCodeUsed: String,
                      @Field("isVerified") isVerified: String): Call<RegistrationResponse>


    /**
     * Get User Profile Api
     */
    @GET("user/{id}")
    fun getUser(@Path("id") id: String): Call<ProfileResponse>


    /**
     * update user  profile Api
     * @param adminId
     * @param userId
     * @param firstname
     * @param lastname
     * @param mobile
     * @param contryCode
     * @return
     */
    @PUT("user/profile")
    fun onUpdateProfile(@Body updateProfileRequest: UpdateProfileRequest): Call<ProfileResponse>


    /**
     * update user  email Api
     * @param adminId
     * @param userId
     * @param email
     * @return
     */

    @PUT("user/email")
    fun onUpdateEmail(@Body editEmailRequest: EditEmailRequest
    ): Call<ProfileResponse>


    /**
     * change user  password Api
     * @param adminId
     * @param userId
     * @param email
     * @return
     */
    @PUT("user/password")
    fun onChangePassword(
            @Body editPasswordRequest: EditPasswordRequest
    ): Call<ProfileResponse>


    /**
     * upload user profile Api
     * @param userId
     * @param email
     * @return
     */

    @Multipart
    @PUT("user/profilepic")
    fun onProfilePicUpload(
            @Part("userId") userId: RequestBody,
            @Part image: MultipartBody.Part): Call<ProfileResponse>


    @POST("user/phonelogin")
    fun onPhoneLogin(@Body userCheckRequest: UserCheckRequest): Call<RegistrationResponse>


    /**
     *rate driver Api
     * @param adminId
     * @param userId
     * @param email
     * @return
     */
    @FormUrlEncoded
    @POST("rating/driver")
    fun onRateDriver(@Field("driverId") driverId: String,
                     @Field("userId") userId: String,
                     @Field("bookingId") bookingId: String,
                     @Field("rating") rating: String
                     , @Field("review") review: String
    ): Call<DriverRatingResponse>


    /**
     * check phone status Api
     * @param adminId
     * @param userId
     * @param contryCode
     * @param phone
     * @return
     */
    @PUT("user/phonestatus")
    fun onCheckPhoneStatus(
            @Body editNumberRequest: EditNumberRequest

    ): Call<CheckPhoneStatusModel>


    /**
     * Social Registration Api
     * @param inviteCodeUsed
     * @param adminId
     * @param email
     * @param firstName
     * @param deviceId
     * @param devicetype
     * @param lastName
     * @param isVerified
     * @return
     */
    @POST("user/socialregister")
    fun onSocialMedia_FB_Registeration(@Body socialMedia_FB_RegistrationRequest: SocialMedia_FB_RegistrationRequest): Call<RegistrationResponse>


    @POST("user/socialregister")
    fun onSocialMedia_GOOGLE_Registeration(@Body socialMedia_GOOGLE_RegistrationRequest: SocialMedia_GOOGLE_RegistrationRequest): Call<RegistrationResponse>

    /**
     * Get User past trips  Api
     */
    @FormUrlEncoded
    @POST("booking/user/history")
    fun getPastTrips(@Field("userId") userId: String, @Field("skip") skip: Int): Call<History>

    //Get user user schedule
    @GET("trip/schedule")
    fun getscheduleTrips(@Query("userId") userId: String): Call<History>

    //Cancel Schedule ride
    @GET("trip/schedule/cancel/{id}")
    fun cancelRide(@Path("id") id: String): Call<CheckPhoneStatusModel>

    /**
     * Get chat history Api
     */

    @POST("message/gethistory")
    fun getMessageHistory(@Body chatHistoryRequest: ChatHistoryRequest): Call<MessageHistory>


    /**
     * support api
     */
    @POST("user/support")
    fun onSendFeedback(@Body submitFeedbackRequest: SubmitFeedbackRequest): Call<SupportResponse>

    @POST("user/notisetting")
    fun onNotificationSettingsChange(@Body notificationRequest: NotificationRequest): Call<NotificationResponse>

    @FormUrlEncoded
    @POST("user/coupon")
    fun onAddPromoCode(@Field("code") code: String, @Field("adminId") adminId: String, @Field("userId") userId: String): Call<SupportResponse>


    /**
     * Get User rewards Api
     */
    @GET("user/rewards/{id}")
    fun getRewards(@Path("id") id: String): Call<RewardsResponse>


    /**
     * Redeem user rewards Api
     */
    @FormUrlEncoded
    @POST("user/redeemrewards")
    fun redeemRewards(@Field("adminId") adminId: String, @Field("userId") userId: String, @Field("rewardPoints") rewardPoints: String): Call<RewardsResponse>


    /**
     * change password  Api
     * @param adminId
     * @param userId
     * @param contryCode
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("user/forgotchangepass")
    fun onForgotChangePassword(
            @Field("adminId") adminId: String,
            @Field("email") email: String,
            @Field("countryCode") contryCode: String,
            @Field("newPassword") newPassword: String,
            @Field("phone") phone: String

    ): Call<CheckPhoneStatusModel>



    @FormUrlEncoded
    @POST("user/sendmailotp")
    fun onSendmailOtp(
            @Field("adminId") adminId: String,
            @Field("email") email: String

    ): Call<CheckPhoneStatusModel>


    /**
     * veryfyotp Api
     * @param adminId
     * @param userId
     * @param contryCode
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("user/veryfyotp")
    fun onVerifyOtp(
            @Field("adminId") adminId: String,
            @Field("email") email: String,
            @Field("code") code: String

    ): Call<CheckPhoneStatusModel>


    /**
     * check reg status Api
     * @param adminId
     * @param userId
     * @param contryCode
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("user/chkregstatus")
    fun onChkregstatus(
            @Field("adminId", encoded = false) adminId: String,
            @Field("email") email: String,
            @Field("countryCode") contryCode: String,
            @Field("phone") newPassword: String

    ): Call<CheckPhoneStatusModel>

    //schedule booking Api
    @POST("booking/schedule")
    fun onBookScheduleRide(@Body scheduleRideRequest: ScheduleRideRequest): Call<CheckPhoneStatusModel>

    //Get contact us Api
    @GET("admin/customer/contactUs")
    fun getContactUs(): Call<ContactUsResponse>

    //send otp Api
    @PUT("customer/sendOtp")
    fun sendOtp(@Body sendOtpRequest: SendOtpRequest): Call<SendSmsOtpResponse>

    //verify otp Api
    @PUT("customer/verifyOtp")
    fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Call<VerifySmsOtpResponse>





   // PAYMENT APIS
    @FormUrlEncoded
    @POST("user/addCard")
    fun addPaymentCard(
            @Field("userId") userId: String,
            @Field("stripePaymentMethod") stripePaymentMethod: String


    ): Call<AddCardResponse>



    @FormUrlEncoded
    @POST("user/setupIntent")
    fun setupIntent(@Field("userId") userId: String): Call<SetUpIntentResponse>

   @FormUrlEncoded
    @POST("user/getCard")
    fun getCardsList(@Field("userId") userId: String): Call<CardsListResponse>



   @FormUrlEncoded
    @POST("user/removeCard")
    fun removeCard(@Field("cardId") cardId: String,@Field("userId") userId: String): Call<CardsListResponse>



   @FormUrlEncoded
    @POST("user/chargeWallet")
    fun addMoneyToWallet(@Field("cardId") cardId: String,@Field("amount") amount: Double,@Field("userId") userId: String): Call<ChargeWalletResponse>


 @FormUrlEncoded
    @POST("user/refundRequest")
    fun refundRequest(@Field("bookingId") bookingId: String,@Field("reason") reason: String,@Field("description") description: String): Call<RefundResponse>




}


