package com.mojasistent;

import com.mojasistent.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ReminderController {
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    ReminderRecordRepository reminderRecordRepository;
    @Autowired
    MedicineRepository medicineRepository;
    String m = "morning";
    String n = "afternoon";
    String e = "evening";
    String p = "pp";


    @PostMapping("/spremiReminder")
    public void spremiReminder(@RequestBody Map<String, String> jsonData) {
        String token = jsonData.get("token");
        Patient patient = patientRepository.findByToken(token);
        List<Medicine> medicineList;
        List<String> medicineListString = new ArrayList<>();
        String m = "morning";
        String n = "afternoon";
        String e = "evening";
        LocalTime podne = LocalTime.of(11, 0, 0);
        LocalTime nav = LocalTime.of(16, 0, 0);
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime noW = LocalDateTime.now();
        if (now.isAfter(nav)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, e);
            for (Medicine medicine : medicineList) {
                medicineListString.add(medicine.getMedicineName());
            }
        } else if (now.isAfter(podne)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, n);
            for (Medicine medicine : medicineList) {
                medicineListString.add(medicine.getMedicineName());
            }
        } else {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, m);
            for (Medicine medicine : medicineList) {
                medicineListString.add(medicine.getMedicineName());
            }
        }
        String string = String.join(", ", medicineListString);
        ReminderRecord reminderRecord = new ReminderRecord(string, noW.plusHours(1),"Popio sam", patient);
        reminderRecordRepository.save(reminderRecord);

    }

    @PostMapping("/spremiNoReminder")
    public void spremiNoReminder(@RequestBody Map<String, String> jsonData) {
        String token = jsonData.get("token");
        Patient patient = patientRepository.findByToken(token);
        List<Medicine> medicineList;
        List<String> medicineListString = new ArrayList<>();
        String m = "morning";
        String n = "afternoon";
        String e = "evening";
        LocalTime podne = LocalTime.of(11, 0, 0);
        LocalTime nav = LocalTime.of(16, 0, 0);
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime noW = LocalDateTime.now();
        if (now.isAfter(nav)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, e);
            for (Medicine medicine : medicineList) {
                medicineListString.add(medicine.getMedicineName());
            }
        } else if (now.isAfter(podne)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, n);
            for (Medicine medicine : medicineList) {
                medicineListString.add(medicine.getMedicineName());
            }
        } else {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, m);
            for (Medicine medicine : medicineList) {
                medicineListString.add(medicine.getMedicineName());
            }
        }
        String string = String.join(", ", medicineListString);
        ReminderRecord reminderRecord = new ReminderRecord(string, noW.plusHours(1), "Nisam popio", patient);
        reminderRecordRepository.save(reminderRecord);
    }


}
