package com.mojasistent;

import com.mojasistent.model.Record;
import com.mojasistent.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FamilyMemberControler {

    @Autowired
    PatientRepository patientRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    MedicineRepository medicineRepository;

    @GetMapping("/toFamilyMemberDashboard")
    public String toFamilyMemberDashboard(Model model, HttpSession session) {
        FamilyMember familymember = (FamilyMember) session.getAttribute("loggedFamilyMember");
        Patient currPatient = familymember.getPatient();
        model.addAttribute("currPatient", currPatient);
        return "fMemberHome.html";
    }

    @GetMapping("/fMemberCRecords")
    public String MemberCRecords(Model model, HttpSession session) {
        FamilyMember familymember = (FamilyMember) session.getAttribute("loggedFamilyMember");
        Patient currPatient = familymember.getPatient();
        List<Record> recordList;
        recordList = recordRepository.findByPatient(currPatient);
        model.addAttribute("currPatient", currPatient);
        model.addAttribute("recordList", recordList);
        return "fMemberCRecord.html";
    }

    @GetMapping("/fMemberCMedicine")
    private String showMedicineList(HttpSession session, Model model) {
        String m = "morning";
        String n = "afternoon";
        String e = "evening";
        String p = "pp";
        FamilyMember familyMember = (FamilyMember) session.getAttribute("loggedFamilyMember");
        Patient patient = familyMember.getPatient();
        List<Medicine> morningMedicineList = medicineRepository.findByPatientAndTakingTime(patient, m);
        List<Medicine> afternoonMedicineList = medicineRepository.findByPatientAndTakingTime(patient, n);
        List<Medicine> eveningMedicineList = medicineRepository.findByPatientAndTakingTime(patient, e);
        List<Medicine> ppMedicineList = medicineRepository.findByPatientAndTakingTime(patient, p);
        model.addAttribute("currPatient", patient);
        model.addAttribute("morningMedicineList", morningMedicineList);
        model.addAttribute("afternoonMedicineList", afternoonMedicineList);
        model.addAttribute("eveningMedicineList", eveningMedicineList);
        model.addAttribute("ppMedicineList", ppMedicineList);
        return "fMemberMedicineList.html";
    }



}
