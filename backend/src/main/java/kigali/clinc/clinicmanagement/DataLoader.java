package kigali.clinc.clinicmanagement;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final OfficeRepository officeRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentRepository appointmentRepository;

    public DataLoader(OfficeRepository officeRepository,
                      DoctorRepository doctorRepository,
                      PatientRepository patientRepository,
                      SpecializationRepository specializationRepository,
                      AppointmentRepository appointmentRepository) {
        this.officeRepository = officeRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.specializationRepository = specializationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (appointmentRepository.count() >= 8
                && patientRepository.count() >= 5
                && doctorRepository.count() >= 3
                && officeRepository.count() >= 4) {
            return;
        }

        appointmentRepository.deleteAll();
        doctorRepository.findAll().forEach(d -> {
            d.getSpecializations().clear();
            d.setOffice(null);
            doctorRepository.save(d);
        });
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        officeRepository.deleteAll();
        specializationRepository.deleteAll();

        Specialization cardio = saveSpec("Cardiology");
        Specialization pediatrics = saveSpec("Pediatrics");
        Specialization derm = saveSpec("Dermatology");
        saveSpec("Neurology");

        Office o101 = saveOffice("Room 101", 101);
        Office o102 = saveOffice("Room 102", 102);
        Office o103 = saveOffice("Room 103", 103);
        saveOffice("Room 104", 104); // free, no doctor

        Doctor alice = saveDoctor("Alice", "Mugisha", LocalDate.of(1990, 1, 15), o101);
        Doctor eric = saveDoctor("Eric", "Nkusi", LocalDate.of(1988, 6, 20), o102);
        Doctor jean = saveDoctor("Jean", "Habimana", LocalDate.of(1992, 3, 8), o103);

        alice.addSpecialization(cardio);
        alice.addSpecialization(derm);
        doctorRepository.save(alice);
        eric.addSpecialization(pediatrics); // eric has only one specialization
        doctorRepository.save(eric);

        Patient p1 = savePatient("Aline", "Uwase", LocalDate.of(1995, 4, 12), "SS-1001");
        Patient p2 = savePatient("Claudine", "Uwase", LocalDate.of(1998, 8, 21), "SS-1002");
        Patient p3 = savePatient("Paul", "Habimana", LocalDate.of(2000, 1, 15), "SS-1003");
        Patient p4 = savePatient("Diane", "Mukamana", LocalDate.of(1997, 9, 3), "SS-1004");
        Patient p5 = savePatient("Kevin", "Niyonsenga", LocalDate.of(1999, 11, 11), "SS-1005");

        // p1 has 3 future appointments with alice; alice also has a CANCELLED one on 2026-10-20
        saveAppt(LocalDate.of(2026, 10, 20), "Review A", AppointmentStatus.SCHEDULED, p1, alice);
        saveAppt(LocalDate.of(2026, 10, 22), "Follow-up", AppointmentStatus.CONFIRMED, p1, alice);
        saveAppt(LocalDate.of(2026, 10, 30), "Check-up", AppointmentStatus.SCHEDULED, p1, alice);
        saveAppt(LocalDate.of(2026, 10, 20), "Skin rash", AppointmentStatus.CANCELLED, p2, alice);

        // two COMPLETED appointments (prescriptions only go on these)
        saveAppt(LocalDate.of(2026, 10, 5), "Fever", AppointmentStatus.COMPLETED, p3, eric);
        saveAppt(LocalDate.of(2026, 10, 10), "Vaccination", AppointmentStatus.COMPLETED, p4, eric);

        saveAppt(LocalDate.of(2026, 11, 2), "Headache", AppointmentStatus.SCHEDULED, p5, jean);
        saveAppt(LocalDate.of(2026, 10, 28), "Review B", AppointmentStatus.CONFIRMED, p2, jean);
    }

    private Specialization saveSpec(String name) {
        Specialization s = new Specialization();
        s.setName(name);
        return specializationRepository.save(s);
    }

    private Office saveOffice(String name, int number) {
        Office o = new Office();
        o.setName(name);
        o.setOfficeNumber(number);
        return officeRepository.save(o);
    }

    private Doctor saveDoctor(String first, String last, LocalDate dob, Office office) {
        Doctor d = new Doctor();
        d.setFirstName(first);
        d.setLastName(last);
        d.setDateOfBirth(dob);
        d.setOffice(office);
        return doctorRepository.save(d);
    }

    private Patient savePatient(String first, String last, LocalDate dob, String ssNumber) {
        Patient p = new Patient();
        p.setFirstName(first);
        p.setLastName(last);
        p.setDateOfBirth(dob);
        p.setSsNumber(ssNumber);
        return patientRepository.save(p);
    }

    private void saveAppt(LocalDate date, String reason, AppointmentStatus status,
                          Patient patient, Doctor doctor) {
        Appointment a = new Appointment();
        a.setAppointmentDate(date);
        a.setReason(reason);
        a.setStatus(status);
        a.setPatient(patient);
        a.setDoctor(doctor);
        appointmentRepository.save(a);
    }
}
