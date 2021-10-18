package cabuser.com.rydz.ui.payment

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.ui.home.payment.ChargeWalletResponse
import cabuser.com.rydz.ui.home.payment.addcard.AddCardResponse
import cabuser.com.rydz.ui.home.payment.addcard.CardsListResponse
import cabuser.com.rydz.ui.home.payment.setupintent.SetUpIntentResponse
import cabuser.com.rydz.util.common.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    var activity: Context = application.applicationContext
    var mProgress: SingleLiveEvent<Boolean>? = null

    var setUpIntentResponse: MutableLiveData<SetUpIntentResponse>? = null
    var addCardResponse: MutableLiveData<AddCardResponse>? = null
    var cardsListResponse: MutableLiveData<CardsListResponse>? = null
    var deleteCardResponse: MutableLiveData<CardsListResponse>? = null
    var addMoneyToWalletResponse: MutableLiveData<ChargeWalletResponse>? = null


    init {

        mProgress = SingleLiveEvent<Boolean>()
        setUpIntentResponse = MutableLiveData<SetUpIntentResponse>()
        cardsListResponse = MutableLiveData<CardsListResponse>()
        deleteCardResponse = MutableLiveData<CardsListResponse>()
        addCardResponse = MutableLiveData<AddCardResponse>()
        addMoneyToWalletResponse = MutableLiveData<ChargeWalletResponse>()


    }


    fun setUpIntentApi(userId: String) {
        mProgress?.value = true
        RydzApplication.getRetroApiClient().setupIntent(userId).enqueue(object : Callback<SetUpIntentResponse> {
            override fun onFailure(call: Call<SetUpIntentResponse>, t: Throwable) {
                mProgress?.value = false

            }

            override fun onResponse(call: Call<SetUpIntentResponse>, response: Response<SetUpIntentResponse>) {

                mProgress?.value = false
                try {
                    setUpIntentResponse?.value = response.body()
                } catch (e: Exception) {

                }

            }
        })

    }


    fun addNewCard(userId: String, pmId: String) {
        mProgress?.value = true
        RydzApplication.getRetroApiClient().addPaymentCard(userId, pmId).enqueue(object : Callback<AddCardResponse> {
            override fun onFailure(call: Call<AddCardResponse>, t: Throwable) {
                mProgress?.value = false

            }

            override fun onResponse(call: Call<AddCardResponse>, response: Response<AddCardResponse>) {

              //  mProgress?.value = false

                    addCardResponse?.value = response.body()


            }
        })

    }
    fun getAllCards(userId: String) {
        mProgress?.value = true
        RydzApplication.getRetroApiClient().getCardsList(userId).enqueue(object : Callback<CardsListResponse> {
            override fun onFailure(call: Call<CardsListResponse>, t: Throwable) {
                mProgress?.value = false

            }

            override fun onResponse(call: Call<CardsListResponse>, response: Response<CardsListResponse>) {

                mProgress?.value = false
                try {
                    cardsListResponse?.value = response.body()
                } catch (e: Exception) {

                }

            }
        })

    }
  fun onDeleteCard(cardId : String,userId: String) {
      mProgress?.value = true
        RydzApplication.getRetroApiClient().removeCard(cardId,userId).enqueue(object : Callback<CardsListResponse> {
            override fun onFailure(call: Call<CardsListResponse>, t: Throwable) {
                mProgress?.value = false

            }

            override fun onResponse(call: Call<CardsListResponse>, response: Response<CardsListResponse>) {


                try {
                    deleteCardResponse?.value = response.body()
                } catch (e: Exception) {

                }

            }
        })

    }
    fun onMakeDefaultCard(cardId : String,userId: String) {

        RydzApplication.getRetroApiClient().getCardsList(userId).enqueue(object : Callback<CardsListResponse> {
            override fun onFailure(call: Call<CardsListResponse>, t: Throwable) {
                mProgress?.value = false

            }

            override fun onResponse(call: Call<CardsListResponse>, response: Response<CardsListResponse>) {

                mProgress?.value = false
                try {
                    cardsListResponse?.value = response.body()
                } catch (e: Exception) {

                }

            }
        })

    }
  fun onChargeWallet(cardId : String,amount : Double,userId: String) {

        RydzApplication.getRetroApiClient().addMoneyToWallet(cardId,amount ,userId).enqueue(object : Callback<ChargeWalletResponse> {
            override fun onFailure(call: Call<ChargeWalletResponse>, t: Throwable) {
                mProgress?.value = false

            }

            override fun onResponse(call: Call<ChargeWalletResponse>, response: Response<ChargeWalletResponse>) {

                mProgress?.value = false
                try {
                    addMoneyToWalletResponse?.value = response.body()
                } catch (e: Exception) {

                }

            }
        })

    }


}