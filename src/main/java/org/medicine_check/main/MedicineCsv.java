

package org.medicine_check.main;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class MedicineCsv {

    private List<Medicine> medicineList;
    public MedicineCsv(){
       medicineList = new ArrayList<>();
    }
    public void addMedicine(Medicine medicine) {
        if (medicine != null) {
            medicineList.add(medicine);
            System.out.println("Medication added: " + medicine.getName());
        }
    }
    public void loadFromGeminiResponse(String geminiResponse) {
        if (geminiResponse == null || geminiResponse.trim().isEmpty()) {
            System.out.println("Error: Empty or null input");
            return;
        }
        String csvContent = geminiResponse
               .replace("<csv>", "")
               .replace("</csv>", "")
               .trim();
        String[] lines = csvContent.trim().split("\n");
        
        for (String line : lines) {
            String[] data = line.split(",");
            if (data.length >= 3) {
                String name = data[0].trim();
                String dose = data[1].trim();
                String time = data[2].trim();
                String date;
                if (data.length > 3) {
                    date = data[3].trim();
                }
                else {
                    date = "N/A";
                }
                addMedicine(new Medicine(name, dose, time, date));
           }
       }
    }

    public String generateCSV() {
        StringBuilder csv = new StringBuilder("Medicine,Dosage,Time, Date\n"); // CSV header
        int startLine = 1;
        for (Medicine med : medicineList) {
            csv.append(med.getName()).append(",");
            csv.append(med.getDose()).append(",");
            csv.append(med.getTime()).append(",");
            csv.append(med.getDate()).append("\n");
        }
        return csv.toString();

    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(generateCSV());
            System.out.println("Saved to: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String name = data[0].trim();
                    String dose = data[1].trim();
                    String time = data[2].trim();
                    String date = (data.length > 3) ? data[3].trim() : "N/A";
                    medicineList.add(new Medicine(name, dose, time, date));
                }
            }
            System.out.println("Loaded from the file: " + filename);
        } catch (IOException e) {
            System.out.println("Error loading the file: " + e.getMessage());
        }
    }
}
    

