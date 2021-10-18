package cabuser.com.rydz.ui.chat;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

public class MessageList implements Parcelable {
    public final static Parcelable.Creator<MessageList> CREATOR;

    static {
        CREATOR = new Creator<MessageList>() {

            public MessageList createFromParcel(Parcel in) {
                return new MessageList(in);
            }

            public MessageList[] newArray(int size) {
                return (new MessageList[size]);
            }

        };
    }

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("driver")
    @Expose
    private Driver driver;
    @SerializedName("bookingId")
    @Expose
    private String bookingId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("date")
    @Expose
    private Long date;
    @SerializedName("__v")
    @Expose
    private Long v;
    @SerializedName("status")
    @Expose
    private Long status;
    @SerializedName("opponentReadStatus")
    @Expose
    private Long opponentReadStatus;
    @SerializedName("userReadStatus")
    @Expose
    private Long userReadStatus;
    @SerializedName("messageType")
    @Expose
    private Long messageType;
    @SerializedName("messageBy")
    @Expose
    private Integer messageBy;

    private MessageList(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.driver = ((Driver) in.readValue((Driver.class.getClassLoader())));
        this.bookingId = ((String) in.readValue((String.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.date = ((Long) in.readValue((Long.class.getClassLoader())));
        this.v = ((Long) in.readValue((Long.class.getClassLoader())));
        this.status = ((Long) in.readValue((Long.class.getClassLoader())));
        this.opponentReadStatus = ((Long) in.readValue((Long.class.getClassLoader())));
        this.userReadStatus = ((Long) in.readValue((Long.class.getClassLoader())));
        this.messageType = ((Long) in.readValue((Long.class.getClassLoader())));
        this.messageBy = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getV() {
        return v;
    }

    public void setV(Long v) {
        this.v = v;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }


    public Integer getMessageBy() {
        return messageBy;
    }


    @NotNull
    @Override
    public String toString() {
        String s = new ToStringBuilder(this).append("id", id).append("user", user).append("driver", driver).append("bookingId", bookingId).append("message", message).append("date", date).append("v", v).append("status", status).append("opponentReadStatus", opponentReadStatus).append("userReadStatus", userReadStatus).append("messageType", messageType).append("messageBy", messageBy).toString();
        return s;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(user);
        dest.writeValue(driver);
        dest.writeValue(bookingId);
        dest.writeValue(message);
        dest.writeValue(date);
        dest.writeValue(v);
        dest.writeValue(status);
        dest.writeValue(opponentReadStatus);
        dest.writeValue(userReadStatus);
        dest.writeValue(messageType);
        dest.writeValue(messageBy);
    }

    public int describeContents() {
        return 0;
    }

}