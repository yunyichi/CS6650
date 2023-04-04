import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetMatchDao {
    private static BasicDataSource dataSource;

    public GetMatchDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public List<Integer> getMatch(int userId) {
        List<Integer> matchList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String searchQueryStatement = "SELECT DISTINCT swipee FROM matches WHERE swiper=?";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(searchQueryStatement);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                matchList.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return matchList;
    }

//    public static void main(String[] args) {
//        LiftRideDao liftRideDao = new LiftRideDao();
//        liftRideDao.createLiftRide(new LiftRide(100, 200, 300, 400, 500, 600));
//    }
}
