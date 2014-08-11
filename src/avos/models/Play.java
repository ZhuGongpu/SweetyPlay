package avos.models;

import avos.AVOSWrapper;
import com.avos.avoscloud.*;

import java.util.Date;

/**
 * Created by zhugongpu on 14-8-11.
 */
@AVClassName("Play")
public class Play extends AVObject {

    String activityPhotoThumbnailUrl = null;

    public AVFile getActivityPhoto() {
        return getAVFile("ActivityPhoto");
    }

    public void setActivityPhoto(AVFile file) {
        put("ActivityPhoto", file);
    }

    public String getDescription() {
        return getString("Description");
    }

    public void setDescription(String description) {
        put("Description", description);
    }

    public AVGeoPoint getPlace() {
        return (AVGeoPoint) get("Place");
    }

    public void setPlace(AVGeoPoint geoPoint) {
        put("Place", geoPoint);
    }

    public String getCreatorPhoneNumber() {
        return getString("CreatorPhoneNum");
    }

    public void setCreatorPhoneNumber(String phoneNumber) {
        put("CreatorPhoneNum", phoneNumber);
    }

    public int getCurrentNumber() {
        return getInt("CurrentNum");
    }

    public void setCurrentNumber(int number) {
        put("CurrentNum", number);
    }

    public String getTitle() {
        return getString("Title");
    }

    public void setTitle(String title) {
        put("Title", title);
    }

    public int getMaxNumber() {
        return getInt("MaxNum");
    }

    public void setMaxNumber(int maxNumber) {
        put("MaxNum", maxNumber);
    }

    public Date getDueDate() {
        return getDate("DueDate");
    }

    public void setDueDate(Date date) {
        put("DueDate", date);
    }

    public Date getPlayDate() {
        return getDate("PlayDate");
    }

    public void setPlayDate(Date date) {
        put("PlayDate", date);
    }

    public AVRelation<AVUser> getParticipationList() {
        return getRelation("ParticipationList");
    }

    public void setParticipationList(AVRelation<AVUser> relation) {
        put("ParticipationList", relation);
    }

    public String getThumbnailUrl() {
        if (activityPhotoThumbnailUrl == null) {
            activityPhotoThumbnailUrl = AVOSWrapper.getThumbnailUrl(getActivityPhoto(), 200, 200);
        }
        return activityPhotoThumbnailUrl;
    }
}
