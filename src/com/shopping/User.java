package com.shopping;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 23/11/11
 * Time: 18.51
 * User class should probably just be our SIObject class. User object can be persons or rollators as for now.
 * Besides the bad naming, it nothing more than a datacontainer.
 */
enum Gender {
    Male, Female;
}
public class User  implements Parcelable {
    private int userId;
    private String fullName, firstName, lastName, bio;
    private ArrayList<Movable> offers;
    //Not in our parcelable, we need to make this dada persistent ie with content provider.
    private UserActivity userActivity;
    private Bitmap userImage;
    private String location;
    private Date birthDay;
    private String imageUrl;
    private String imageUrlMedium;
    private String imageUrlSmall;
    private Gender gender;
    private String type;
   

    public User(){}

    public Bitmap getUserImage() {
        //TODO get use image from server if possible
        if(userImage == null){
            userImage = BitmapFactory.decodeResource(GalleryActivity.getContext().getResources(), R.drawable.senior1_80px);
        }
        return userImage;
    }

    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }

       // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(userId);
        out.writeString(fullName);
        out.writeList(offers);
        getUserImage().writeToParcel(out, PARCELABLE_WRITE_RETURN_VALUE);
        out.writeString(location);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(imageUrl);
        out.writeString(imageUrlSmall);
        out.writeInt(Gender.Male.ordinal());
        out.writeString(bio);
        out.writeString(type);
        if(userActivity==UserActivity.Shopping)
            out.writeInt(1);
        else
            out.writeInt(0);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private User(Parcel in) {
        userId = in.readInt();
        fullName = in.readString();
        ArrayList<Movable> list1 = new ArrayList<Movable>();
        in.readList(list1, Movable.class.getClassLoader());
        offers = list1;
        userImage = Bitmap.CREATOR.createFromParcel(in);
        location = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        imageUrl = in.readString();
        imageUrlSmall = in.readString();
        int g = in.readInt();
        if(g == Gender.Male.ordinal())
            gender = Gender.Male;
        else
            gender = Gender.Female;
        bio = in.readString();
        type = in.readString();
        if(in.readInt()==1)
            setUserActivity(UserActivity.Shopping);
        else
            setUserActivity(UserActivity.Unknown);
    }


    /**
     * Getters and setters
     * @return
     */
    public ArrayList<Movable> getOffers() {
        if(offers == null)
            offers = new ArrayList<Movable>();
        return offers;
    }

    public void setOffers(ArrayList<Movable> offers) {
        this.offers = offers;
    }

    public void addOffer(ShoppingOffer newOffer){
        getOffers().add(newOffer);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrlSmall() {
        return imageUrlSmall;
    }

    public void setImageUrlSmall(String imageUrlSmall) {
        this.imageUrlSmall = imageUrlSmall;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getLocation() {
        if(location == null || location.isEmpty()) location = ""; // " \"sted kendes ikke\".";
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserActivity getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(UserActivity userActivity) {
        this.userActivity = userActivity;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrlMedium() {
        return imageUrlMedium;
    }

    public void setImageUrlMedium(String imageUrlMedium) {
        this.imageUrlMedium = imageUrlMedium;
    }

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
    
    
}
