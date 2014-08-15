package avos.models;

/**
 * Created by Lewis on 8/15/14.
 */
public class BubbleEntity {
    private String nickName;

    private int id;

    private String Message;

    public BubbleEntity(String nickName, String message, String userId) {
        this.nickName = nickName;
        this.Message = message;
        this.userId = userId;

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }


}
