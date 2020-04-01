package core.dao;

import core.types.TollEvent;
import core.types.TollEvents;
import core.types.Vehicles;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class TollEventDao extends MySqlDao implements ITollEvent {

    @Override
    public TollEvent findTollEvent(long imageId) throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TollEvent tollEvent = null;
        try {
            con = this.getConnection();

            String query = "SELECT * FROM toll_events WHERE image_id = ?";
            ps = con.prepareStatement(query);
            ps.setLong(1, imageId);

            rs = ps.executeQuery();
            if (rs.next()) {
                String registration = rs.getString("registration");
                long image_id = rs.getLong("image_id");
                Instant timestamp = rs.getTimestamp("timestamp").toInstant();
                tollEvent = new TollEvent(registration, image_id, timestamp);
            }
        } catch (SQLException e) {
            throw new DaoException("findTollEvent() " + e.getMessage());
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
                throw new DaoException("findTollEvent() " + e.getMessage());
            }
        }
        return tollEvent;
    }

    @Override
    public TollEvent findTollEvent(String registration) throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TollEvent tollEvent = null;
        try {
            con = this.getConnection();

            String query = "SELECT * FROM toll_events WHERE registration = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, registration);

            rs = ps.executeQuery();
            if (rs.next()) {
                registration = rs.getString("registration");
                long image_id = rs.getLong("image_id");
                Instant timestamp = rs.getTimestamp("timestamp").toInstant();
                tollEvent = new TollEvent(registration, image_id, timestamp);
            }
        } catch (SQLException e) {
            throw new DaoException("findTollEvent() " + e.getMessage());
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
                throw new DaoException("findTollEvent() " + e.getMessage());
            }
        }
        return tollEvent;
    }

    @Override
    public TollEvents getTollEvents() throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TollEvents tollEvents;
        try {
            con = this.getConnection();

            String query = "SELECT * FROM toll_events";
            ps = con.prepareStatement(query);

            rs = ps.executeQuery();
            Vehicles vehicles = new VehiclesDao().getVehicles();
            tollEvents = new TollEvents(vehicles);
            while (rs.next()) {
                String registration = rs.getString("registration");
                long image_id = rs.getLong("image_id");
                Instant timestamp = rs.getTimestamp("timestamp").toInstant();
                tollEvents.addTollEvent(new TollEvent(registration, image_id, timestamp));
            }
        } catch (SQLException e) {
            throw new DaoException("getTollEvents() " + e.getMessage());
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
                throw new DaoException("getTollEvents() " + e.getMessage());
            }
        }
        return tollEvents;
    }

    public HashMap<String, ArrayList<TollEvent>> getTollEventsMap() throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TollEvents tollEvents;
        try {
            con = this.getConnection();

            String query = "SELECT * FROM toll_events";
            ps = con.prepareStatement(query);

            rs = ps.executeQuery();
            Vehicles vehicles = new VehiclesDao().getVehicles();
            tollEvents = new TollEvents(vehicles);
            while (rs.next()) {
                String registration = rs.getString("registration");
                long image_id = rs.getLong("image_id");
                Instant timestamp = rs.getTimestamp("timestamp").toInstant();
                tollEvents.addTollEvent(new TollEvent(registration, image_id, timestamp));
            }
        } catch (SQLException e) {
            throw new DaoException("getTollEvents() " + e.getMessage());
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
                throw new DaoException("getTollEvents() " + e.getMessage());
            }
        }
        return tollEvents.getEvents();
    }

    @Override
    public TollEvents getTollEventsSince(Instant instant) throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TollEvents tollEvents;
        try {
            con = this.getConnection();

            String query = "SELECT * FROM toll_events WHERE timestamp > ?";
            ps = con.prepareStatement(query);
            ps.setTimestamp(1, Timestamp.from(instant));

            rs = ps.executeQuery();
            Vehicles vehicles = new VehiclesDao().getVehicles();
            tollEvents = new TollEvents(vehicles);
            while (rs.next()) {
                String registration = rs.getString("registration");
                long image_id = rs.getLong("image_id");
                Instant timestamp = rs.getTimestamp("timestamp").toInstant();
                tollEvents.addTollEvent(new TollEvent(registration, image_id, timestamp));
            }
        } catch (SQLException e) {
            throw new DaoException("getTollEvents() " + e.getMessage());
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
                throw new DaoException("getTollEvents() " + e.getMessage());
            }
        }
        return tollEvents;
    }

    @Override
    public TollEvents getTollEventsBetween(Instant from, Instant to) throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TollEvents tollEvents;
        try {
            con = this.getConnection();

            String query = "SELECT * FROM toll_events WHERE timestamp > ? AND timestamp < ?";
            ps = con.prepareStatement(query);
            ps.setTimestamp(1, Timestamp.from(from));
            ps.setTimestamp(2, Timestamp.from(to));

            rs = ps.executeQuery();
            Vehicles vehicles = new VehiclesDao().getVehicles();
            tollEvents = new TollEvents(vehicles);
            while (rs.next()) {
                String registration = rs.getString("registration");
                long image_id = rs.getLong("image_id");
                Instant timestamp = rs.getTimestamp("timestamp").toInstant();
                tollEvents.addTollEvent(new TollEvent(registration, image_id, timestamp));
            }
        } catch (SQLException e) {
            throw new DaoException("getTollEvents() " + e.getMessage());
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
                throw new DaoException("getTollEvents() " + e.getMessage());
            }
        }
        return tollEvents;
    }

    @Override
    public Set<String> getAllRegistrations() throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TreeSet<String> registrations = new TreeSet<>();
        try {
            con = this.getConnection();

            String query = "SELECT * FROM toll_events";
            ps = con.prepareStatement(query);

            rs = ps.executeQuery();
            Vehicles vehicles = new VehiclesDao().getVehicles();
            while (rs.next()) {
                String registration = rs.getString("registration");
                registrations.add(registration);
            }
        } catch (SQLException e) {
            throw new DaoException("getTollEvents() " + e.getMessage());
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
                throw new DaoException("getTollEvents() " + e.getMessage());
            }
        }
        return registrations;
    }

    @Override
    public void updateDatabase(TollEvents tollEvents) throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = this.getConnection();

            for (String reg : tollEvents.getEvents().keySet()) {
                ArrayList<TollEvent> tolls = tollEvents.getEvents().get(reg);
                for (TollEvent toll : tolls) {
                    String query = "INSERT INTO toll_events VALUES (?, ?, ?, ?)";
                    ps = con.prepareStatement(query);
                    ps.setString(1, toll.getRegistration());
                    ps.setLong(2, toll.getImageId());
                    ps.setTimestamp(3, Timestamp.from(toll.getTimestamp()));
                    ps.setString(4, toll.getBoothId());

                    ps.executeUpdate();
                }
            }

            for (TollEvent tollEvent : tollEvents.getInvalidEvents()) {
                String query = "INSERT INTO invalid_toll_events VALUES (?, ?, ?, ?)";
                ps = con.prepareStatement(query);
                ps.setString(1, tollEvent.getRegistration());
                ps.setLong(2, tollEvent.getImageId());
                ps.setTimestamp(3, Timestamp.from(tollEvent.getTimestamp()));
                ps.setString(4, tollEvent.getBoothId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DaoException("updateDatabase() " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                throw new DaoException("updateDatabase() " + e.getMessage());
            }
        }
    }

    public void registerValidTollEvent(TollEvent toll) throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = this.getConnection();

            String query = "INSERT INTO toll_events VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(query);
            ps.setString(1, toll.getRegistration());
            ps.setLong(2, toll.getImageId());
            ps.setTimestamp(3, Timestamp.from(toll.getTimestamp()));
            ps.setString(4, toll.getBoothId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("registerValidTollEvent() " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                throw new DaoException("registerValidTollEvent() " + e.getMessage());
            }
        }
    }

    public void registerInvalidTollEvent(TollEvent toll) throws DaoException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = this.getConnection();

            String query = "INSERT INTO invalid_toll_events VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(query);
            ps.setString(1, toll.getRegistration());
            ps.setLong(2, toll.getImageId());
            ps.setTimestamp(3, Timestamp.from(toll.getTimestamp()));
            ps.setString(4, toll.getBoothId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("registerInvalidTollEvent() " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                throw new DaoException("registerInvalidTollEvent() " + e.getMessage());
            }
        }
    }
}
