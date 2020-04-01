package core.dao;

import core.types.Vehicles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehiclesDao extends MySqlDao implements IVehicle {

    @Override
    public Vehicles getVehicles() throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vehicles vehicles;
        try {
            con = this.getConnection();

            String query = "SELECT * FROM vehicles";
            ps = con.prepareStatement(query);

            rs = ps.executeQuery();
            vehicles = new Vehicles();
            while (rs.next()) {
                String registration = rs.getString("registration");
                vehicles.addVehicle(registration);
            }
        } catch (SQLException e) {
            throw new DaoException("getVehicles() " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                throw new DaoException("getVehicles() " + e.getMessage());
            }
        }
        return vehicles;     // u may be null
    }

}
