package controller;

import com.jfoenix.controls.JFXTextField;
import db.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Student;
import view.tm.StudentTm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;

public class StudentFormController {
    public JFXTextField txtId;
    public JFXTextField txtName;
    public JFXTextField txtEmail;
    public JFXTextField txtContact;
    public JFXTextField txtAddress;
    public JFXTextField txtNic;
    public TableView<StudentTm> tblStudent;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colEmail;
    public TableColumn colContact;
    public TableColumn colAddress;
    public TableColumn colNIC;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colNIC.setCellValueFactory(new PropertyValueFactory<>("nic"));

        loadAllStudents();

        tblStudent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtId.setText(newValue.getId());
            txtName.setText(newValue.getName());
            txtEmail.setText(newValue.getEmail());
            txtContact.setText(newValue.getContact());
            txtAddress.setText(newValue.getAddress());
            txtNic.setText(newValue.getNic());
        });
    }

    private void loadAllStudents() {
        ArrayList<Student> students = new ArrayList<>();
        ObservableList<StudentTm> obList = FXCollections.observableArrayList();
        try {
            ResultSet set = DbConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Student").executeQuery();
            while (set.next()) {
                students.add(new Student(
                        set.getString(1),
                        set.getString(2),
                        set.getString(3),
                        set.getString(4),
                        set.getString(5),
                        set.getString(6)
                ));

                for (Student tm : students) {
                    obList.add(new StudentTm(tm.getId(), tm.getName(), tm.getEmail(), tm.getContact(), tm.getAddress(), tm.getNic()));
                }
                tblStudent.setItems(obList);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtIdOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Student WHERE student_id=?");
        stm.setString(1, txtId.getText());
        ResultSet set = stm.executeQuery();
        if (set.next()) {
            txtName.setText(set.getString(2));
            txtEmail.setText(set.getString(3));
            txtContact.setText(set.getString(4));
            txtAddress.setText(set.getString(5));
            txtNic.setText(set.getString(6));
        } else {
            new Alert(Alert.AlertType.WARNING, "Empty Result").show();
        }
    }

    public void saveStudentOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Student s1 = new Student(
                txtId.getText(), txtName.getText(), txtEmail.getText(), txtContact.getText(), txtAddress.getText(), txtNic.getText()
        );
        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement("INSERT INTO Student VALUES (?,?,?,?,?,?)");
        stm.setString(1, s1.getId());
        stm.setString(2, s1.getName());
        stm.setString(3, s1.getEmail());
        stm.setString(4, s1.getContact());
        stm.setString(5, s1.getAddress());
        stm.setString(6, s1.getNic());
        if (stm.executeUpdate() > 0) {
            new Alert(Alert.AlertType.CONFIRMATION, "Saved..!").show();
        } else {
            new Alert(Alert.AlertType.WARNING, "Try Again").show();
        }
    }

    public void updateStudentOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Student s1 = new Student(
                txtId.getText(), txtName.getText(), txtEmail.getText(), txtContact.getText(), txtAddress.getText(), txtNic.getText()
        );
        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement("UPDATE Student SET student_name=?, email=?, contact=?, address=?, nic=? WHERE student_id=?");
        stm.setString(1, s1.getName());
        stm.setString(2, s1.getEmail());
        stm.setString(3, s1.getContact());
        stm.setString(4, s1.getAddress());
        stm.setString(5, s1.getNic());
        stm.setString(6, s1.getId());

        if (stm.executeUpdate() > 0) {
            new Alert(Alert.AlertType.CONFIRMATION, "Updated Student.").show();
        } else {
            new Alert(Alert.AlertType.WARNING, "Try Again").show();
        }
    }

    public void deleteStudentOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement("DELETE FROM Student WHERE student_id=?");
        stm.setString(1, txtId.getText());
        if (stm.executeUpdate() > 0) {
            new Alert(Alert.AlertType.CONFIRMATION, "Deleted Student.").show();
        } else {
            new Alert(Alert.AlertType.WARNING, "Try Again").show();
        }

    }
}
