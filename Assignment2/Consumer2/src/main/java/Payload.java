/**
 * Payload which contains the information of swiper
 * swipee, comment and user's leftOrRight(like or dislike)
 * By Yunyi Chi
 * */
/*
 * Payload class
 */
public class Payload {
    private String leftOrRight;
    private int swiper;
    private int swipee;
    private String comment;

    /*
     * constructor of Payload
     */
    public Payload(String leftOrRight, int swiper, int swipee, String comment) {
        this.leftOrRight = leftOrRight;
        this.swiper = swiper;
        this.swipee = swipee;
        this.comment = comment;
    }
    /*
     * getter of leftOrRight
     */
    public String getLeftOrRight() {
        return leftOrRight;
    }
    /*
     * setter of leftOrRight
     */
    public void setLeftOrRight(String leftOrRight) {
        this.leftOrRight = leftOrRight;
    }
    /*
     * getter of swiper
     */
    public int getSwiper() {
        return swiper;
    }
    /*
     * setter of swiper
     */
    public void setSwiper(int swiper) {
        this.swiper = swiper;
    }
    /*
     * getter of swipee
     */
    public int getSwipee() {
        return swipee;
    }
    /*
     * setter of swipee
     */
    public void setSwipee(int swipee) {
        this.swipee = swipee;
    }
    /*
     * getter of comment
     */
    public String getComment() {
        return comment;
    }
    /*
     * setter of comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}