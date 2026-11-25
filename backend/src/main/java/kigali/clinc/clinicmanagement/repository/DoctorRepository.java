package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // B1 JPQL
    @Query("SELECT d FROM Doctor d JOIN d.specializations s "
            + "WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findBySpecializationName(@Param("name") String name);

    // B2 JPQL
    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName ASC")
    List<Doctor> findDoctorsWithoutOffice();

    // Quiz 2 A4 DERIVED: is any doctor using this office?
    boolean existsByOffice_Id(Long officeId);

    // Quiz 2 D3 JPQL: first name or last name contains the text, ignore case
    @Query("SELECT d FROM Doctor d "
            + "WHERE LOWER(d.firstName) LIKE LOWER(:name) "
            + "OR LOWER(d.lastName) LIKE LOWER(:name) "
            + "ORDER BY d.lastName")
    List<Doctor> searchByName(@Param("name") String name);

    // Bonus JPQL: COMPLETED appointments per doctor in a date range, busiest first
    @Query("SELECT d.firstName, d.lastName, COUNT(a) "
            + "FROM Appointment a JOIN a.doctor d "
            + "WHERE a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED "
            + "AND a.appointmentDate BETWEEN :start AND :end "
            + "GROUP BY d.id, d.firstName, d.lastName "
            + "ORDER BY COUNT(a) DESC")
    List<Object[]> findWorkload(@Param("start") LocalDate start,
                                @Param("end") LocalDate end);
}
