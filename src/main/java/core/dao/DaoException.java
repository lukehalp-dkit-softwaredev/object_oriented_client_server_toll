package core.dao;

import java.sql.SQLException;

/** A 'homemade' Exception to report exceptions
 *  arising in the the Data Access Layer.
 */
public class DaoException extends SQLException 
{
    public DaoException() {
    }

    public DaoException(String aMessage) 
    {
        super(aMessage);
    }
}