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
public class CoordinatorController {

    @Autowired
    NurseRepository nurseRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    RecordRepository recordRepository;

    @Autowired
    CoordinatorRepository coordinatorRepository;

    @Autowired
    FamilyMemberRepository familyMemberRepository;


    @Autowired
    MedicineRepository medicineRepository;
    @Autowired
    private ReminderRecordRepository reminderRecordRepository;


    @GetMapping("/toCoordinator")
    public String toCoordinator(Model model, HttpSession session) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        List<Nurse> nurseList = nurseRepository.findByCoordinator(coordinator);
        model.addAttribute("coordinator", coordinator);
        model.addAttribute("nurseList", nurseList);
        return "coordinatorDashboard.html";
    }

    @GetMapping("/showPatientList")
    public String showPatientList(Model model, HttpSession session) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("coordinator", coordinator);
        model.addAttribute("patientList", patientList);
        return "doctor-dashboard.html";
    }

    @GetMapping("/newNurse")
    public String newNurse(HttpSession session, Model model) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        model.addAttribute("coordinator", coordinator);
        return "addNewNurse.html";
    }

    @GetMapping("/saveNewNurse")
    public String saveNewNurse(@RequestParam("name") String name, @RequestParam("lastName") String lastName, @RequestParam("email") String email,
                               @RequestParam("password") String password, HttpSession session, Model model) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        boolean containsDigit = false;
        boolean containsLowercase = false;
        boolean containsUpperCase = false;
        boolean nameVerification = false;
        boolean lastNameVerification = false;
        boolean nameAlph = false;
        boolean lastnameAlph = false;
        boolean emailExists = false;
        boolean fEmailExists = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                containsDigit = true;
                break;
            }
        }
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                containsLowercase = true;
                break;
            }
        }
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                containsUpperCase = true;
                break;
            }
        }
        for (char c : name.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                nameAlph = true;
                break;
            }
        }
        for (char c : lastName.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                lastnameAlph = true;
                break;
            }
        }
        if (Character.isUpperCase(name.charAt(0))) {
            nameVerification = true;
        }
        if (Character.isUpperCase(lastName.charAt(0))) {
            lastNameVerification = true;
        }
        // Provjera koristi li neko upisani email
        Patient nPatient = patientRepository.findByEmail(email);
        FamilyMember nFamilyMember = familyMemberRepository.findByEmail(email);
        Nurse nNurse = nurseRepository.findByEmail(email);
        Coordinator nCoordinator = coordinatorRepository.findByEmail(email);

        if (nPatient == null && nFamilyMember == null && nNurse == null && nCoordinator == null) {
            emailExists = true;
        }
        if (password.length() >= 8 && containsDigit && containsUpperCase && containsLowercase && nameVerification && lastNameVerification && nameAlph
                && lastnameAlph && emailExists) {
            Nurse nurse = new Nurse(name, lastName, email, password, coordinator);
            nurseRepository.save(nurse);
            model.addAttribute("warningMessage", "Medicinska sestra/tehničar " + nurse.getName() + " dodan");
            return "addNewNurse.html";
        } else {
            if (!containsDigit) {
                model.addAttribute("warningPassMessage", "Lozinka treba sadržavati minimalno 8 znakova, jedno veliko slovo, jedno malo slovo i minimalno jedan broj!");
                return "addNewNurse.html";
            }
            if (!containsLowercase) {
                model.addAttribute("warningPassMessage", "Lozinka treba sadržavati minimalno 8 znakova, jedno veliko slovo, jedno malo slovo i minimalno jedan broj!");
                return "addNewNurse.html";
            }
            if (!containsUpperCase) {
                model.addAttribute("warningPassMessage", "Lozinka treba sadržavati minimalno 8 znakova, jedno veliko slovo, jedno malo slovo i minimalno jedan broj!");
                return "addNewNurse.html";
            }
            if (password.length() < 8) {
                model.addAttribute("warningPassMessage", "Lozinka treba sadržavati minimalno 8 znakova, jedno veliko slovo, jedno malo slovo i minimalno jedan broj!");
                return "addNewNurse.html";
            }
            if (!nameVerification) {
                model.addAttribute("warningNameMessage", "Ime treba sadržavati veliko početno slovo!");
                return "addNewNurse.html";
            }
            if (!lastNameVerification) {
                model.addAttribute("warningLastMessage", "Prezime treba sadržavati veliko početno slovo!");
                return "addNewNurse.html";
            }
            if (!nameAlph) {
                model.addAttribute("warningNameMessage", "Ime treba sadržavati samo slova!");
                return "addNewNurse.html";
            }
            if (!lastnameAlph) {
                model.addAttribute("warningNameMessage", "Ime treba sadržavati samo slova!");
                return "addNewNursen.html";
            }
            if (!emailExists) {
                model.addAttribute("warningMailMessage", "Email je već zauzet, koristi ga neki drugi korisnik!");
                return "addNewNurse.html";
            } else {
                model.addAttribute("warningMessage", "Došlo je do pogreške! Pokušajte ponovno! ");
                return "addNewNurse.html";
            }
        }
    }

    @GetMapping("/coordinatorCPatientsRecords")
    public String doctorCPatientsRecords(HttpSession session, Model model) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
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
        model.addAttribute("newRecordList", newRecordList);
        model.addAttribute("coordinator", coordinator);

        return "coordinatorCPatients.html";
    }

    @GetMapping("/toAdministration")
    public String toAdministration(HttpSession session, Model model) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("patientList", patientList);
        model.addAttribute("coordinator", coordinator);
        return "patientsAdministration.html";
    }


    @GetMapping("/showMLC")
    public String showML(HttpSession session, Model model, @RequestParam("id") long id) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("patientList", patientList);
        model.addAttribute("coordinator", coordinator);
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

    @GetMapping("/showPlistForMlC")
    public String showML(HttpSession session, Model model) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
        model.addAttribute("patientList", patientList);
        model.addAttribute("coordinator", coordinator);
        return "cooCPatientsForMLC.html";
    }

    @GetMapping("/showPTMlist")
    public String showPTMlist(HttpSession session, Model model) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
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
        model.addAttribute("coordinator", coordinator);
        return "nurse&cooCReminderRecord.html";
    }

    @GetMapping("/cMedicineP")
    public String cMedicineP(HttpSession session, Model model) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
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
        model.addAttribute("coordinator", coordinator);
        return "cPWReminderrecord.html";
    }

    @GetMapping("/cMedicine")
    public String cMedicine(HttpSession session, Model model, @RequestParam("id") long id) {
        Coordinator coordinator = (Coordinator) session.getAttribute("loggedCoordinator");
        Optional<Patient> patient = patientRepository.findById(id);
        List<ReminderRecord> reminderRecordList = (List<ReminderRecord>) reminderRecordRepository.findByPatient(patient);
        model.addAttribute("currPatient", patient);
        model.addAttribute("reminderRecordList", reminderRecordList);
        model.addAttribute("coordinator", coordinator);
        return "cReminderRecords.html";
    }

    @GetMapping("/deleteNurse")
    public String deleteNurse(Model model, HttpSession session, @RequestParam("id") long id) {
        nurseRepository.deleteById(id);


        return "redirect:/toCoordinator";
    }

    @GetMapping("/deletePatienten")
    public String deletePatiente(Model model, HttpSession session, @RequestParam("id") long id) {
       Optional<Patient> patient = patientRepository.findById(id);
        recordRepository.deleteByPatient(patient);
        reminderRecordRepository.deleteByPatient(patient);
        medicineRepository.deleteByPatient(patient);
        familyMemberRepository.deleteByPatient(patient);
        patientRepository.deleteById(id);
        return "redirect:/toAdministration";
    }



}

