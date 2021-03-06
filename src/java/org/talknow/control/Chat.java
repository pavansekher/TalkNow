package org.talknow.control;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.talknow.model.Message;
import org.talknow.model.User;

public class Chat extends HttpServlet {

    public Vector<User> chatUsers = new Vector<>();
    public Vector<Message> messages = new Vector<>();
    
    /*
    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
    }
    */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("utf-8"); 
        PrintWriter writer = response.getWriter();
        
        Gson messagesJsonBuilder = new Gson();
        String messagesJson = messagesJsonBuilder.toJson(messages);
        Gson usersJsonBuilder = new Gson();
        String usersJson = usersJsonBuilder.toJson(chatUsers);
        String bothJson = "["+messagesJson+","+usersJson+"]";
        writer.write(bothJson);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = response.getWriter();

        String action = request.getParameter("action");
        if  (action.equals("register")){
            String username = request.getParameter("userName");
            String userEmail = request.getParameter("userEmail");
            String userPassword = request.getParameter("userPass");
            User newUser = new User(username, userPassword, userEmail);
            chatUsers.add(newUser);
            HttpSession mSession = request.getSession(true);
            mSession.setAttribute("userName", username);
            System.out.println(username);
        } else if (action.equals("login")) {
            String username = checkLoginParameters(request);
            if (username != null) {
                HttpSession mSession = request.getSession(true);
                mSession.setAttribute("userName", username);
                writer.write("valid");
            } else {
                writer.write("invalid");
            }
        } else if (action.equals("message")) {
            String senderUser = request.getParameter("user");
            String messageText = request.getParameter("msg");
            Message msg = new Message(senderUser, messageText);
            messages.add(msg);
        } else if (action.equals("logout")) {
            request.getSession(false).invalidate();
        }

    }

    private String checkLoginParameters(HttpServletRequest request) {
        String username = null;
        String email = request.getParameter("userEmail");
        String password = request.getParameter("userPass");
        for (User u : chatUsers) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                username = u.getUserName();
            }
        }
        return username;
    }

}
