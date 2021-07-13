
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;


/**
 * A program to display the COVID-19 X-rays and info ot the patients.
 * Author: Skryabin Nikolay, CSCI 230.
 * @version 05/25/2021
 */
public class ImageViewer extends JFrame
{
    // The limit for reading from a web page
    private static final int LIMIT_OF_PATIENTS = 100;
    // X position for left alignment of all elements in the app
    private static final int X_POSITION = 175;
    // A button to hold a patient info
    private JButton patientInfoButton;
    // Button to hold the big image
    private JButton bigImageButton;
    // List of the patients objects
    private ArrayList<PatientInfo> patientsInfoList;
    // Panels to hold the visual components of the program
    private JPanel controlButtonsPanel;
    private JPanel bigImagePanel;
    private JPanel imageGalleryPanel;
    private JPanel infoPanel;
    // List to hold buttons with thumbnail images
    private ArrayList<JButton> imageGalleryButtons;
    // List to hold urls from the file
    private ArrayList<String> imageURLsList;
    // A variable to track index of the current displayed image and info from the lists
    private int listIndex;
    // A scroll pane for image gallery
    JScrollPane imageGalleryScrollPane;
    // A scroll pane for a patient info
    JScrollPane infoScrollPane;
    // Timer for replay mode
    Timer timer;
    // Control buttons
    private JButton replayButton;
    private JButton homeButton;
    private JButton previousButton;
    private JButton stopButton;
    private JButton nextButton;
    private JButton lastButton;
    private JButton thumbnailButton;
    private JButton[] controlButtons;

    // Font for the titles
    Font titleFont;

    // Constructor
    public ImageViewer() throws IOException
    {
        // Set name and parameters for the frame:
        super("The Covid-19 Image Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLayout(null);
        setLocationRelativeTo(null);  // center the window
        setResizable(false);

        // Set font for the titles:
        titleFont = new Font("Helvetica", Font.BOLD, 30);

        // Set a titled border for the frame:
        TitledBorder frameTitledBorder = BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, Color.BLACK),
                "The Covid-19 Image Viewer",TitledBorder.CENTER,TitledBorder.TOP, titleFont, Color.BLACK);
        getRootPane().setBorder(frameTitledBorder);

        // set the index to 0
        listIndex = 0;

        // create URLs with images
        imageURLsList = createImageURLsList();

        // create info list:
        patientsInfoList = createPatientsListInfoFromGithub();

        // Create a panel to display big images:
        bigImagePanel = createBigImagePanel();
        bigImagePanel.setVisible(true);
        add(bigImagePanel);

        // Create a panel with a patient info:
        infoPanel = createInfoPanel();

        // Add it to the scroll pane
        infoScrollPane = new JScrollPane(infoPanel);
        infoScrollPane.setBounds(X_POSITION,50, 600, 700);
        // Set a titled border for the infoScrollPane:
        TitledBorder infoBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,0,0),"Patient Info",TitledBorder.CENTER,TitledBorder.ABOVE_TOP, titleFont, Color.BLACK);
        infoScrollPane.setBorder(infoBorder);
        // hide it
        infoScrollPane.setVisible(false);
        add(infoScrollPane);

        // Create a panel with control buttons
        controlButtonsPanel = createControlButtonsPanel();
        // display it
        controlButtonsPanel.setVisible(true);
        add(controlButtonsPanel);

        // Create the thumbnail images panel
        imageGalleryPanel = createImageGalleryPanel();
        // Add it to the scroll pane
        imageGalleryScrollPane = new JScrollPane(imageGalleryPanel);
        imageGalleryScrollPane.setBounds(X_POSITION,50, 600, 700);
        // Set a titled border for the imageGalleryScrollPane:
        TitledBorder imageGalleryBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,0,0),"Image Gallery",TitledBorder.CENTER,TitledBorder.ABOVE_TOP, titleFont, Color.BLACK);
        imageGalleryScrollPane.setBorder(imageGalleryBorder);
        // hide it
        imageGalleryScrollPane.setVisible(false);
        add(imageGalleryScrollPane);

        setVisible(true);
    }// End of constructor

    /**
     * Creates a JPanel with a big image.
     * @return panel
     */
    private JPanel createBigImagePanel()
    {
        JPanel panel = new JPanel();

        panel.setBounds(X_POSITION,50, 600, 700);

        // set text and image for the big image button
        bigImageButton = new JButton(getBigImageButtonText(listIndex));

        // Make the button to display only image
        bigImageButton.setFocusable(false);
        bigImageButton.setOpaque(false);
        bigImageButton.setBorderPainted(false);
        bigImageButton.setContentAreaFilled(false);
        bigImageButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        // set the hand cursor
        bigImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // if image is clicked action
        bigImageButton.addActionListener(event ->
        {
            // stop replay
            if (timer != null)
            {
                timer.stop();
            }
            // hide the big image and control panels
            bigImagePanel.setVisible(false);
            controlButtonsPanel.setVisible(false);
            // display info panel
            infoScrollPane.setVisible(true);
        });

        panel.add(bigImageButton);

        return panel;
    }// End of the big image panel

    /**
     * Returns a text for big image button at the imageURLsList index.
     * @param index the list index
     * @return text
     */
    private String getBigImageButtonText(int index)
    {
        String text = "<html>  <p ALIGN='CENTER'> <h2 align=center> Patient: #" + patientsInfoList.get(index).getPatientID() +
                "</h2><br> <IMG SRC='" + imageURLsList.get(listIndex) + "' width ='600' height='700'</p></html>";
        return text;
    }// End of the method

    /**
     * Creates a JPanel with the control buttons.
     * @return panel
     */
    private JPanel createControlButtonsPanel()
    {
        JPanel panel = new JPanel();
        panel.setBounds(X_POSITION, 800, 600,50);
        panel.setLayout(new GridLayout(1, 7, 20, 10));

        // Set the control buttons with the control images accordingly
        replayButton = new JButton("<HTML><BODY> <P align='center'> <IMG SRC='https://cdn4.iconfinder.com/data/icons/media-player-ui-32-px/32/Media_player-replay-playback-rotate-128.png' width ='50' height='50' ></P></BODY></HTML>");

        homeButton = new JButton("<HTML><BODY> <P align='center'> <IMG SRC='https://cdn3.iconfinder.com/data/icons/ui-ux-circles-1/24/home-circle-128.png' width ='50' height='50' ></P></BODY></HTML>");

        previousButton = new JButton("<HTML><BODY> <P align='center'> <IMG SRC='https://cdn0.iconfinder.com/data/icons/mutuline-audio/48/previous_controls_music_media_player_1-128.png' width ='50' height='50' ></P></BODY></HTML>");

        stopButton = new JButton("<HTML><BODY> <P align='center'> <IMG SRC='https://cdn4.iconfinder.com/data/icons/multimedia-player-ui/24/multimedia_stop-128.png' width ='50' height='50' ></P></BODY></HTML>");

        nextButton = new JButton("<HTML><BODY> <P align='center'> <IMG SRC='https://cdn0.iconfinder.com/data/icons/mutuline-audio/48/next_controls_music_media_player_1-128.png' width ='50' height='50' ></P></BODY></HTML>");

        lastButton = new JButton("<HTML><BODY> <P align='center'> <IMG SRC='https://cdn0.iconfinder.com/data/icons/mutuline-audio/48/next_controls_music_media_player-128.png' width ='50' height='50' ></P></BODY></HTML>");

        thumbnailButton = new JButton("<HTML><BODY> <P align='center'> <IMG SRC='https://cdn1.iconfinder.com/data/icons/ui-essentials-14/32/UI_Essentials_button_circle_round_menu-64.png' width ='50' height='50'></P></BODY></HTML>");

        controlButtons = new JButton[]{homeButton, previousButton, replayButton, stopButton, nextButton, lastButton, thumbnailButton};

        // Add parameters and add the buttons to the panel
        for (JButton jButton : controlButtons)
        {
            // Make the buttons display only icons
            jButton.setFocusable(false);
            jButton.setOpaque(false);
            jButton.setBorderPainted(false);
            jButton.setContentAreaFilled(false);
            jButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
            // set the cursor
            jButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // Add to the panel
            panel.add(jButton);
        }

        // Implement the control buttons actions and keep tracking the index of each visual element:

        // Replay action using timer with delay 5 seconds
        replayButton.addActionListener(event ->
        {
            timer = new Timer(5000, e ->
            {
                listIndex++; // iterate the index

                if (listIndex < imageURLsList.size())
                {
                    // Set the visual elements at the image index accordingly
                    setElementsAtIndex(listIndex);
                }
                else
                {
                    timer.stop();
                }
            });
            timer.start();
        });

        // Home button action
        homeButton.addActionListener(event ->
        {
            if (timer != null)
            {
                // stop replay
                timer.stop();
            }
            listIndex = 0;
            // Set the elements at the image index
            setElementsAtIndex(listIndex);
        });

        // Previous action
        previousButton.addActionListener(event ->
        {
            if (timer != null)
            {
                // stop replay
                timer.stop();
            }
            if (listIndex > 0)
            {
                listIndex--;
            }
            else
            {
                listIndex = 0;
            }
            // Set the elements at the image index
            setElementsAtIndex(listIndex);
        });

        // Stop button action
        stopButton.addActionListener(event ->
        {
            if (timer != null)
            {
                // stop replay
                timer.stop();
            }
        });

        // Next action
        nextButton.addActionListener(event ->
        {
            if (timer != null)
            {
                // stop replay
                timer.stop();
            }
            if (listIndex < imageURLsList.size() - 1)
            {
                listIndex++;
            }
            else
            {
                listIndex = imageURLsList.size()-1;
            }
            // Set the elements at the image index
            setElementsAtIndex(listIndex);
        });
        // Last action
        lastButton.addActionListener(event ->
        {
            if (timer != null)
            {
                // stop replay
                timer.stop();
            }

            listIndex = imageURLsList.size() - 1;
            // Set the elements at the image index
            setElementsAtIndex(listIndex);
        });
        // Thumbnail button action
        thumbnailButton.addActionListener(event ->
        {
            if (timer != null)
            {
                // stop replay
                timer.stop();
            }
            // Hide the panel with big image and controls
            bigImagePanel.setVisible(false);
            controlButtonsPanel.setVisible(false);
            // display the thumb images
            imageGalleryScrollPane.setVisible(true);
        });

        return panel;
    }// End of the control buttons panel

    /**
     * Creates a JPanel with a patient info.
     * @return panel
     */
    private JPanel createInfoPanel()
    {
        JPanel panel = new JPanel();

        // Set parameters for the panel
        panel.setBounds(X_POSITION,50, 600, 700);
        panel.setLayout(new GridLayout(1, 1, 1, 1));
        panel.setBackground(Color.BLACK);

        // Create the patient info button
        patientInfoButton = new JButton(getPatientInfoButtonText(listIndex));

        // Set parameters for the patient info button
        patientInfoButton.setBackground(Color.BLACK);
        patientInfoButton.setForeground(Color.WHITE);
        patientInfoButton.setFocusable(false);
        patientInfoButton.setOpaque(false);
        patientInfoButton.setBorderPainted(false);
        patientInfoButton.setContentAreaFilled(false);
        patientInfoButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        patientInfoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Action for the button
        patientInfoButton.addActionListener(event ->
                {
                    // hide the info and image gallery panels
                    infoScrollPane.setVisible(false);
                    imageGalleryScrollPane.setVisible(false);
                    // display the big image and control buttons panels
                    bigImagePanel.setVisible(true);
                    controlButtonsPanel.setVisible(true);

                });

        // Add to the panel
        panel.add(patientInfoButton);

        return panel;
    }// End of the info panel

    /**
     * Returns a text for patient info button at the list index.
     * @param index the list index
     * @return text
     */
    private String getPatientInfoButtonText(int index)
    {
        String text = "<html><body>  <p > <h1 align='center'>Categories & Results </h1>" +
                "<br><br><h2> Patient ID: &nbsp;&nbsp;" + patientsInfoList.get(index).getPatientID() + "" +
                "<br><br> Offset: &nbsp;&nbsp;" + patientsInfoList.get(index).getOffset() +
                "<br><br> Sex:  &nbsp;&nbsp; " + patientsInfoList.get(index).getSex() +
                "<br><br> Age:  &nbsp;&nbsp;" + patientsInfoList.get(index).getAge() +
                "<br><br> Finding: &nbsp;&nbsp;" + patientsInfoList.get(index).getFinding() +
                "<br><br> Survival: &nbsp;&nbsp;" + patientsInfoList.get(index).getSurvival() +
                "<br><br> Intubated:  &nbsp;&nbsp;" + patientsInfoList.get(index).getIntubated() +
                "<br><br> Intubation present: &nbsp;&nbsp;" + patientsInfoList.get(index).getIntubation_present() +
                "<br><br> Went icu:  &nbsp;&nbsp;" + patientsInfoList.get(index).getWent_icu() +
                "<br><br> In icu:    &nbsp;&nbsp;" + patientsInfoList.get(index).getIn_icu() +
                "<br><br> Needed supplemental O2:  &nbsp;&nbsp;" + patientsInfoList.get(index).getNeeded_supplemental_O2() +
                "<br><br> Extubated: &nbsp;&nbsp;" + patientsInfoList.get(index).getExtubated() +
                "<br><br> Temperature: &nbsp;&nbsp;" + patientsInfoList.get(index).getTemperature() +
                "<br><br> PO2 saturation:  &nbsp;&nbsp;" + patientsInfoList.get(index).getpO2_saturation() +
                "<br><br> Leukocyte count: &nbsp;&nbsp;" + patientsInfoList.get(index).getLeukocyte_count() +
                "<br><br> Neutrophil count:  &nbsp;&nbsp;" + patientsInfoList.get(index).getNeutrophil_count() +
                "<br><br> Lymphocyte count:  &nbsp;&nbsp; " + patientsInfoList.get(index).getLymphocyte_count() +
                "<br><br> View:    &nbsp;&nbsp;" + patientsInfoList.get(index).getView() +
                "<br><br> Modality:  &nbsp;&nbsp;" + patientsInfoList.get(index).getModality() +
                "<br><br> Date:     &nbsp;&nbsp;" + patientsInfoList.get(index).getDate() +
                "<br><br> Location:  &nbsp;&nbsp;" + patientsInfoList.get(index).getLocation() +
                "</h2></p><body></html>";

        return text;
    }


    /**
     * Returns a JPanel for the Image Gallery.
     *  @return panel
     */
    private JPanel createImageGalleryPanel()
    {
        JPanel panel = new JPanel();
        panel.setBounds(X_POSITION,50, 600, 700);

        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);

        // set rows and columns for the image buttons grid
        int columns = 5;
        int rows = patientsInfoList.size()/columns;

        panel.setLayout(new GridLayout(rows, columns, 10, 10));

        // Create a list with the image buttons
        imageGalleryButtons = new ArrayList<>();

        for (int i = 0; i < imageURLsList.size(); i++)
        {
            // Set the thumbnail sized image for each image gallery button
            imageGalleryButtons.add(new JButton("<html>  <p ALIGN='CENTER'> <h3 color=white>Patient ID: " + patientsInfoList.get(i).getPatientID() +
                    "</h3><br> <IMG SRC='" + imageURLsList.get(i) + "' width ='100' height='125'</p></html>"));
            // Make the buttons display only images
            imageGalleryButtons.get(i).setFocusable(false);
            imageGalleryButtons.get(i).setOpaque(false);
            imageGalleryButtons.get(i).setBorderPainted(false);
            imageGalleryButtons.get(i).setContentAreaFilled(false);
            imageGalleryButtons.get(i).setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

            // set cursor to the hand cursor
            imageGalleryButtons.get(i).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // add to the panel
            panel.add(imageGalleryButtons.get(i));
        }

        // Add action to the buttons:
        for (JButton imgButton: imageGalleryButtons)
        {
            imgButton.addActionListener(event -> {

                int i = 0;

                for (JButton thumbButton: imageGalleryButtons) {

                    if (event.getSource().equals(thumbButton))
                    {

                        listIndex = i;
                        // Set the elements at the image index
                        setElementsAtIndex(listIndex);
                    }

                    // hide the thumbnail panel
                    imageGalleryScrollPane.setVisible(false);
                    // display the big image and buttons panel
                    bigImagePanel.setVisible(true);
                    controlButtonsPanel.setVisible(true);
                    i++;
                }
            });
        }
        return panel;
    }// End of the thumbnail images panel


    /**
     * Sets the big image and info buttons with parameters according to their index in the lists.
     * @param index the list index
     */
    private void setElementsAtIndex(int index)
    {
        // Set the label with the big picture
        bigImageButton.setText(getBigImageButtonText(index));

        // Set the info label
        patientInfoButton.setText(getPatientInfoButtonText(index));
    }

    /**
     * Creates list with images URLs from the GitHub.
     * @return list
     */
    private ArrayList<String> createImageURLsList()
    {
        ArrayList<String> list = new ArrayList<>();

        try {

            URL url = new URL("https://github.com/ieee8023/covid-chestxray-dataset/tree/master/images");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String info;

            int numLine = 0;

            // Read the web page line by line into string info
            while ((info = bufferedReader.readLine()) != null)
            {
                // check for the url image pattern in each line on the GitHub web page
                if (info.contains("/ieee8023/covid-chestxray-dataset/blob/master/images/"))
                {
                    // get the URL suffix from the line
                    String[] value = info.split("\"");
                    String URLSuffix = value[9];
                    numLine++;

                    // add to the list the completed url
                    list.add("https://github.com" + URLSuffix + "?raw=true");
                }

                if (numLine == LIMIT_OF_PATIENTS)
                {
                    break;
                }
            }
             System.out.println("the lines with image URLs: " + numLine); // the total: 927 images
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //  <span class="css-truncate css-truncate-target d-block width-fit"><a class="js-navigation-open Link--primary" title="000001-1.jpg" data-pjax="#repo-content-pjax-container" href="/ieee8023/covid-chestxray-dataset/blob/master/images/000001-1.jpg">000001-1.jpg</a></span>
        return list;
    }// End of the method


    /**
     * Creates a list with the patients info objects from GitHub.
     * @return list
     */
    private ArrayList<PatientInfo> createPatientsListInfoFromGithub()
    {

        ArrayList<PatientInfo> list = new ArrayList<>();

        try {
            URL url = new URL("https://raw.githubusercontent.com/ieee8023/covid-chestxray-dataset/master/metadata.csv");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String info;

            int countLine = 0;

            // Read info from the URL line by line
            while ((info = bufferedReader.readLine()) != null)
            {

                if (countLine > 0) {
                    // split the line using regex ,
                    String[] value = info.split(",");

                    // check for empty values, and assign them to n/a
                    for (int i = 0;  i < value.length; i++)
                    {
                        if (value[i].isEmpty()) {
                            value[i] = "n/a";
                        }
                    }

                    // store the info into the variables
                    String id = value[0];
                    String offset = value[1];
                    String sex = value[2];
                    String age = value[3];
                    String finding = value[4];
                    String survival = value[6];

                    String intubated = value[7];
                    String intubation_present = value[8];
                    String went_icu = value[9];
                    String in_icu = value[10];
                    String needed_supplemental_O2 = value[11];
                    String extubated = value[12];

                    String temperature = value[13];
                    String pO2_saturation = value[14];
                    String leukocyte_count = value[15];
                    String neutrophil_count = value[16];
                    String lymphocyte_count = value[17];
                    String view = value[19];

                    //modality,date,location
                    String modality = value[18];
                    String date = value[20];
                    String location = value[21].replace("\"", ""); // get rid of double quotes
                    // Check if the date contains double quotes
                    if (date.contains("\""))
                    {
                        // set the date location values accordingly
                        date = value[20].replace("\"", "") + value[21].replace("\"", "");// get rid of double quotes
                        location = value[22].replace("\"", "");// get rid of double quotes
                    }

                    // create a patient info object and add it to the list:
                    list.add(new PatientInfo(id, offset, sex, age, finding, survival, countLine,
                            intubated, intubation_present, went_icu, in_icu, needed_supplemental_O2, extubated,
                            temperature, pO2_saturation, leukocyte_count, neutrophil_count, lymphocyte_count, view,
                            modality, date, location));

                }
                // Check for limit to exit of the loop
                if (countLine == LIMIT_OF_PATIENTS)
                {
                    break;
                }
                countLine++;
            }
            System.out.println("the number of patients info lines in the text file: " + countLine);
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return list;
    }// End of the method


    /**
     * A class that represents a patient info object.
     */
    public class PatientInfo
    {
        // An individual index for each object
        private int index;
        // Instance variables for a patient info:
        // patient id,offset,sex,age,finding,survival,intubated,intubation_present,went_icu,in_icu,needed_supplemental_O2,extubated,temperature,pO2_saturation,leukocyte_count,
        // neutrophil_count,lymphocyte_count,view,modality,date,location
        private String patientID;
        private String offset;
        private String sex;
        private String age;
        private String finding;
        private String survival;
        private String intubated;
        private String intubation_present;
        private String went_icu;
        private String in_icu;
        private String needed_supplemental_O2;
        private String extubated;
        private String temperature;
        private String pO2_saturation;
        private String leukocyte_count;
        private String neutrophil_count;
        private String lymphocyte_count;
        private String view;
        private String modality;
        private String date;
        private String location;

        // Constructor
        public PatientInfo(String patientID, String offset, String sex, String age, String finding, String survival, int index,
                           String intubated, String intubation_present, String went_icu, String in_icu, String needed_supplemental_O2, String extubated,
                           String temperature, String pO2_saturation, String leukocyte_count, String neutrophil_count, String lymphocyte_count, String view,
                           String modality, String date, String location)
        {
            this.patientID = patientID;
            this.offset = offset;
            this.sex = sex;
            this.age = age;
            this.finding = finding;
            this.survival = survival;
            this.index = index;
            this.intubated = intubated;
            this.intubation_present = intubation_present;
            this.went_icu = went_icu;
            this.in_icu = in_icu;
            this.needed_supplemental_O2 = needed_supplemental_O2;
            this.extubated = extubated;
            this.temperature = temperature;
            this.pO2_saturation = pO2_saturation;
            this.leukocyte_count = leukocyte_count;
            this.neutrophil_count = neutrophil_count;
            this.lymphocyte_count = lymphocyte_count;
            this.view = view;
            this.modality = modality;
            this.date = date;
            this.location = location;
        }

        // Getters for all parameters:

        public String getPatientID() {
            return patientID;
        }

        public String getOffset() {
            return offset;
        }
        public String getSex() {
            return sex;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
        public String getFinding() {
            return finding;
        }

        public String getSurvival() {
            return survival;
        }

        public int getIndex() {
            return index;
        }

        public String getIntubated() {
            return intubated;
        }

        public String getIntubation_present() {
            return intubation_present;
        }

        public String getWent_icu() {
            return went_icu;
        }

        public String getIn_icu() {
            return in_icu;
        }

        public String getNeeded_supplemental_O2() {
            return needed_supplemental_O2;
        }

        public String getExtubated() {
            return extubated;
        }

        public String getTemperature() {
            return temperature;
        }

        public String getpO2_saturation() {
            return pO2_saturation;
        }

        public String getLeukocyte_count() {
            return leukocyte_count;
        }

        public String getNeutrophil_count() {
            return neutrophil_count;
        }

        public String getLymphocyte_count() {
            return lymphocyte_count;
        }

        public String getView() {
            return view;
        }

        public String getModality() {
            return modality;
        }

        public String getDate() {
            return date;
        }

        public String getLocation() {
            return location;
        }

        @Override
        public String toString() {
            return "PatientInfo{" +
                    "index=" + index +
                    ", patientID='" + patientID + '\'' +
                    ", offset='" + offset + '\'' +
                    ", sex='" + sex + '\'' +
                    ", age='" + age + '\'' +
                    ", finding='" + finding + '\'' +
                    ", survival='" + survival + '\'' +
                    ", intubated='" + intubated + '\'' +
                    ", intubation_present='" + intubation_present + '\'' +
                    ", went_icu='" + went_icu + '\'' +
                    ", in_icu='" + in_icu + '\'' +
                    ", needed_supplemental_O2='" + needed_supplemental_O2 + '\'' +
                    ", extubated='" + extubated + '\'' +
                    ", temperature='" + temperature + '\'' +
                    ", pO2_saturation='" + pO2_saturation + '\'' +
                    ", leukocyte_count='" + leukocyte_count + '\'' +
                    ", neutrophil_count='" + neutrophil_count + '\'' +
                    ", lymphocyte_count='" + lymphocyte_count + '\'' +
                    ", view='" + view + '\'' +
                    ", modality='" + modality + '\'' +
                    ", date='" + date + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }
    }// End of the patient class



    public static void main(String[] args) {
        // Create frame on the event dispatching thread:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new ImageViewer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}// End of the program
