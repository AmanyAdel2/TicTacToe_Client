/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyGame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.event.ActionEvent;

public class GameRecordController {

    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9; // تعريف الأزرار

    public void initialize() {
        loadFinalBoardState(); // بدء تحميل الحالة النهائية للوحة
    }

    private void loadFinalBoardState() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader("game_record.txt"))) {
                String line;
                boolean isFinalBoardState = false;
                int rowIndex = 0; // مؤشر لتعقب الصفوف (0, 1, 2)

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Final Board State:")) {
                        isFinalBoardState = true;
                        continue; // تخطي السطر الذي يحتوي على "Final Board State:"
                    }

                    if (isFinalBoardState) {
                        // تحديث واجهة المستخدم بالحالة النهائية
                        String finalLine = line.trim(); // إزالة الفراغات الزائدة
                        int finalRowIndex = rowIndex; // متغير نهائي لاستخدامه داخل Platform.runLater
                        Platform.runLater(() -> updateGameBoard(finalLine, finalRowIndex));
                        rowIndex++; // الانتقال إلى الصف التالي
                        Thread.sleep(3000); // تأخير لمدة 1 ثانية بين كل صف
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start(); // بدء الخيط
    }

    private void updateGameBoard(String row, int rowIndex) {
        // تحديث الأزرار بناءً على الصف الحالي
        char[] cells = row.toCharArray();

        for (int colIndex = 0; colIndex < cells.length; colIndex++) {
            Button buttonToUpdate = getButtonByRowCol(rowIndex, colIndex); // الحصول على الزر المناسب
            if (buttonToUpdate != null) {
                buttonToUpdate.setText(String.valueOf(cells[colIndex])); // تحديث نص الزر
                buttonToUpdate.setStyle("-fx-text-fill: " + (cells[colIndex] == 'X' ? "red" : "blue") + "; -fx-font-size: 45; -fx-font-weight: bold;");
            }
        }
    }

    private Button getButtonByRowCol(int row, int col) {
        // تحديد الزر بناءً على الصف والعمود
        switch (row) {
            case 0:
                return (col == 0) ? p1 : (col == 1) ? p2 : p3;
            case 1:
                return (col == 0) ? p4 : (col == 1) ? p5 : p6;
            case 2:
                return (col == 0) ? p7 : (col == 1) ? p8 : p9;
            default:
                return null;
        }
    }

    @FXML
    private void openRecord(ActionEvent event) {
        // يمكنك إضافة أي كود إضافي هنا إذا لزم الأمر
    }

    @FXML
    private void backButton(ActionEvent event) throws IOException {
        // يمكنك إضافة أي كود إضافي هنا إذا لزم الأمر
    }
}

/*
package easyGame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.event.ActionEvent;

public class GameRecordController {

    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9; // تعريف الأزرار

    public void initialize() {
        loadGameMoves(); // بدء تحميل الحركات عند تهيئة الواجهة
    }

    private void loadGameMoves() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader("game_record.txt"))) {
                String line;
                
                while ((line = reader.readLine()) != null) {
                    // تحقق من أن السطر يتوافق مع التنسيق "X 1" أو "O 2"
                    if (line.matches("^[XO] [1-9]$")) {
                        String finalLine = line; // متغير نهائي
                        Platform.runLater(() -> updateGameBoard(finalLine)); // تحديث واجهة المستخدم
                        Thread.sleep(1000); // تأخير لمدة 1 ثانية
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start(); // بدء الخيط
    }

    private void updateGameBoard(String move) {
        String[] parts = move.split(" ");
        
        String player = parts[0]; // 'X' أو 'O'
        int position = Integer.parseInt(parts[1]) - 1; // تحويل إلى فهرس (0-8)

        Button buttonToUpdate = null;

        // تحديد الزر بناءً على الرقم
        switch (position) {
            case 0: buttonToUpdate = p1; break;
            case 1: buttonToUpdate = p2; break;
            case 2: buttonToUpdate = p3; break;
            case 3: buttonToUpdate = p4; break;
            case 4: buttonToUpdate = p5; break;
            case 5: buttonToUpdate = p6; break;
            case 6: buttonToUpdate = p7; break;
            case 7: buttonToUpdate = p8; break;
            case 8: buttonToUpdate = p9; break;
        }

        // تحديث الزر إذا تم تحديده
        if (buttonToUpdate != null) {
            buttonToUpdate.setText(player);
            buttonToUpdate.setStyle("-fx-text-fill: " + (player.equals("X") ? "red" : "blue") + "; -fx-font-size: 45; -fx-font-weight: bold;");
        }
    }
    @FXML
private void openRecord(ActionEvent event) {
    
}
 @FXML
    private void backButton(ActionEvent event) throws IOException {
      
}
}
*/