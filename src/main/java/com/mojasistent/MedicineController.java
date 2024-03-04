package com.mojasistent;

import com.mojasistent.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MedicineController {
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

    @GetMapping("/saveMedicine")
    public String saveReminder(Model model, HttpSession session, @RequestParam(name = "pp", required = false) boolean pp, @RequestParam(name = "morning", required = false) boolean morning,
                               @RequestParam(name = "afternoon", required = false) boolean afternoon, @RequestParam(name = "evening", required = false) boolean evening,
                               @RequestParam("medicine") String medicine) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        String added = "Lijek dodan";
        if (medicine != null) {
            if (morning && afternoon && evening) {
                Medicine medicineM = new Medicine(medicine, m, patient);
                Medicine medicineN = new Medicine(medicine, n, patient);
                Medicine medicineE = new Medicine(medicine, e, patient);
                medicineRepository.save(medicineM);
                medicineRepository.save(medicineN);
                medicineRepository.save(medicineE);
                model.addAttribute("added", added);
            } else if (afternoon && evening) {
                Medicine medicineN = new Medicine(medicine, n, patient);
                Medicine medicineE = new Medicine(medicine, e, patient);
                medicineRepository.save(medicineN);
                medicineRepository.save(medicineE);
                model.addAttribute("added", added);
            } else if (morning && evening) {
                Medicine medicineM = new Medicine(medicine, m, patient);
                Medicine medicineE = new Medicine(medicine, e, patient);
                medicineRepository.save(medicineM);
                medicineRepository.save(medicineE);
                model.addAttribute("added", added);
            } else if (morning && afternoon) {
                Medicine medicineM = new Medicine(medicine, m, patient);
                Medicine medicineN = new Medicine(medicine, n, patient);
                medicineRepository.save(medicineM);
                medicineRepository.save(medicineN);
                model.addAttribute("added", added);
            } else if (morning) {
                Medicine medicineM = new Medicine(medicine, m, patient);
                medicineRepository.save(medicineM);
                model.addAttribute("added", added);
            } else if (afternoon) {
                Medicine medicineN = new Medicine(medicine, n, patient);
                medicineRepository.save(medicineN);
                model.addAttribute("added", added);
            } else if (evening) {
                Medicine medicineE = new Medicine(medicine, e, patient);
                medicineRepository.save(medicineE);
                model.addAttribute("added", added);
            } else if (pp) {
                Medicine medicinePP = new Medicine(medicine, p, patient);
                medicineRepository.save(medicinePP);
                model.addAttribute("added", added);
            } else {
            }
            model.addAttribute("currPatient", patient);
            return "redirect:/overview_of_medications";
        } else {
            model.addAttribute("currPatient", patient);
            return "add_new_medication.html";
        }
    }

    @GetMapping("/overview_of_medications")
    private String showMedicineList(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        List<Medicine> morningMedicineList = medicineRepository.findByPatientAndTakingTime(patient, m);
        List<Medicine> afternoonMedicineList = medicineRepository.findByPatientAndTakingTime(patient, n);
        List<Medicine> eveningMedicineList = medicineRepository.findByPatientAndTakingTime(patient, e);
        List<Medicine> ppMedicineList = medicineRepository.findByPatientAndTakingTime(patient, p);
        model.addAttribute("currPatient", patient);
        model.addAttribute("morningMedicineList", morningMedicineList);
        model.addAttribute("afternoonMedicineList", afternoonMedicineList);
        model.addAttribute("eveningMedicineList", eveningMedicineList);
        model.addAttribute("ppMedicineList", ppMedicineList);
        return "overview_of_medications.html";
    }


}



