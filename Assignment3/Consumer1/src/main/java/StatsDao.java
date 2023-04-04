import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class StatsDao {
    private static BasicDataSource dataSource;

    public StatsDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createStats(List<Payload> batch) {
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
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        String likeinsertQueryStatement = "INSERT INTO stats (userId, NumberOfLike, NumberOfDislike)" +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE NumberOfLike=NumberOfLike+1";
        String dislikeinsertQueryStatement = "INSERT INTO stats (userId, NumberOfLike, NumberOfDislike)" +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE NumberOfDislike=NumberOfDislike+1";
        try {
            conn = dataSource.getConnection();
            preparedStatement1 = conn.prepareStatement(likeinsertQueryStatement);
            preparedStatement2 = conn.prepareStatement(dislikeinsertQueryStatement);
            conn.setAutoCommit(false);
            for (Payload body : batch) {
                int userId = body.getSwiper();
                if (body.getLeftOrRight().equals("right")) {
                    preparedStatement1.setInt(1, userId);
                    preparedStatement1.setInt(2, 1);
                    preparedStatement1.setInt(3, 0);
                    preparedStatement1.addBatch();
                } else {
                    preparedStatement2.setInt(1, userId);
                    preparedStatement2.setInt(2, 0);
                    preparedStatement2.setInt(3, 1);
                    preparedStatement2.addBatch();
                }
            }
            preparedStatement1.executeBatch();
            preparedStatement2.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback the transaction if an exception occurs
            }
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
