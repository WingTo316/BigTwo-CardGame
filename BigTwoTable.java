import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

/**
 * This class is used to build a GUI for the Big Two card game 
 * and handle all user actions
 * 
 * @author wingk
 */
public class BigTwoTable implements CardGameTable {
  private JFrame frame = new JFrame("Big Two"); // the window of this application
  private JPanel textPanel = new JPanel(); // a panel holding text areas
  private JPanel gamePanel = new JPanel(); // a panel holding game table
  
  private TextArea msgArea; // a text area showing game status
  private TextArea chatArea; // a text area for chat
  
  private JButton playButton; // a "Play" button for player play selected cards
  private JButton reselectButton; // a "Reselect" button for player reselect cards
  private JButton passButton; // a "Pass" button for player pass his/her turn

  private JMenuItem connectMenuItem; // for reconnect to server
  private JMenuItem quitMenuItem; // for player quit game
  private JCheckBoxMenuItem smallSizeMenuItem; // 1x (1280*720)
  private JCheckBoxMenuItem middleSizeMenuItem; // 1.25x
  private JCheckBoxMenuItem largeSizeMenuItem; // 1.5x
  
  //list of player panel for display player avatar, handOnTable player hand
  private ArrayList<PlayerPanel>playerPanelList = new ArrayList<PlayerPanel>();
  
  private final double baseWidth; // initial width of the PlayerPanel
  private final double baseHeight; // initial height of the PlayerPanel
  private double sizeRatio = 1.0; // minimum of widthRatio and heightRatio
  
  private BigTwoClient client; // a client associates with this table
  private int activePlayer; // the index of the active player
  
  //a array indicate which cards are selected
  private boolean[] selected = new boolean[13];  
  private Image[][] cardImages = new Image[4][13]; // images of card faces
  private Image cardBackImage; // image of the card back
  
  /**
   * a constructor for creating a BigTwoTable, 
   * it initialize the GUI for user
   * 
   * @param client a client associates with this table
   */
  public BigTwoTable(BigTwoClient client) {
    this.client = client;
    loadCardImages(); // loading images of faces and back of cards
    
    gamePanel.setLayout(new BorderLayout());
    gamePanel.add(labelPanel(), BorderLayout.NORTH);
    gamePanel.add(buttonPanel(), BorderLayout.SOUTH);
    gamePanel.add(playersPanel());
    
    textPanel.setLayout(new BorderLayout());
    textPanel.add(textFieldPanel(), BorderLayout.SOUTH);
    textPanel.add(textAreaPanel());
    textPanel.setPreferredSize(new Dimension(560, 720));
    
    frame.setJMenuBar(menuBar()); // add menuBar to main frame
    frame.add(textPanel, BorderLayout.WEST);
    frame.add(gamePanel);
    
    // setting the main frame
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setPreferredSize(new Dimension(1280, 720));
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);
    
    // setting the initial width and height
    baseWidth = playerPanelList.get(0).getWidth();
    baseHeight = playerPanelList.get(0).getHeight();
    
    disable(); // it will able when game start
  }
  
  /**
   * A prompt for user input playerName, targeted IP and targeted Port, 
   * called when starting the game or reconnect to server
   * 
   * @param client a client associates with this prompt
   */
  public static void userInputPrompt(BigTwoClient client) {
    JTextField nameField = new JTextField(15);
    JTextField IPField = new JTextField(15);
    JTextField PortField = new JTextField(15);
    nameField.setText(client.getPlayerName());
    IPField.setText(client.getServerIP());
    PortField.setText(client.getServerPort()+"");
    
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Please enter your name:")); panel.add(nameField);
    panel.add(new JLabel("Please enter server IP:")); panel.add(IPField);
    panel.add(new JLabel("Please enter TCP Port:")); panel.add(PortField);
    
    JOptionPane.showMessageDialog(null, panel, "Starting", JOptionPane.DEFAULT_OPTION);
    
    if (nameField.getText().length() != 0)
      client.setPlayerName(nameField.getText()); // store user name
    else
      client.setPlayerName("Hugo"+((int) (9000*Math.random()+999))); // if no name
    
    client.setServerIP(IPField.getText()); // store server IP
    
    try {
      int serverPort = Integer.parseInt(PortField.getText());
      client.setServerPort(serverPort); // store TCP Port
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Invalid TCP Port");
    } // if user input not integer
  }
  
  /**
   * A prompt for display the end game massage,
   * i.e. # of cards in player hand and winner
   * 
   * @param client a client associates with this prompt
   */
  public static void endgamePrompt(BigTwoClient client) {
    String gameResult = "";
    for (CardGamePlayer player : client.getPlayerList()) {
      if (player.getNumOfCards() == 0)
        gameResult += (player.getName()+" wins the game.\n");
      else
        gameResult += (player.getName()+" has "+
            player.getNumOfCards()+" cards in hand.\n"); 
    }
    JOptionPane.showMessageDialog(
        null, gameResult, "Game ends", JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * loading card images
   */
  private void loadCardImages() {
    String[] suits = {"d", "c", "h", "s"};
    String[] ranks = {"a", "2", "3", "4", "5", "6", "7", "8", "9", "t", "j", "q", "k"};
    for (int i=0; i<4; i++) { // suits
      for (int j=0; j<13; j++) { // ranks
        try {
          cardImages[i][j] = new ImageIcon(ClassLoader.getSystemResource(
                  "cards/"+ranks[j]+suits[i]+".gif")).getImage();
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("Image(s) not found");
        }
      }
    }
    try {
      cardBackImage = new ImageIcon(
          ClassLoader.getSystemResource("cards/b.gif")).getImage();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Image(s) not found");
    }
  }
  
  /**
   * create labelPanel
   * @return a panel with labels
   */
  private JPanel labelPanel() {
    Font labelFont = new Font("Consolas", Font.PLAIN, 18);
    
    JPanel labelPanel = new JPanel(); // a panel on gamePanel.NORTH holding labels
    labelPanel.setLayout(new GridBagLayout());
    labelPanel.setBackground(new Color(139, 69, 19));
    GridBagConstraints labelPanelConstraint = new GridBagConstraints();
    
    labelPanelConstraint.anchor = GridBagConstraints.WEST;
    labelPanelConstraint.ipady = 13;
    labelPanelConstraint.weightx = 1.0;
    
    labelPanelConstraint.insets = new Insets(0, 10, 0, 0);
    JLabel label = new JLabel("Players Name");
    label.setFont(labelFont);
    labelPanel.add(label, labelPanelConstraint);
    
    labelPanelConstraint.gridx = 3;
    labelPanelConstraint.insets = new Insets(0, 0, 0, 70);
    label = new JLabel("Table");
    label.setFont(labelFont);
    labelPanel.add(label, labelPanelConstraint);
    
    labelPanelConstraint.gridx = 9;
    labelPanelConstraint.ipadx = 50;
    label = new JLabel("Players Hand");
    label.setFont(labelFont);
    labelPanel.add(label, labelPanelConstraint);
    
    return labelPanel;
  }
  
  /**
   * create playersPanel and each playerPanel
   * @return a panel holding other 4 player panels
   */
  private JPanel playersPanel() {
    JPanel playersPanel = new JPanel();
    playersPanel.setLayout(new GridBagLayout());
    playersPanel.setBackground(new Color(46, 139, 87));
    GridBagConstraints playerPanelConstraint = new GridBagConstraints();
    
    playerPanelConstraint.weightx = 1.0;
    playerPanelConstraint.weighty = 1.0;
    playerPanelConstraint.fill = GridBagConstraints.BOTH;
    
    for (int i=0; i<4; i++) {
      playerPanelConstraint.gridy = i;
      playerPanelList.add(new PlayerPanel(client.getPlayerList().get(i)));
      playersPanel.add(playerPanelList.get(i), playerPanelConstraint);
    }
    return playersPanel;
  }
  
  /**
   * create button panel
   * @return a panel with play, reselect, pass buttons
   */
  private JPanel buttonPanel() {
    JPanel buttonPanel = new JPanel();
    
    setPlayButton();
    setReselectButton();
    setPassButton();
    
    Box box = Box.createHorizontalBox();      
    box.add(Box.createVerticalStrut(25)); box.add(playButton);    
    box.add(Box.createHorizontalStrut(50)); box.add(reselectButton);
    box.add(Box.createHorizontalStrut(50)); box.add(passButton);
    
    buttonPanel.setBackground(new Color(139, 69, 19));
    buttonPanel.add(box);
    
    return buttonPanel;
  }
  
  /**
   * create text area panel
   * @return a panel with two text areas
   */
  private JPanel textAreaPanel() {
    JPanel textAreaPanel = new JPanel();
    textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.Y_AXIS));
 
    msgArea = new TextArea();
    chatArea = new TextArea();  
    textAreaPanel.add(msgArea.scroller);
    textAreaPanel.add(chatArea.scroller);
    
    return textAreaPanel;
  }
  
  /**
   * create text field panel
   * @return a panel with a text field
   */
  private JPanel textFieldPanel() {
    JTextField field = new JTextField(40);   
    field.addActionListener(new ActionListener() {   
      @Override
      public void actionPerformed(ActionEvent e) {
        client.sendMessage(new CardGameMessage(
            CardGameMessage.MSG, client.getPlayerID(),field.getText()));
        field.setText("");
      }
    });  
    JPanel textFieldPanel = new JPanel();
    JLabel label = new JLabel("Message: ");
    textFieldPanel.add(label);
    textFieldPanel.add(field);
    
    return textFieldPanel;
  }
  
  /**
   * create a menu bar
   * @return a menu bar
   */
  private JMenuBar menuBar() {
    JMenu gameMenu = new JMenu("Game menu");
    gameMenu.setPreferredSize(new Dimension(120, gameMenu.getPreferredSize().height));
    setConnectMenuItem(); gameMenu.add(connectMenuItem);
    setQuitMenuItem(); gameMenu.add(quitMenuItem);
    
    JMenu windowsMenu = new JMenu("Windows size");
    windowsMenu.setPreferredSize(
        new Dimension(120, windowsMenu.getPreferredSize().height));
    setSmallSizeMenuItem(); windowsMenu.add(smallSizeMenuItem);
    setMiddleSizeMenuItem(); windowsMenu.add(middleSizeMenuItem);
    setLargeSizeMenuItem(); windowsMenu.add(largeSizeMenuItem);
    
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(gameMenu);
    menuBar.add(windowsMenu);
    
    return menuBar;
  }
  
  /**
   * create play button and 
   * implement the actionPerformed() for play button
   */
  private void setPlayButton() {
    playButton = new JButton("Play");
    playButton.setPreferredSize(new Dimension(120, 25));
    playButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (client.getPlayerID() != activePlayer) return;
        if (getSelected() == null) {
          printMsg("---Please select card(s)!!!---");
          return;
        }
        client.makeMove(client.getPlayerID(), getSelected());
        resetSelected();
        frame.repaint();
        try {
          Thread.sleep(20);
        } catch (Exception ex) {
          ex.printStackTrace();
          System.out.println("Error during thread sleeping");
        }
      }
    });
    playButton.setMultiClickThreshhold(300); // avoid multiple click
  }
  
  /**
   * create reselect button and 
   * implement the actionPerformed() for reselect button
   */
  private void setReselectButton() {
    reselectButton = new JButton("Reselect");
    reselectButton.setPreferredSize(new Dimension(120, 25));
    reselectButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        resetSelected();
        frame.repaint();
      }
    });
    reselectButton.setMultiClickThreshhold(300); // avoid multiple click
  }
  
  /**
   * create pass button and 
   * implement the actionPerformed() for pass button
   */
  private void setPassButton() {
    passButton = new JButton("Pass");
    passButton.setPreferredSize(new Dimension(120, 25));
    passButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (client.getPlayerID() != activePlayer) { return; }
        client.makeMove(client.getPlayerID(), null);
        resetSelected();
        frame.repaint();
        try {
          Thread.sleep(20);
        } catch (Exception ex) {
          ex.printStackTrace();
          System.out.println("Error during thread sleeping");
        }
      }
    });
    passButton.setMultiClickThreshhold(300); // avoid multiple click
  }
  
  /**
   * create connect menu item and 
   * implement the actionPerformed() for connect menu item
   */
  private void setConnectMenuItem() {
    connectMenuItem = new JMenuItem("Reconnect");
    connectMenuItem.addActionListener(new ActionListener() {        
      @Override
      public void actionPerformed(ActionEvent e) {
        userInputPrompt(client);
        client.makeConnection();
      }
    });
  }
  
  /**
   * create quit menu item and 
   * implement the actionPerformed() for quit menu item
   */
  private void setQuitMenuItem() {
    quitMenuItem = new JMenuItem("Quit");
    quitMenuItem.addActionListener(new ActionListener() {       
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
  }
  
  /**
   * create small size menu item and 
   * implement the actionPerformed() for small size menu item
   */
  private void setSmallSizeMenuItem() {
    smallSizeMenuItem = new JCheckBoxMenuItem("1.00x 1280*720");
    smallSizeMenuItem.setState(true);
    smallSizeMenuItem.addActionListener(new ActionListener() {        
      @Override
      public void actionPerformed(ActionEvent e) {
        msgArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        textPanel.setPreferredSize(new Dimension(560, 720));
        frame.getContentPane().setPreferredSize(new Dimension(1280, 720));
        frame.pack();
        // set state of check boxes
        smallSizeMenuItem.setState(true);
        middleSizeMenuItem.setState(false);
        largeSizeMenuItem.setState(false);
        // calculate the minimum the size ratio
        sizeRatio = Math.min(
            playerPanelList.get(0).getWidth()/baseWidth,
            playerPanelList.get(0).getHeight()/baseHeight);
      }
    });
  }
  
  /**
   * create middle size menu item and 
   * implement the actionPerformed() for middle size menu item
   */
  private void setMiddleSizeMenuItem() {
    middleSizeMenuItem = new JCheckBoxMenuItem("1.25x 1600*900");
    middleSizeMenuItem.setState(false);
    middleSizeMenuItem.addActionListener(new ActionListener() {       
      @Override
      public void actionPerformed(ActionEvent e) {
        msgArea.setFont(new Font("Consolas", Font.PLAIN, 20));
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 20));
        textPanel.setPreferredSize(new Dimension(700, 900));
        frame.getContentPane().setPreferredSize(new Dimension(1600, 900));
        frame.pack();
        // set state of check boxes
        smallSizeMenuItem.setState(false);
        middleSizeMenuItem.setState(true);
        largeSizeMenuItem.setState(false);
        // calculate the minimum the size ratio
        sizeRatio = Math.min(
            playerPanelList.get(0).getWidth()/baseWidth,
            playerPanelList.get(0).getHeight()/baseHeight);
      }
    });
  }
  
  /**
   * create large size menu item and 
   * implement the actionPerformed() for large size menu item
   */
  private void setLargeSizeMenuItem() {
    largeSizeMenuItem = new JCheckBoxMenuItem("1.50x 1920*1080");
    largeSizeMenuItem.setState(false);
    largeSizeMenuItem.addActionListener(new ActionListener() {        
      @Override
      public void actionPerformed(ActionEvent e) {
        msgArea.setFont(new Font("Consolas", Font.PLAIN, 24));
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 24));
        textPanel.setPreferredSize(new Dimension(840, 1080));
        frame.getContentPane().setPreferredSize(new Dimension(1920, 1080));
        frame.pack();
        // set state of check boxes
        smallSizeMenuItem.setState(false);
        middleSizeMenuItem.setState(false);
        largeSizeMenuItem.setState(true);
        // calculate the minimum the size ratio
        sizeRatio = Math.min(
            playerPanelList.get(0).getWidth()/baseWidth,
            playerPanelList.get(0).getHeight()/baseHeight);
      }
    });
  }
  
  /**
   * set the local player panel to the bottom
   * for better user friendly GUI
   * 
   * @param playerID the ID of local player
   */
  public void updatePlayersPanel(int playerID) {
    int[] sequence = {0, 1, 2, 3};
    switch (playerID) {
    case 0:
      sequence[0] = 1;
      sequence[1] = 2;
      sequence[2] = 3;
      sequence[3] = 0;
      break;
    case 1:
      sequence[0] = 2;
      sequence[1] = 3;
      sequence[2] = 0;
      sequence[3] = 1;
      break;
    case 2:
      sequence[0] = 3;
      sequence[1] = 0;
      sequence[2] = 1;
      sequence[3] = 2;
    }
    for (int i=0; i<4; i++)
      playerPanelList.get(i).setPlayerPanelPlayer(sequence[i]);
  }
  
  @Override
  public void setActivePlayer(int activePlayer) { this.activePlayer = activePlayer; }
  
  @Override
  public int[] getSelected() {
    int[] cardIdx = null;
    int count = 0;
    for (int i=0; i<selected.length; i++) {
      if (selected[i])
        count++;
    }   
    if (count != 0) {
      cardIdx = new int[count];
      count = 0;
      for (int i=0; i<selected.length; i++) {
        if (selected[i]) {
          cardIdx[count] = i;
          count++;
        }
      }
    }
    return cardIdx;
  }
  
  @Override
  public void resetSelected() {
    selected = new boolean[13];
    repaint();
  }
  
  @Override
  public void repaint() { frame.repaint(); }
  
  @Override
  public void printMsg(String msg) { msgArea.append(msg+"\n"); }

  @Override
  public void clearMsgArea() { msgArea.setText(""); }
  
  /**
   * print message to chat area
   * @param msg message to print
   */
  public void printChat(String msg) { chatArea.append(msg+"\n"); }
  
  @Override
  public void reset() {
    resetSelected();
    clearMsgArea();
    enable();
    repaint();
  }
  
  @Override
  public void enable() {
    for (int i=0; i<4; i++) {
      if (client.getPlayerList().get(client.getPlayerID())
          == playerPanelList.get(i).player)
        playerPanelList.get(i).setEnabled(true);      
    }
    playButton.setEnabled(true);
    passButton.setEnabled(true);
    reselectButton.setEnabled(true);
  }
  
  @Override
  public void disable() {
    for (int i=0; i<4; i++)
      playerPanelList.get(i).setEnabled(false);
    playButton.setEnabled(false);
    reselectButton.setEnabled(false);
    passButton.setEnabled(false);
    repaint();
  }
  
  /**
   * This inner class is to model the text area in the application
   * 
   * @author wingk
   */
  private class TextArea extends JTextArea {
    private static final long serialVersionUID = -7004403120461276489L;
    private JScrollPane scroller;
    private DefaultCaret caret;
    
    public TextArea() {
      setRows(20);
      setLineWrap(true);
      setEditable(false);
      setFont(new Font("Consolas", Font.PLAIN, 16));
      
      caret = (DefaultCaret) this.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      
      scroller = new JScrollPane(this);
      scroller.setVerticalScrollBarPolicy(
          ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      scroller.setHorizontalScrollBarPolicy(
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
  }
  
  /**
   * This inner class is to model playerPanel to display 
   * a player avatar, hand and table
   * 
   * @author wingk
   */
  private class PlayerPanel extends JPanel implements MouseListener {
    private static final long serialVersionUID = 2543413276285289042L;
    private CardGamePlayer player; // the player of this panel
    private Image avatar; // image of avatar of this player
    private Image current; // Indicate current player
    
    /**
     * a constructor initialize a playerPanel
     * @param player a player associate with this panel
     */
    public PlayerPanel(CardGamePlayer player) {
      this.player = player;
      player.setName("");
      try {
        avatar = new ImageIcon(ClassLoader.getSystemResource(
            "avatars/"+client.getPlayerList().indexOf(player)+".png")).getImage();
        current = new ImageIcon(ClassLoader.getSystemResource("cards/j.gif")).getImage();
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Image(s) not found");
      }
      if (player == client.getPlayerList().get(3))
        setBorder(BorderFactory.createLineBorder(Color.blue, 1));
      else
        setBorder(BorderFactory.createLineBorder(Color.black, 1));
      addMouseListener(this); // register
      setEnabled(false);
    }
    
    /**
     * setting the player associate with this panel
     * @param playerID the ID of a player that will master this panel
     */
    private void setPlayerPanelPlayer(int playerID) {
      player = client.getPlayerList().get(playerID);
      try {
        avatar = new ImageIcon(ClassLoader.getSystemResource(
            "avatars/"+client.getPlayerList().indexOf(player)+".png")).getImage();
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Image(s) not found");
      }
    }
    
    @Override
    public void paintComponent(Graphics g) {   
      CardList playerHand = player.getCardsInHand();
      if (client.endOfGame()) { // if game end, show all remaining cards
        displayCardFace(g, playerHand);
      } else { // if is local player, show all cards
        if (player == client.getPlayerList().get(activePlayer)) {
          g.setColor(Color.yellow); // active player yellow color name
          // a image shows that he is the active player
          g.drawImage(
              scaledImage(current), (int) (310*sizeRatio), (int) (33*sizeRatio), this);
        }
        // display player hand
        if (player == client.getPlayerList().get(client.getPlayerID())) {
          g.setFont(new Font("Consolas", Font.PLAIN, 20));
          g.drawString(
              player.getName()+" (You)", (int) (5*sizeRatio), (int) (23*sizeRatio));
          displayerHand(g, playerHand);         
        } else {          
          displayCardBack(g);
        }
      }     
      if (!(client.getHandsOnTable().isEmpty())) { // if some hand on table
        Hand lastHandOnTable = 
            client.getHandsOnTable().get(client.getHandsOnTable().size()-1);
        if (player == lastHandOnTable.getPlayer())
          displayLastHandOnTable(g, lastHandOnTable);
      }
      // if player not exist, then return
      if (player.getName() == null) { return; }
      if (player.getName() == "") { return; }
      // display player avatar and name
      g.drawImage(
          scaledImage(avatar), (int) (12*sizeRatio), (int) (30*sizeRatio), this);
      g.setFont(new Font("Consolas", Font.PLAIN, 20));
      g.drawString(player.getName(), (int) (5*sizeRatio), (int) (23*sizeRatio));
    }
    
    /**
     * display last hand on table if it is played by this player
     * @param g Graphics g
     * @param lastHandOnTable the last hand on table
     */
    private void displayLastHandOnTable(Graphics g, Hand lastHandOnTable) {
      for (int i=0; i<lastHandOnTable.size(); i++) {
        int suit = lastHandOnTable.getCard(i).getSuit();
        int rank = lastHandOnTable.getCard(i).getRank();
        g.drawImage(
            scaledImage(cardImages[suit][rank]),
            (int) ((160+15*i)*sizeRatio),
            (int) (33*sizeRatio),
            this);
      }
    }
    
    /**
     * display card faces
     * @param g Graphics g
     * @param playerHand player hand
     */
    private void displayCardFace(Graphics g, CardList playerHand) {
      for (int i=0; i<playerHand.size(); i++) {
        int suit = playerHand.getCard(i).getSuit();
        int rank = playerHand.getCard(i).getRank();
        g.drawImage(
            scaledImage(cardImages[suit][rank]),
            (int) ((400+15*i)*sizeRatio),
            (int) (33*sizeRatio),
            this);
      }
    }
    
    /**
     * display local player card faces
     * @param g Graphics g
     * @param playerHand local player hand
     */
    private void displayerHand(Graphics g, CardList playerHand) {         
      for (int i=0; i<playerHand.size(); i++) {
        int suit = playerHand.getCard(i).getSuit();
        int rank = playerHand.getCard(i).getRank();
        if (selected[i]) // if that card is selected, place upper
          g.drawImage(
              scaledImage(cardImages[suit][rank]),
              (int) ((400+15*i)*sizeRatio),
              (int) (18*sizeRatio),
              this);
        else
          g.drawImage(
              scaledImage(cardImages[suit][rank]),
              (int) ((400+15*i)*sizeRatio),
              (int) (33*sizeRatio),
              this);
      }
    }
    
    /**
     * display card back
     * @param g Graphics g
     */
    private void displayCardBack(Graphics g) {
      for (int i=0; i<player.getNumOfCards(); i++)
        g.drawImage(
            scaledImage(cardBackImage), 
            (int) ((400+15*i)*sizeRatio),
            (int) (33*sizeRatio),
            this);
    }
    
    /**
     * adjust the size of a image
     * @param image image need to adjust
     * @return image after adjust
     */
    private Image scaledImage(Image image) {
      if (sizeRatio > 1) {
        Image scaledImage = image.getScaledInstance(
            (int) (image.getWidth(this)*sizeRatio),
            (int) (image.getHeight(this)*sizeRatio),
            Image.SCALE_SMOOTH);
        ImageIcon newImage = new ImageIcon(scaledImage);
        return newImage.getImage();
      }
      return image;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
      if (!(isEnabled())) return;
      int xCoor = e.getX(); // coordinate of click
      int yCoor = e.getY();
      int numOfCards = player.getNumOfCards();
      
      // check if click within a card
      // locate the area of click
      if (xCoor>399*sizeRatio && xCoor<(472+15*(numOfCards-1))*sizeRatio) {
        for (int i=numOfCards-1; i>=0; i--) {
          if (xCoor>(399+15*i)*sizeRatio && xCoor<(472+15*i)*sizeRatio) {
            if (selected[i]) {
              if (yCoor>17*sizeRatio && yCoor<116*sizeRatio) {
                selected[i] = false;
                break;
              }
            } else {
              if (yCoor>32*sizeRatio && yCoor<131*sizeRatio) {
                selected[i] = true;
                break;
              }
            }
          }
        }
      }
      frame.repaint();
    }

    // not use in this assignment
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    
  }
}
