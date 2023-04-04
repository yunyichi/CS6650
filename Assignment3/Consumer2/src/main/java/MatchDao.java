import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MatchDao {
    private static BasicDataSource dataSource;

    public MatchDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createMatch(List<Payload> batch) {
        final int maxRetries = 5;
        int currentRetry = 0;
        while (currentRetry < maxRetries) {
            try {
                processBatch(batch);
                break; // If successful, exit the loop
            } catch (SQLException e) {
                if (e instanceof BatchUpdateException && e.getCause() instanceof MySQLTransactionRollbackException) {
                    System.err.println("Deadlock detected, retrying... (" + (currentRetry + 1) + "/" + maxRetries + ")");
                    currentRetry++;
                } else {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void processBatch(List<Payload> batch) throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO matches (swiper, swipee)" +
                "VALUES (?, ?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            conn.setAutoCommit(false);
            for (Payload body : batch) {
                int swiper = body.getSwiper();
                int swipee = body.getSwipee();
                preparedStatement.setInt(1, swiper);
                preparedStatement.setInt(2, swipee);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
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
    }
}

