package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping(value = "/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // C1
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePrescription(@RequestParam Long appointmentId,
                                              @RequestBody Prescription prescription) {
        String returnedMessage = prescriptionService.savePrescription(appointmentId, prescription);
        if (returnedMessage.equalsIgnoreCase("Prescription saved successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        } else if (returnedMessage.equalsIgnoreCase("Prescriptions are only for COMPLETED appointments")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    // C2
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> findByAppointment(@PathVariable Long appointmentId) {
        Object result = prescriptionService.findByAppointment(appointmentId);
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // C3
    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<?> findByPatient(@PathVariable String ssNumber) {
        List<Prescription> prescriptions = prescriptionService.findByPatient(ssNumber);
        return new ResponseEntity<>(prescriptions, HttpStatus.OK);
    }

    // C4
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePrescription(@PathVariable UUID id,
                                                @RequestBody Prescription prescription) {
        String returnedMessage = prescriptionService.updatePrescription(id, prescription);
        if (returnedMessage.equalsIgnoreCase("Prescription updated successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePrescription(@PathVariable UUID id) {
        String returnedMessage = prescriptionService.deletePrescription(id);
        if (returnedMessage.equalsIgnoreCase("Prescription deleted successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    // Bonus
    @GetMapping("/stats/top-medicines")
    public ResponseEntity<?> topMedicines() {
        return new ResponseEntity<>(prescriptionService.topMedicines(), HttpStatus.OK);
    }
}
