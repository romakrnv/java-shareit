package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Statuses;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerId(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStatus(Long bookerId, Statuses status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.item.id, b.end DESC")
    List<Booking> findLastBookingsByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.item.id, b.start ASC")
    List<Booking> findNextBookingsByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP BETWEEN b.start and b.end")
    Collection<Booking> findAllCurrentBookingByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP > b.end")
    Collection<Booking> findAllPastBookingByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP < b.start")
    Collection<Booking> findAllFutureBookingByBookerId(Long bookerId);


    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1")
    Collection<Booking> findAllByOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1 " +
            "and b.status = ?2")
    Collection<Booking> findAllByOwnerIdAndStatus(Long ownerId, Statuses status);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1 " +
            "and CURRENT_TIMESTAMP BETWEEN b.start and b.end")
    Collection<Booking> findAllCurrentBookingByOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1 " +
            "and CURRENT_TIMESTAMP > b.end")
    Collection<Booking> findAllPastBookingByOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1 " +
            "and CURRENT_TIMESTAMP < b.start")
    Collection<Booking> findAllFutureBookingByOwnerId(Long ownerId);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime localDateTime);

    @Query("select b.start " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and CURRENT_TIMESTAMP < b.start")
    List<LocalDateTime> findNextBookingStartByItemId(Long itemId);

    @Query("select b.end " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and CURRENT_TIMESTAMP > b.end")
    List<LocalDateTime> findLastBookingEndByItemId(Long itemId);
}
