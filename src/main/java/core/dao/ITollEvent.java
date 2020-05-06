package core.dao;

import core.types.Bill;
import core.types.TollEvent;
import core.types.TollEvents;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface ITollEvent {

    TollEvent findTollEvent(long imageId) throws DaoException;
    TollEvent findTollEvent(String registration) throws DaoException;
    TollEvents getTollEvents() throws DaoException;
    TollEvents getTollEventsSince(Instant instant) throws DaoException;
    TollEvents getTollEventsBetween(Instant from, Instant to) throws DaoException;
    Set<String> getAllRegistrations() throws DaoException;
    void updateDatabase(TollEvents tollEvents) throws DaoException;
    List<Bill> getBills() throws DaoException;

}
