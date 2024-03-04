package com.mojasistent;

import com.mojasistent.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {


    @Autowired
    PatientRepository patientRepository;

    @Autowired
    NurseRepository nurseRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;

    @Autowired
    CoordinatorRepository coordinatorRepository;

    @GetMapping("/")
    public String showLogin(Model model, HttpSession session) {
        session.invalidate();
        return "login.html";
    }

    @GetMapping("/login")
    public String processLogin(Model model, @RequestParam("username") String email, String password, HttpServletRequest request) {
        Patient currPatient = patientRepository.findByEmailAndPassword(email, password);
        Nurse currNurse = nurseRepository.findByEmailAndPassword(email, password);
        FamilyMember familyMember = familyMemberRepository.findByEmailAndPassword(email, password);
        Coordinator currCoordinator = coordinatorRepository.findByEmailAndPassword(email, password);
        if (currNurse != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedNurse", currNurse);
            Coordinator coordinator = currNurse.getCoordinator();
            List<Patient> patientList = patientRepository.findByCoordinator(coordinator);
            model.addAttribute("patientList", patientList);
            return "redirect:/doctorCPatientsRecords";
        } else if (currCoordinator != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedCoordinator", currCoordinator);
            return "redirect:/coordinatorCPatientsRecords";
        } else if (currPatient != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedPatient", currPatient);
            return "redirect:/form-01";
        } else if (familyMember != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedFamilyMember", familyMember);
            return "redirect:/toFamilyMemberDashboard";
        } else {
            model.addAttribute("warningMessage", "Pogresni unos korisničkog imena i/ili lozinke!");
            return "login.html";
        }

    }

    @GetMapping("/toregistration")
    public String toRegistration() {
        return "registration.html";
    }
}
