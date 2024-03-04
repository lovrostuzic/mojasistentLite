package com.mojasistent;

import com.mojasistent.model.*;
import com.mojasistent.model.Record;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class DoctorController {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    RecordRepository recordRepository;

    @Autowired
    NurseRepository nurseRepository;

    @Autowired
    CoordinatorRepository coordinatorRepository;

    @Autowired
    ReminderRecordRepository reminderRecordRepository;


    Patient currPatient;
    @Autowired
    private MedicineRepository medicineRepository;


    //Ovdje pretpostavljam da ce sve sestre/tehničari imati ovlasti vidjeti sve pacijente i njihove zapise

    @GetMapping("/doctor-dashboard")
    public String showDashboard(Model model, Long id) {
        currPatient = patientRepository.findById(id).get();
        Coordinator coordinator;
        coordinator = currPatient.getCoordinator();
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("currPatient", currPatient);
        model.addAttribute("patientList", patientList);
        return "patientsAdministration.html";
    }

    @GetMapping("/newPatient")
    public String addPatient(Model model, Long id) {
        currPatient = patientRepository.findById(id).get();
        model.addAttribute("currPatient", currPatient);
        return "add_new_patient.html";
    }


    @GetMapping("/deletePatient")
    public String deletePatient(Long id) {
        Patient currPatient = patientRepository.findById(id).get();
        patientRepository.delete(currPatient);
        return "redirect:/doctor-dashboard";
    }


    @GetMapping("/savePatient")
    public String savePatient(String firstName, String lastName, String email, String password, String address, String phoneNumber, Long id, Date date) {
        Nurse nurse = nurseRepository.findById(1);
        patientRepository.save(new Patient(firstName, lastName, email, password, phoneNumber, date, coordinatorRepository.findById(1L)));
        return "redirect:/doctor-dashboard?id=" + currPatient.getId();
    }


    @GetMapping("/showRecords")
    public String showPatientRecords(Model model, Long id, HttpSession session) {
        Patient currSelectedPatient = patientRepository.findById(id).get();
        List<Record> recordList = recordRepository.findByPatient(currSelectedPatient);
        model.addAttribute("currPatient", currPatient);
        model.addAttribute("currPatient", currSelectedPatient);
        model.addAttribute("recordList", recordList);
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        model.addAttribute("nurse", nurse);
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        model.addAttribute("coordinator", coordinator);
        return "patient_records.html";
    }

    @GetMapping("/doctorCPatientsRecords")
    public String doctorCPatientsRecords(HttpSession session, Model model) {
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        Coordinator coordinator = nurse.getCoordinator();
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        List<Record> newRecordList = new ArrayList<>();
        for (Patient patient : patientList) {
            Record newRecord = recordRepository.findByPatientAndDate(patient);
            if (newRecord == null) {
            } else {
                newRecordList.add(newRecord);
            }
        }
        Collections.sort(newRecordList, Comparator.comparing(Record::getPainLevel, Comparator.reverseOrder()));
        model.addAttribute("nurse", nurse);
        model.addAttribute("newRecordList", newRecordList);

        return "doctorCPatientsRecords.html";
    }

    @GetMapping("/toPAdministration")
    public String toAdministration(HttpSession session, Model model) {
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        Coordinator coordinator = nurse.getCoordinator();
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("patientList", patientList);
        model.addAttribute("nurse", nurse);
        return "patientsAdministration.html";
    }


    @GetMapping("/showML")
    public String showML(HttpSession session, Model model, @RequestParam("id") long id) {
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        Coordinator coordinator = nurse.getCoordinator();
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("patientList", patientList);
        model.addAttribute("nurse", nurse);
        Optional<Patient> patient = patientRepository.findById(id);
        String m = "morning";
        String n = "afternoon";
        String e = "evening";
        String p = "pp";
        List<Medicine> morningMedicineList = medicineRepository.findByPatientAndTakingTime(patient, m);
        List<Medicine> afternoonMedicineList = medicineRepository.findByPatientAndTakingTime(patient, n);
        List<Medicine> eveningMedicineList = medicineRepository.findByPatientAndTakingTime(patient, e);
        List<Medicine> ppMedicineList = medicineRepository.findByPatientAndTakingTime(patient, p);
        model.addAttribute("currPatient", patient);
        model.addAttribute("morningMedicineList", morningMedicineList);
        model.addAttribute("afternoonMedicineList", afternoonMedicineList);
        model.addAttribute("eveningMedicineList", eveningMedicineList);
        model.addAttribute("ppMedicineList", ppMedicineList);
        return "nurse&cooCMedicineList.html";
    }

    @GetMapping("/showPlistForMl")
    public String showML(HttpSession session, Model model) {
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        Coordinator coordinator = nurse.getCoordinator();
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("patientList", patientList);
        model.addAttribute("nurse", nurse);
        return "nurse&cooCPatientforML.html";
    }


    @GetMapping("/showPTMlistN")
    public String showPTMlist(HttpSession session, Model model) {
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        Coordinator coordinator = nurse.getCoordinator();
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        List<ReminderRecord> newRecordList = new ArrayList<>();
        for (Patient patient : patientList) {
            ReminderRecord reminderRecord = reminderRecordRepository.findByPatientAndDate(patient);
            if (reminderRecord == null) {
            } else {
                newRecordList.add(reminderRecord);
            }
        }
        model.addAttribute("newRecordList", newRecordList);
        model.addAttribute("nurse", nurse);
        return "nurse&cooCReminderRecordN.html";
    }

    @GetMapping("/cMedicinePN")
    public String cMedicineP(HttpSession session, Model model) {
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        Coordinator coordinator = nurse.getCoordinator();
        List<Patient> patientList = new ArrayList<>();
        List<ReminderRecord> reminderRecordList = (List<ReminderRecord>) reminderRecordRepository.findAll();
        for (ReminderRecord reminderRecord : reminderRecordList) {
            Patient patient = reminderRecord.getPatient();
            if (patientList.contains(patient)) {
            } else {
                patientList.add(patient);
            }
        }
        model.addAttribute("patientList", patientList);
        model.addAttribute("nurse", nurse);
        return "cPWReminderrecordN.html";
    }
/*
    @GetMapping("/cMedicine")
    public String cMedicine(HttpSession session, Model model, @RequestParam("id") long id) {
        Nurse nurse = (Nurse) session.getAttribute("loggedNurse");
        Coordinator coordinator = nurse.getCoordinator();
        Optional<Patient> patient = patientRepository.findById(id);
        List<ReminderRecord> reminderRecordList = (List<ReminderRecord>) reminderRecordRepository.findByPatient(patient);
        model.addAttribute("currPatient", patient);
        model.addAttribute("reminderRecordList", reminderRecordList);
        model.addAttribute("nurse", nurse);
        return "cReminderRecords.html";
    }
*/
}
