package com.mojasistent;

import com.mojasistent.model.Patient;
import com.mojasistent.model.PatientRepository;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter@Setter
public class TokenController {
@Autowired
    PatientRepository patientRepository;
    @PostMapping("/spremiToken")
    public String spremiToken(@RequestBody TokenRequest tokenRequest, HttpSession session  ) {
        String receivedToken = tokenRequest.getToken();
        Patient currPatient = (Patient) session.getAttribute("loggedPatient");
        currPatient.setToken(receivedToken);
        patientRepository.updateTokenById(receivedToken, currPatient.getId());
        return "Token primljen i spremljen.";
    }
}
