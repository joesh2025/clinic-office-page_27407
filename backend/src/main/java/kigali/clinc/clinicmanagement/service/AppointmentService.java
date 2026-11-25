package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    public String saveAppointment(Appointment appointment) {
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            return "Patient is required";
        }
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return "Doctor is required";
        }
        Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());
        if (patient.isEmpty()) {
            return "Patient not found";
        }
        Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }

        // B1: no booking in the past
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        // A4: no double booking (ignore CANCELLED)
        boolean booked = appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                doctor.get().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);
        if (booked) {
            return "Doctor is already booked on that date";
        }

        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());
        appointment.setStatus(AppointmentStatus.SCHEDULED); // B1: always starts SCHEDULED
        appointmentRepo.save(appointment);
        return "Appointment is saved Successfully";
    }

    public List<Appointment> findAll() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepo.findById(id);
    }

    public String updateAppointment(Long id, Appointment appointment) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "Appointment not found";
        }
        Appointment dbAppointment = existing.get();
        dbAppointment.setAppointmentDate(appointment.getAppointmentDate());
        dbAppointment.setReason(appointment.getReason());
        // B2: status only changes through the status endpoint

        if (appointment.getPatient() != null && appointment.getPatient().getId() != null) {
            Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());
            if (patient.isEmpty()) {
                return "Patient not found";
            }
            dbAppointment.setPatient(patient.get());
        }
        if (appointment.getDoctor() != null && appointment.getDoctor().getId() != null) {
            Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());
            if (doctor.isEmpty()) {
                return "Doctor not found";
            }
            dbAppointment.setDoctor(doctor.get());
        }

        appointmentRepo.save(dbAppointment);
        return "Appointment is updated Successfully";
    }

    public String deleteAppointment(Long id) {
        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found";
        }
        // C4: an appointment with prescriptions must keep them
        if (prescriptionRepo.existsByAppointment_Id(id)) {
            return "That appointment has prescriptions, delete them first";
        }
        appointmentRepo.deleteById(id);
        return "Appointment is deleted Successfully";
    }

    // B2
    public String updateStatus(Long id, AppointmentStatus newStatus) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = existing.get();
        AppointmentStatus current = appointment.getStatus();

        boolean allowed = false;
        if (current == AppointmentStatus.SCHEDULED) {
            allowed = newStatus == AppointmentStatus.CONFIRMED
                    || newStatus == AppointmentStatus.CANCELLED;
        } else if (current == AppointmentStatus.CONFIRMED) {
            allowed = newStatus == AppointmentStatus.COMPLETED
                    || newStatus == AppointmentStatus.CANCELLED;
        }
        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    // B3
    public String reschedule(Long id, LocalDate newDate) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = existing.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return "A COMPLETED appointment cannot be rescheduled";
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return "A CANCELLED appointment cannot be rescheduled";
        }
        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        List<Appointment> sameDay = appointmentRepo.findByDoctor_IdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED);
        for (Appointment other : sameDay) {
            if (!other.getId().equals(appointment.getId())) {
                return "Doctor is already booked on that date";
            }
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }

    // A2
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    // A3
    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    // C1
    public List<Object[]> statsByStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    // C4
    @Transactional
    public String cancelDay(Long doctorId, LocalDate date) {
        int updated = appointmentRepo.cancelAppointmentsForDoctorOnDate(doctorId, date);
        return updated + " appointments cancelled";
    }

    // Bonus page
    public Page<Appointment> findPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    // Bonus delete
    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int deleted = appointmentRepo.deleteByStatusAndAppointmentDateBefore(
                AppointmentStatus.CANCELLED, date);
        return deleted + " appointments deleted";
    }
 public List<Appointment> doctorAgenda(Long doctorId, LocalDate date) {
        return appointmentRepo.findAll().stream()
                .filter(appointment ->
                        appointment.getDoctor() != null
                        && appointment.getDoctor().getId().equals(doctorId)
                        && appointment.getAppointmentDate().equals(date))
                .toList();
    }
 public List<Appointment> upcomingAppointments(Long patientId) {
        return appointmentRepo.findAll().stream()
                .filter(a -> a.getPatient() != null
                        && a.getPatient().getId().equals(patientId))
                .filter(a -> a.getAppointmentDate() != null
                        && !a.getAppointmentDate().isBefore(LocalDate.now()))
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED
                        && a.getStatus() != AppointmentStatus.COMPLETED)
                .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                .toList();
    }

    
    public Map<String, Object> dashboard() {
        List<Appointment> appointments = appointmentRepo.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAppointments", appointments.size());
        stats.put("scheduled", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                .count());
        stats.put("confirmed", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED)
                .count());
        stats.put("completed", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count());
        stats.put("cancelled", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .count());

        return stats;
    }

    // D3 - Search appointments by patient name
    public List<Appointment> searchByPatientName(String name) {
        String search = name.toLowerCase();

        return appointmentRepo.findAll().stream()
                .filter(a -> a.getPatient() != null)
                .filter(a -> {
                    String first = a.getPatient().getFirstName() == null
                            ? "" : a.getPatient().getFirstName();
                    String last = a.getPatient().getLastName() == null
                            ? "" : a.getPatient().getLastName();

                    return first.toLowerCase().contains(search)
                            || last.toLowerCase().contains(search)
                            || (first + " " + last).toLowerCase().contains(search);
                })
                .toList();
    }
}
