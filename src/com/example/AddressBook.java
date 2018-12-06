package com.example;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static java.nio.file.StandardOpenOption.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AddressBook extends Application {
    private static List<Integer> dataLength = new ArrayList<>();
    private static int counter = 0;
    private static LimitedTextField tfName = new LimitedTextField();
    private static LimitedTextField tfStreet = new LimitedTextField();
    private static LimitedTextField tfCity = new LimitedTextField();
    private static LimitedTextField tfState = new LimitedTextField();
    private static LimitedTextField tfZip = new LimitedTextField();

    public static void clear(){
        tfName.setText("");
        tfStreet.setText("");
        tfCity.setText("");
        tfState.setText("");
        tfZip.setText("");
    }

    public static boolean isInputEmpty(){
        return tfName.getText().isEmpty() || tfStreet.getText().isEmpty() || tfCity.getText().isEmpty()
                || tfState.getText().isEmpty() || tfZip.getText().isEmpty();
    }
    public static boolean checkEntry(){
        return tfName.getText().matches(".*[\\\\/;|\"\'].*") || tfStreet.getText().matches(".*[\\\\/;|\"\'].*")
                || tfCity.getText().matches(".*[\\\\/;|\"\'].*") || tfState.getText().matches(".*[\\\\/;|\"\'].*")
                || tfZip.getText().matches(".*[\\\\/;|\"\'].*");
    }

    public static boolean checkState(){
        return tfState.getText().length() != 2 || tfState.getText().matches(".*[0-9].*");
    }

    public static boolean checkZip(){
        return tfZip.getText().length() != 6;
    }

    public static void loadData(){
        try{
            Path path = FileSystems.getDefault().getPath("Address.dat");
            FileChannel fc = FileChannel.open(path, CREATE, READ);
            System.out.println(fc.size());
            if(fc.size() != 0){
                ByteBuffer copy = ByteBuffer.allocate((int)fc.size() - 3); // -3 to remove the last characters (";\r\n")
                int nread;
                do{
                    nread = fc.read(copy);
                } while(nread != -1 && copy.hasRemaining());
                fc.close();
                String s = new String(copy.array());
                String lth[] = s.split(";\\r\\n");
                for (int i = 0; i < lth.length; i++) {
                    dataLength.add(lth[i].length() + 3);
                }
            }
            if(fc != null)
                fc.close();
        } catch(IOException e){
            System.out.println(e);
        }
    }

    public static void loadUserInfo(int position){
        if(dataLength.size() == 0)
            showDialog("Error","Data is empty");
        else{
            try{
                Path path = FileSystems.getDefault().getPath("Address.dat");
                FileChannel fc = FileChannel.open(path, CREATE, READ);

                // position the filechannel to the beginning of the reading user info
                if(position > 0){
                    int tmp = 0;
                    // count the total bytes before the reading user info
                    for (int i = 0; i <= position - 1; i++) {
                        tmp += dataLength.get(i);
                    }
                    fc.position(tmp);
                }

                ByteBuffer copy = ByteBuffer.allocate(dataLength.get(position) - 3);
                int nread;
                do{
                    nread = fc.read(copy);
                } while (nread != -1 && copy.hasRemaining());
                fc.close();
                String s = new String(copy.array());
                String[] info = s.split("\\|");
                tfName.setText(info[0]);
                tfStreet.setText(info[1]);
                tfCity.setText(info[2]);
                tfState.setText(info[3]);
                tfZip.setText(info[4]);
            } catch(IOException e){
                System.out.println(e);
            }
        }
    }

    public static void showDialog(String title, String message){
        Text txt = new Text(message);
        Button ok = new Button("Ok");
        ok.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                Node source = (Node)  event.getSource();
                Stage stage  = (Stage) source.getScene().getWindow();
                stage.close();
            }
        });
        StackPane pane1 = new StackPane();
        pane1.getChildren().add(txt);
        StackPane pane2 =  new StackPane();
        pane2.getChildren().add(ok);
        GridPane pane = new GridPane();
        pane.setVgap(10);
        pane.add(pane1, 0, 0);
        pane.add(pane2, 0, 1);
        pane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(pane, 250, 100);

        Stage error = new Stage();
        error.setTitle(title);
        error.setScene(scene);
        error.show();
    }

    @Override
    public void start(Stage primaryStage) {
        loadData();

        // GridPane
        GridPane pane1 = new GridPane();
        pane1.setAlignment(Pos.CENTER);
        pane1.setHgap(5);
        pane1.setVgap(5);
        pane1.setPadding(new Insets(5, 5, 5, 5));

        tfName.setPrefColumnCount(25);
        tfName.setMaxLength(32);

        tfStreet.setPrefColumnCount(25);
        tfStreet.setMaxLength(32);

        tfCity.setPrefColumnCount(15);
        tfCity.setMaxLength(20);

        tfState.setPrefColumnCount(2);
        tfState.setMaxLength(2);

        tfZip.setPrefColumnCount(5);
        tfZip.setMaxLength(6);

        pane1.add(new Label("Name"), 0, 0);
        pane1.add(tfName, 1, 0, 5 ,1);
        pane1.add(new Label("Street"), 0, 1);
        pane1.add(tfStreet, 1,1,5 ,1);
        pane1.add(new Label("City"), 0, 2);
        pane1.add(tfCity, 1 , 2);
        pane1.add(new Label("State"), 2, 2);
        pane1.add(tfState, 3, 2);
        pane1.add(new Label("Zip"), 4, 2);
        pane1.add(tfZip, 5, 2);

        // Flow Pane
        FlowPane pane2 = new FlowPane();
        pane2.setAlignment(Pos.CENTER);
        pane2.setPadding(new Insets(5 , 5,5 ,5));
        pane2.setHgap(5);
        pane2.setVgap(5);

        Button btAdd = new Button("Add");
        btAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(isInputEmpty()){
                    showDialog("Error","Input cannot be empty");
                } else if (checkEntry()){
                    showDialog("Error","Input cannot contain ; | \\ / \" \'");
                } else if(checkState())
                    showDialog("Error","State must contain 2 letters (no number)");
                else if(checkZip())
                    showDialog("Error","Zip Code must contain 6 characters");
                else {
                    String s = tfName.getText() + "|" + tfStreet.getText() + "|" + tfCity.getText() +
                            "|" + tfState.getText() + "|" + tfZip.getText() + ";\r\n";

                    byte data[] = s.getBytes();

                    ByteBuffer out = ByteBuffer.wrap(data);

                    try {
                        Path path = FileSystems.getDefault().getPath("Address.dat");
                        FileChannel fc = FileChannel.open(path, CREATE, APPEND);

                        if(fc.size() != 0)
                            fc.position(fc.size() - 1);
                        while(out.hasRemaining()){
                            fc.write(out);
                        }
                        dataLength.add(s.length());
                        showDialog("Success", "User Info added");
                        clear();
                    } catch(IOException e){
                        System.out.println(e);
                    }
                }
            }
        });

        Button btFirst = new Button("First");
        btFirst.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                counter = 0;
                loadUserInfo(counter);
            }
        });

        Button btNext = new Button("Next");
        btNext.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(counter < dataLength.size() - 1)
                    loadUserInfo(++counter);

            }
        });

        Button btPrevious = new Button("Previous");
        btPrevious.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(counter > 0)
                    loadUserInfo(--counter);
            }
        });

        Button btLast = new Button("Last");
        btLast.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(dataLength.size() != 0){
                    counter = dataLength.size() - 1;
                    loadUserInfo(counter);
                }
            }
        });

        Button btUpdate = new Button("Update");
        btUpdate.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(isInputEmpty()){
                    showDialog("Error","Input cannot be empty");
                } else if (checkEntry()){
                    showDialog("Error","Input cannot contain ; | \\ / \" \'");
                } else if(checkState())
                    showDialog("Error","State must contain 2 letters (no number)");
                else if(checkZip())
                    showDialog("Error","Zip Code must contain 6 characters");
                else {
                    String s = tfName.getText() + "|" + tfStreet.getText() + "|" + tfCity.getText() +
                            "|" + tfState.getText() + "|" + tfZip.getText();
                    if(dataLength.size() == 0)
                        showDialog("Error","Data is empty");
                    else {
                        try {
                            Path path = FileSystems.getDefault().getPath("Address.dat");
                            FileChannel fc = FileChannel.open(path, CREATE, READ);
                            if(fc.size() != 0){
                                ByteBuffer copy = ByteBuffer.allocate((int)fc.size() - 3); // -3 to remove the last characters (";\r\n")
                                int nread;
                                do{
                                    nread = fc.read(copy);
                                } while(nread != -1 && copy.hasRemaining());
                                fc.close();
                                String data = new String(copy.array());
                                String str[] = data.split(";\\r\\n");
                                str[counter] = s;
                                dataLength.set(counter, s.length() + 3);
                                s = "";
                                for (int i = 0; i < str.length; i++) {
                                    s += str[i] + ";\r\n";
                                }
                            }
                            if(fc != null)
                                fc.close();
                            fc = FileChannel.open(path, CREATE, WRITE);
                            byte data[] = s.getBytes();
                            ByteBuffer out = ByteBuffer.wrap(data);
                            while(out.hasRemaining())
                                fc.write(out);
                            fc.close();
                            showDialog("Success", "User Info updated");
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                }
            }
        });

        pane2.getChildren().addAll(btAdd, btFirst, btNext, btPrevious, btLast, btUpdate);

        // grid pane?
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5 , 5,5 ,5));
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5);
        pane.setVgap(5);
        pane.add(pane1, 0, 0);
        pane.add(pane2, 0, 1);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("Address Book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
