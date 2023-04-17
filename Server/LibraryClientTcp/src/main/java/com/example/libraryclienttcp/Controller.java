package com.example.libraryclienttcp;

import com.example.libraryclienttcp.dto.TcpInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final String libraryClientIp = "192.168.1.10";
    private final Integer readSocketPort = 6666;
    private final Integer sendSocketPort = 6667;
    private final String successResponse = "success";
    private final String failResponse = "fail";
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;
    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label orderTypeLabel;

    @FXML
    private Label publishYearLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label authorLabel;

    @FXML
    private Label bookIdLabel;

    public Controller() {
        new Thread(() -> {
            Socket s;
            while (true) {
                try {
                    ServerSocket ss = new ServerSocket(readSocketPort);
                    s = ss.accept();
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    var result = dis.readUTF();
                    var tempInfo = objectMapper.readValue(result, TcpInfoDto.class);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            reloadTitles(tempInfo);
                            acceptButton.setDisable(false);
                            rejectButton.setDisable(false);
                        }
                    });
                    ss.close();
                    s.close();
                } catch (IOException aE) {
                    throw new RuntimeException(aE);
                }
            }
        }).start();
    }


    @FXML
    protected void rejectOrder() {
        reloadTitles(null);
        sendResponseToServer(failResponse);
        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
    }

    @FXML
    protected void confirmOrder() {
        reloadTitles(null);
        sendResponseToServer(successResponse);
        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
    }

    private void sendResponseToServer(String response) {
        Socket s;
        DataOutputStream dout;
        try {
            s = new Socket(libraryClientIp,sendSocketPort);
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(response);
            dout.flush();
            dout.close();
            s.close();
        } catch (IOException aE) {
            throw new RuntimeException(aE);
        }
    }

    public void reloadTitles(TcpInfoDto aTcpInfoDto){
        var firstName = "Imię : ";
        var lastName = "Nazwisko : ";
        var address = "Adres : ";
        var orderType = "Typ zamówienia : ";
        var yearOfPublish = "Rok publikacji książki : ";
        var title = "Tytuł książki : ";
        var author = "Autor książki : ";
        var bookId = "Id książki : ";
        if(aTcpInfoDto != null){
             firstName = firstName.concat(aTcpInfoDto.getFirstName());
             lastName = lastName.concat(aTcpInfoDto.getLastName());
             address = address.concat(aTcpInfoDto.getAddress());
             orderType = orderType.concat(aTcpInfoDto.getOrderType());
             yearOfPublish = yearOfPublish.concat(aTcpInfoDto.getYearOfPublish().toString());
             title = title.concat(aTcpInfoDto.getTitle());
             author = author.concat(aTcpInfoDto.getAuthor());
             bookId = bookId.concat(aTcpInfoDto.getBookId().toString());
        }
        firstNameLabel.setText(firstName);
        lastNameLabel.setText(lastName);
        addressLabel.setText(address);
        orderTypeLabel.setText(orderType);
        publishYearLabel.setText(yearOfPublish);
        titleLabel.setText(title);
        authorLabel.setText(author);
        bookIdLabel.setText(bookId);
    }
}