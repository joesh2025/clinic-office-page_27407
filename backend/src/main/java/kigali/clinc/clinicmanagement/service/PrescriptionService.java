package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // C1
    public String savePrescription(Long appointmentId, Prescription prescription) {
        Optional<Appointment> appointment = appointmentRepo.findById(appointmentId);
        if (appointment.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        if (appointment.get().getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }
        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment.get());
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    // C2
    public Object findByAppointment(Long appointmentId) {
        if (!appointmentRepo.existsById(appointmentId)) {
            return "The appointment with that id does not exist";
        }
        return prescriptionRepo.findByAppointment_Id(appointmentId);
    }

    // C3
    public List<Prescription> findByPatient(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumber(ssNumber);
    }

    // C4
    public String updatePrescription(UUID id, Prescription prescription) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);
        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        Prescription dbPrescription = existing.get();
        dbPrescription.setMedicineName(prescription.getMedicineName());
        dbPrescription.setDosage(prescription.getDosage());
        dbPrescription.setDurationDays(prescription.getDurationDays());
        prescriptionRepo.save(dbPrescription);
        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        if (!prescriptionRepo.existsById(id)) {
            return "The prescription with that id does not exist";
        }
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    // Bonus
    public List<Object[]> topMedicines() {
        return prescriptionRepo.countMedicineNames();
    }
}
