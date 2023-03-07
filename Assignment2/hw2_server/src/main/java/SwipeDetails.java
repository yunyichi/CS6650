/**
 * a class represents the set of details in swipe
 * By Yunyi Chi
 * */

public class SwipeDetails {
    private int swiper;
    private int swipee;
    private String comment;
    /*
    * constructor
    */
    public SwipeDetails(int swiper, int swipee, String comment) {
        this.swiper = swiper;
        this.swipee = swipee;
        this.comment = comment;
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
     * getter of swipe
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
