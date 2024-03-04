package com.mojasistent;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import com.mojasistent.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@EnableScheduling
@Component
public class FCMNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(FCMNotificationController.class);
    private static final String[] SCOPES = {"https://www.googleapis.com/auth/firebase.messaging"};

    @Autowired
    ReminderRecordRepository reminderRecordRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    MedicineRepository medicineRepository;

    @Scheduled(cron = "0 */1 * * * ?")
    private void sendFCMNotification() {
        List<Patient> patientList = (List<Patient>) patientRepository.findAll();
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        for (Patient patient : patientList) {
            if (shouldSendNotification(patient, now)) {
                try {
                    sendNotificationToPatient(patient, now);
                } catch (Exception e) {
                    logger.error("Error sending notification to patient ID: " + patient.getId(), e);
                }
            }
        }
    }

    private boolean shouldSendNotification(Patient patient, LocalTime now) {
        boolean tokenExists = patient.getToken() != null;
        boolean timeMatches = doesTimeMatch(patient, now);
        List<Medicine> medicineList;
        String m = "morning";
        String n = "afternoon";
        String e = "evening";
        LocalTime podne = LocalTime.of(11, 0, 0);
        LocalTime nav = LocalTime.of(16, 0, 0);
        if (now.isAfter(nav)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, e);
        } else if (now.isAfter(podne)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, n);
        } else {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, m);
        }
        boolean medicineListNotEmpty = medicineList != null;

        return tokenExists && timeMatches && medicineListNotEmpty;
    }

    private boolean doesTimeMatch(Patient patient, LocalTime now) {
        return (patient.getMorningReminderTime() != null && patient.getMorningReminderTime().truncatedTo(ChronoUnit.MINUTES).equals(now)) || (patient.getNoonReminderTime() != null && patient.getNoonReminderTime().truncatedTo(ChronoUnit.MINUTES).equals(now)) || (patient.getEveningReminderTime() != null && patient.getEveningReminderTime().truncatedTo(ChronoUnit.MINUTES).equals(now));
    }

    private void sendNotificationToPatient(Patient patient, LocalTime now) throws IOException, FirebaseMessagingException {
        URL url = new URL("https://fcm.googleapis.com/v1/projects/mojasistent-9b039/messages:send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        conn.setRequestProperty("Content-Type", "application/json; UTF-8");
        conn.setDoOutput(true);
        String notificationData = constructNotificationData(patient, now);

        String token = patient.getToken();
        String medicineListString = getMedicineListForCurrentTime(patient, now);

        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle("Jeste li popili lijekove?")
                            .setBody("Lijekovi: " + medicineListString)
                            .build())
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(new WebpushNotification(
                                    "MojAsistentReminder",
                                    "Podsjetnik: Jeste li popili lijekove?\nLijekovi: " + medicineListString,
                                    ""))
                            .putHeader("Urgency", "high")
                            .build())
                    .setToken(token)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            logger.error("Ne radi prva metoda");
        }

    }


    private String constructNotificationData(Patient patient, LocalTime now) {
        String token = patient.getToken();
        String medicineListString = getMedicineListForCurrentTime(patient, now);

        return String.format("""
                {
                    "message": {
                        "token": "%s",
                        "notification": {
                            "title": "Jeste li popili lijekove?",
                            "body": "Lijekovi: %s"
                        },
                        "data": {
                            "customKey": "customValue",
                            "actions": {
                                "1": {"title": "Popio sam", "action": "taken"},
                                "2": {"title": "Nisam popio", "action": "not_taken"},
                                "3": {"title": "Odgoda", "action": "delayed"}
                            }
                        }
                    }
                }""", token, medicineListString);
    }
    /*{
     Primjer iz dokumentacije
   "message":{
      "token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
      "notification":{
        "body":"This is an FCM notification message!",
        "title":"FCM Message"
      }
   }
}*/

    private String getMedicineListForCurrentTime(Patient patient, LocalTime now) {
        List<Medicine> medicineList;
        List<String> medicineStringList = new ArrayList<>();
        String medicineListString = null;
        String m = "morning";
        String n = "afternoon";
        String e = "evening";
        LocalTime podne = LocalTime.of(11, 0, 0);
        LocalTime nav = LocalTime.of(16, 0, 0);
        if (now.isAfter(nav)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, e);
        } else if (now.isAfter(podne)) {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, n);
        } else {
            medicineList = medicineRepository.findByPatientAndTakingTime(patient, m);
        }

        if (medicineList != null) {
            for (Medicine medicine : medicineList) {
                medicineStringList.add(medicine.getMedicineName());
            }
        }
        medicineListString = String.join(", ", medicineStringList);
        return medicineListString;
    }


    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("mojasistent-9b039-firebase-adminsdk-aiild-0c3a10f11b.json"))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }


}
