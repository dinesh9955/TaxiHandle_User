package cabuser.com.rydz.ui.profile;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

public class User {

    @SerializedName("loginType")
    @Expose
    private Integer loginType;
    @Nullable
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("inviteCode")
    @Expose
    private String inviteCode;
    @SerializedName("inviteCodeUsed")
    @Expose
    private String inviteCodeUsed;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("isFullUrl")
    @Expose
    private Integer isFullUrl;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("id")
    @Expose
    private String id;
    @Nullable
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @Nullable
    @SerializedName("phone")
    @Expose
    private String phone;
    @Nullable
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("sendNoti")
    @Expose
    private Integer sendNoti;
    @SerializedName("isBroadcast")
    @Expose
    private Integer isBroadcast;


    @SerializedName("rewardPoints")
    @Expose
    private Double rewardPoints;

  @SerializedName("isSubscribed")
    @Expose
    private Number isSubscribed;

 @SerializedName("company")
    @Expose
    private String company;

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    @Nullable
    public Double getRating() {
        return rating;
    }

    public void setRating(@Nullable Double rating) {
        this.rating = rating;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getInviteCodeUsed() {
        return inviteCodeUsed;
    }

    public void setInviteCodeUsed(String inviteCodeUsed) {
        this.inviteCodeUsed = inviteCodeUsed;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public Integer getIsFullUrl() {
        return isFullUrl;
    }

    public void setIsFullUrl(Integer isFullUrl) {
        this.isFullUrl = isFullUrl;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@Nullable String countryCode) {
        this.countryCode = countryCode;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    public Integer getSendNoti() {
        return sendNoti;
    }

    public void setSendNoti(Integer sendNoti) {
        this.sendNoti = sendNoti;
    }

    public Integer getIsBroadcast() {
        return isBroadcast;
    }

    public void setIsBroadcast(Integer isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    public Double getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Double rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public Number getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(Number isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}