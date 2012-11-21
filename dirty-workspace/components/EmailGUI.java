/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package components;

import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

import java.awt.*;              //for layout managers and more
import java.awt.event.*;        //for action events

import java.net.URL;
import java.io.IOException;

public class EmailGUI extends JPanel
                             implements ActionListener {
    private String newline = "\n";
    protected static final String emailString = "Senders e-mail";
    protected static final String emailPassword = "e-mail password";
    protected static final String buttonString = "JButton";

    protected static JButton namesButton;
    protected static JButton emailsButton;
    protected static JButton pushTemplate;
    protected static JButton next;
    protected static JButton send;

    /* Flags */
    private String gotEmail;
    private String gotPassword;

    /* For File Chooser */
    private JFileChooser fcName;
    private JFileChooser fcEmail;
    private JTextArea textArea;

    /*Utilities */
    private Templator tmpl;
    private ListIterator itrNames;
    private ListIterator itrEmails;
    private EmailSender ems;

    /*Gobals*/
    private String currentEmail;
    private String lastEmail;
    private JTextPane textPane;
    private JTextField textField;
    private JTextField subjectField;
    private JPasswordField passwordField;
    protected JLabel actionLabel;
    protected JLabel emailLabel;
    private String msg = null;

    private void EmailInit() {
        ems = new EmailSender();
        gotEmail = "Not e-mail set yet";
        currentEmail = "";
        lastEmail = "";
    }

    public EmailGUI() {
        //Initializing utilities
        EmailInit();

        setLayout(new BorderLayout());

        //Create a regular text field.
        textField = new JTextField(10);
        textField.setActionCommand(emailString);
        textField.addActionListener(this);

        //Create a password field.
        passwordField = new JPasswordField(10);
        passwordField.setActionCommand(emailPassword);
        passwordField.addActionListener(this);

        //Create some labels for the fields.
        JLabel textFieldLabel = new JLabel(emailString + ": ");
        textFieldLabel.setLabelFor(textField);
        JLabel passwordFieldLabel = new JLabel(emailPassword + ": ");
        passwordFieldLabel.setLabelFor(passwordField);

        //Create a label to put messages during an action event.
        actionLabel = new JLabel("Type text in a field and press Enter.");
        actionLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        //Create a label for recipient email address.
        emailLabel = new JLabel("Recipient email: " + gotEmail);
        emailLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        //Lay out the text controls and the labels.
        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        textControlsPane.setLayout(gridbag);

        JLabel[] labels = {textFieldLabel, passwordFieldLabel};
        JTextField[] textFields = {textField, passwordField};
        addLabelTextRows(labels, textFields, gridbag, textControlsPane);

        c.gridwidth = GridBagConstraints.REMAINDER; //last
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        textControlsPane.add(actionLabel, c);
        textControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Text Fields"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));

        //Create a text area.
        textArea = new JTextArea(
            "Input the template here. Put the judge name variable" +
            " as \n <%= name %>.\n e.g. 'Dear sifu <%= name %>'"
        );
        textArea.setFont(new Font("Serif", Font.ITALIC, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(500, 600));
        areaScrollPane.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Template"),
                                BorderFactory.createEmptyBorder(5,5,5,5)),
                areaScrollPane.getBorder()));
        pushTemplate = new JButton("Set Template");
        pushTemplate.addActionListener(this);


        //Button panel
        JPanel buttonPanel = new JPanel (new BorderLayout());

        namesButton = new JButton("Attach names...");
        namesButton.addActionListener(this);
        emailsButton = new JButton("Attach emails...");
        emailsButton.addActionListener(this);
        buttonPanel.add(namesButton, BorderLayout.PAGE_START);
        buttonPanel.add(emailsButton, BorderLayout.CENTER);
        buttonPanel.setPreferredSize(new Dimension(500, 20));
        buttonPanel.setMinimumSize(new Dimension(10, 10));
        buttonPanel.add(emailLabel, BorderLayout.PAGE_END);

        //Create pane
        textPane = createInitTextPane();
        JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(500, 600));
        paneScrollPane.setMinimumSize(new Dimension(10, 10));
        paneScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                              BorderFactory.createTitledBorder("Personalized"+
                                                                " e-mail"),
                              BorderFactory.createEmptyBorder(5,5,5,5)));

        //Initializing next and send button
        next = new JButton("Next");
        next.addActionListener(this);
        send = new JButton("Send");
        send.addActionListener(this);
        JPanel commandPanel = new JPanel();
        commandPanel.add(next, BorderLayout.LINE_START);
        commandPanel.add(send, BorderLayout.LINE_END);
        commandPanel.setPreferredSize(new Dimension(500, 20));
        commandPanel.setMinimumSize(new Dimension(10, 10));

        //Put the File chooser pane and the text pane in a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                              commandPanel,
                                              paneScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.36);
        JPanel rightPane = new JPanel(new GridLayout(1,0));
        rightPane.add(splitPane);
        rightPane.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Styled Text"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
        JSplitPane newRPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                             splitPane,
                                             buttonPane);
        newRPane.setOneTouchExpandable(true);
        newRPane.setResizeWeight(0.97);



        //Put everything together.
        JPanel leftPane = new JPanel(new BorderLayout());
        leftPane.add(textControlsPane, 
                     BorderLayout.PAGE_START);
        leftPane.add(areaScrollPane,
                     BorderLayout.CENTER);
        leftPane.add(pushTemplate, BorderLayout.PAGE_END);

        JSplitPane splitHorPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                leftPane,
                                                newRPane);
        splitHorPane.setOneTouchExpandable(true);
        splitHorPane.setResizeWeight(0.5);

        add(splitHorPane, BorderLayout.CENTER);
    }

    private void addLabelTextRows(JLabel[] labels,
                                  JTextField[] textFields,
                                  GridBagLayout gridbag,
                                  Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        int numLabels = labels.length;

        for (int i = 0; i < numLabels; i++) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            container.add(labels[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            container.add(textFields[i], c);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String prefix = "You entered ";
        if (emailString.equals(e.getActionCommand())) {
            JTextField source = (JTextField)e.getSource();
            actionLabel.setText(prefix + source.getText() + "\"");
            gotEmail = textField.getText();
        } else if (emailPassword.equals(e.getActionCommand())) {
            JPasswordField source = (JPasswordField)e.getSource();
            actionLabel.setText(prefix + "your password");
            gotPassword = passwordField.getText();
        } else if (buttonString.equals(e.getActionCommand())) {
            Toolkit.getDefaultToolkit().beep();
        } else if (e.getSource() == namesButton) {
            if (fcName == null) {
                fcName = new JFileChooser();
            }
            fcName.addChoosableFileFilter(new TextFileFilter());
            fcName.setAcceptAllFileFilterUsed(false);
            //Show the pop-up window
            int returnVal = fcName.showDialog(EmailGUI.this,
                                          "Attach");
            //Process the results.
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //Initializing global var
                File Names = fcName.getSelectedFile();
                itrNames = new ListIterator(Names);
            }
            //Reset the file chooser for the next time it's shown.
            fcName.setSelectedFile(null);
        } else if (e.getSource() == emailsButton) {
            if (fcEmail == null) {
                fcEmail = new JFileChooser();
            }
            fcEmail.addChoosableFileFilter(new TextFileFilter());
            fcEmail.setAcceptAllFileFilterUsed(false);
            //Show the pop-up window
            int returnVal = fcEmail.showDialog(EmailGUI.this,
                                          "Attach");
            //Process the results.
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //Initializing global var
                File Emails = fcEmail.getSelectedFile();
                itrEmails = new ListIterator(Emails);
            }
            //Reset the file chooser for the next time it's shown.
            fcEmail.setSelectedFile(null);
        } else if (e.getSource() == pushTemplate) {
            String template = null;
            try {
                template = textArea.getText();
                tmpl = new Templator(template);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == next) {
            if ((tmpl != null) && (itrNames != null) && (itrEmails) != null) {
                if (itrEmails.hasNext() && itrNames.hasNext()) {
                    try {
                        msg = tmpl.generate(itrNames.next());
                    } catch (Exception et) {
                        et.printStackTrace();
                    }
                    if (msg != null) {
                        currentEmail = itrEmails.next();
                        emailLabel.setText("Recipient email: " +
                                            currentEmail);
                    } else {
                        setText("You need to generate template first!",
                                textPane);
                    }
                } else {
                    msg = "No more emails";
                }
                setText(msg, textPane);

            } else {
                setText("Please load the template, names.txt and emails.txt",
                        textPane);
            }
        } else if (e.getSource() == send) {
            if (itrEmails == null || itrNames == null || tmpl == null) {
                setText("Please load the template, names.txt and emails.txt",
                        textPane);
            } else {
                if (!currentEmail.equals(lastEmail)) {
                    lastEmail = currentEmail;
                    if ((gotEmail != null) && (gotPassword != null)) {
                        ems.connect(gotEmail.substring(0, gotEmail.indexOf('@')),
                                    gotPassword, gotEmail, 
                                    "smtp.gmail.com", 465);
                        try {
                            ems.send(currentEmail, "Temp Subject", msg);
                        } catch (Exception emse) {
                            emse.printStackTrace();
                        }
                    } else {
                        if (gotEmail == null) {
                            actionLabel.setText("You must enter your email");
                        } else if (gotPassword == null) {
                            actionLabel.setText("You must enter your " +
                                                "email password");
                        }
                    }
                }
            } 
            //Load up email fields
        }
    }

    private JTextPane createMsg(String msg) {
        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), msg, doc.getStyle("regular"));
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
        return textPane;
    }

    private void setText(String toSet, JTextPane textPane) {
        StyledDocument doc = textPane.getStyledDocument();
        textPane.setText("");
        try {
            doc.insertString(doc.getLength(), toSet, doc.getStyle("regular"));
        } catch (BadLocationException ble1) {
            ble1.printStackTrace();
        }
    }

    private JTextPane createInitTextPane() {
        String initString = "There are no e-mails generated yet";
        String initStyles = "regular";
        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), initString,
                                 doc.getStyle(initStyles));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
        return textPane;
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("EmailGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new EmailGUI());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 //Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
            }
        });
    }
}
/** have to stack panels in right pane */
