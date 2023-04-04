import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetStatsDao {
    private static BasicDataSource dataSource;

    public GetStatsDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public List<Integer> getStats(int userId) {
        List<Integer> statsList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String searchQueryStatement = "SELECT NumberOfLike, NumberOfDislike FROM stats WHERE userId=?";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(searchQueryStatement);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                statsList.add(resultSet.getInt(1));
                statsList.add(resultSet.getInt(2));
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
        return statsList;
    }

}
