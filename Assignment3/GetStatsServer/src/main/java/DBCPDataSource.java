import org.apache.commons.dbcp2.BasicDataSource;

public class DBCPDataSource {
    private static BasicDataSource dataSource;
//    private static final String HOST_NAME = "localhost";
    private static final String HOST_NAME = "cs6650.c6xouuveaaae.us-east-1.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "twinder_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";

    static {
        dataSource = new BasicDataSource();
        try {
             Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        dataSource.setUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(10);
        dataSource.setMaxTotal(60);

    }
    public static BasicDataSource getDataSource() {
        return dataSource;
    }

}