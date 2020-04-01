/** MySqlDao - 
 * - implements functionality that is common to all MySQL DAOs
 * - i.e. getConection() and freeConnection()
 * All MySQL DAOs will extend (inherit from) this class in order to 
 * gain the connection functionality, thus avoiding inclusion 
 * of this code in every DAO.
 * 
 */
package core.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDao 
{
    public Connection getConnection() throws DaoException 
    {

        String url = "jdbc:mysql://localhost:3306/toll";
        String username = "D00219060";
        String password = "abcdefg";
        Connection con;
        
        try 
        {
            con = DriverManager.getConnection(url, username, password);
        }
        catch (SQLException ex2) 
        {
            throw new DaoException(String.format("Could not connect to DB: %s", ex2.getMessage()));
        }
        return con;
    }

    public void freeConnection(Connection con) throws DaoException
    {
        try 
        {
            if (con != null) 
            {
                con.close();
            }
        } 
        catch (SQLException e) 
        {
            throw new DaoException(String.format("Could not free connection to DB: %s", e.getMessage()));
        }
    }   
}