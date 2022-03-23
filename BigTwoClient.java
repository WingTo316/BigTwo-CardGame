import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is used to model a Big Two card game client
 * that supports local user play with other 3 users over the Internet
 * 
 * @author wingk
 */
public class BigTwoClient implements CardGame, NetworkGame {
  private int numOfPlayer; // the number of players
  private Deck deck; // a deck of cards
  
  //a list of players and list of hands played on the table
  private ArrayList<CardGamePlayer> playerList = new ArrayList<CardGamePlayer>();
  private ArrayList<Hand> handsOnTable = new ArrayList<Hand>();
  
  private int playerID; // the ID index of the local player
  private String playerName = ""; // the name of the local player
  
  private String serverIP = "127.0.0.1"; // the IP address of the game server
  private int serverPort = 2396; // the TCP port of the game server
  private Socket sock; // a socket connection to the game server
  
  private ObjectOutputStream oos; // for sending messages to the server
  private int currentIdx; // the index of the current player
  private BigTwoTable table; // the GUI for local player
  
  /**
   * This is a constructor for BigTwoClient, 
   * it create 4 player, construct user GUI, 
   * and call to connect to server
   */
  public BigTwoClient() {
    for (int i=0; i<4; i++)
      playerList.add(new CardGamePlayer());
    table = new BigTwoTable(this);
    BigTwoTable.userInputPrompt(this);
    makeConnection();
  }
  
  /**
   * The main function of this application,
   * create a instance of BigTwoClient
   * 
   * @param args not use in this application
   */
  @SuppressWarnings("unused")
  public static void main(String[] args) {
    BigTwoClient client = new BigTwoClient();
  }
  
  @Override
  public void start(Deck deck) {
    handsOnTable = new ArrayList<Hand>(); // remove all the cards on the table
    
    for (CardGamePlayer player : playerList) { // distribute cards players
      player.removeAllCards(); // remove all cards from playerHand
      for (int i=0; i<13; i++) { // 13 cards each
        player.addCard(deck.getCard(0));
        deck.removeCard(0);
      }
      player.sortCardsInHand(); // sort player hand
      
      if (player.getCardsInHand().contains(new Card(0,2))) { // locate diamond 3
        currentIdx = playerList.indexOf(player);
        table.setActivePlayer(currentIdx); // set starting player
      }
    }
    table.reset();
    table.printMsg("(Server) All players are ready. Game starts.");
    table.printMsg(playerList.get(currentIdx).getName()+"'s turn:");
  }
  
  @Override
  public int getNumOfPlayers() { return numOfPlayer; }
  
  @Override
  public Deck getDeck() { return deck; }
  
  @Override
  public ArrayList<CardGamePlayer> getPlayerList() { return playerList; }
  
  @Override
  public ArrayList<Hand> getHandsOnTable() { return handsOnTable; }
  
  @Override
  public int getCurrentIdx() { return currentIdx; }

  @Override
  public synchronized void makeMove(int playerID, int[] cardIdx) {
    sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
  }

  @Override
  public synchronized void checkMove(int playerID, int[] cardIdx) {
    // only active player can "play"
    if (playerID != currentIdx || endOfGame()) { return; }
    
    CardList selectedCard = playerList.get(playerID).play(cardIdx);   
    Hand playerHand = null; // hand represent the card selected by player
    
    if (handsOnTable.isEmpty()) { // the very first play
      
      if (cardIdx == null) {
        if (this.playerID == currentIdx)
          table.printMsg("{pass} <== Not a legal move!!!");
        return;
      } else {
        playerHand = composeHand(playerList.get(playerID), selectedCard);       
        // if player hand is valid and contain diamond 3
        if (playerHand != null && playerHand.contains(new Card(0,2))) {
          
          table.printMsg("{"+playerHand.getType()+"} "+playerHand);
          
          // and play hand to table and remove from player hand
          handsOnTable.add(playerHand);
          playerList.get(playerID).removeCards(playerHand);         
        } else {
          if (this.playerID == currentIdx)
            table.printMsg(selectedCard+" <== Not a legal move!!!");
          return;
        }
      }
    } else if (handsOnTable.get(handsOnTable.size()-1).getPlayer()
        == playerList.get(playerID)) { // all player pass
      if (cardIdx == null) {
        if (this.playerID == currentIdx)
          table.printMsg("{pass} <== Not a legal move!!!");
        return;
      } else {
        playerHand = composeHand(playerList.get(playerID), selectedCard);
        
        if (playerHand == null) {
          if (this.playerID == currentIdx)
            table.printMsg(selectedCard+" <== Not a legal move!!!");
          return;
        } else {
          table.printMsg("{"+playerHand.getType()+"} "+playerHand);
          handsOnTable.add(playerHand);
          playerList.get(playerID).removeCards(playerHand);
        }
      }
    } else { // need to beat last hand on table
      if (cardIdx == null) { // choose pass
        table.printMsg("{pass}");
      } else { // try to beat last hand on table
        playerHand = composeHand(playerList.get(playerID), selectedCard);
        
        if (playerHand == null || !(playerHand.beats(
            handsOnTable.get(handsOnTable.size()-1)))) {
          if (this.playerID == currentIdx)
            table.printMsg(selectedCard+" <== Not a legal move!!!");
          return;
        } else {
          table.printMsg("{"+playerHand.getType()+"} "+playerHand);
          handsOnTable.add(playerHand);
          playerList.get(playerID).removeCards(playerHand);
        }
      } 
    }
    if (endOfGame()) {
      table.disable();
      BigTwoTable.endgamePrompt(this);
      sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
    } else { // next player move
      currentIdx = (currentIdx == 3)? 0 : ++currentIdx;
      table.setActivePlayer(currentIdx);
      table.printMsg(playerList.get(currentIdx).getName()+"'s turn: ");
    }
  }

  @Override
  public boolean endOfGame() {
    if (playerList.get(0).getNumOfCards() != 0
        && playerList.get(1).getNumOfCards() != 0
        && playerList.get(2).getNumOfCards() != 0
        && playerList.get(3).getNumOfCards() != 0)
      return false;
    return true;
  }
  
  @Override
  public int getPlayerID() { return playerID; }

  @Override
  public void setPlayerID(int playerID) { this.playerID = playerID; }
  
  @Override
  public String getPlayerName() { return playerName; }

  @Override
  public void setPlayerName(String playerName) { this.playerName = playerName; }

  @Override
  public String getServerIP() { return serverIP; }

  @Override
  public void setServerIP(String serverIP) { this.serverIP = serverIP; }

  @Override
  public int getServerPort() { return serverPort; }

  @Override
  public void setServerPort(int serverPort) { this.serverPort = serverPort; }

  @Override
  public void makeConnection() {
    if (sock != null) { return; }
    
    try {
      sock = new Socket(serverIP, serverPort);
      oos = new ObjectOutputStream(sock.getOutputStream());
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Connot connect to server");
      table.printChat("(Clinet) Connot connect to server !");
      table.printChat("\n(Clinet) Try server IP: 127.0.0.1");
      table.printChat("(Clinet) Try TCP port:  2396\n");
      try {
        sock = new Socket("127.0.0.1", 2396);
        oos = new ObjectOutputStream(sock.getOutputStream());
        serverIP = "127.0.0.1";
        serverPort = 2396;
      } catch (Exception e1) {
        e1.printStackTrace();
        System.out.println("Still cannot connect to server");
        table.printChat("(Clinet) Still connot connect to server !!!");
        table.printChat("(Clinet) This may because: [1] Server is not operating");
        table.printChat("                           [2] Incorrect IP or TCP Port");
        table.printChat("(Clinet) Please try again {Game menu} => {Reconnect}\n");
        return;
      }
    }
    table.printChat("(Clinet) Connected to server !");
    table.printChat("(Clinet) The server IP: "+serverIP);
    table.printChat("(Clinet) The TCP port:  "+serverPort+"\n");
    // new thread handle receiving massage from server
    Thread commuThread = new Thread(new ServerHandler());
    commuThread.start();
  }

  @Override
  public synchronized void parseMessage(GameMessage message) {
    switch (message.getType()) { // parses the message based on it type
    case CardGameMessage.PLAYER_LIST: // receive playerList
      playerID = message.getPlayerID();
      String[] playerNames = (String[]) message.getData();
      for (int i=0; i<4; i++) {
        playerList.get(i).setName(playerNames[i]); // set name
        if (playerNames[i] != null) {
          // exist player is already "ready"
          table.printChat("(Server) "+playerNames[i]+" has joined");
        }
      }
      table.updatePlayersPanel(playerID); // update playerPanel sequence
      sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
      break;
    case CardGameMessage.JOIN: // someone just join
      playerList.get(message.getPlayerID()).setName((String) message.getData());
      table.printChat("(Server) "+((String) message.getData())+" has joined");
      
      if (message.getPlayerID() == playerID) // send ready
        sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
      break;
    case CardGameMessage.FULL: // server full
      table.printChat("(Server) The server is full !!!");
      break;
    case CardGameMessage.QUIT: // someone quit
      table.printChat(
          "(Server) "+playerList.get(message.getPlayerID()).getName()
          +" "+message.getData()+" has disconnected !");
      table.printMsg("(Server) "+playerList.get(message.getPlayerID()).getName()
          +" has disconnected !");
      playerList.get(message.getPlayerID()).setName("");
      table.disable();
      if (!(endOfGame()))
        sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
      break;
    case CardGameMessage.READY:
      table.printMsg(
          "(Server) "+playerList.get(message.getPlayerID()).getName()+" is ready !");
      break;    
    case CardGameMessage.START:
      start((Deck) message.getData());
      break;
    case CardGameMessage.MOVE:
      checkMove(message.getPlayerID(), (int[]) message.getData());
      break;
    case CardGameMessage.MSG:
      table.printChat((String) message.getData());
    }
    table.repaint();
  }

  @Override
  public void sendMessage(GameMessage message) {
    try {
      oos.writeObject(message);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error in sending message to server");
      table.printChat("(Clinet) Error in sending message to server !!!");
    }
  }

  /*
   * This is an inner class that setting the job of a threat, 
   * this threat responsible for handle message from server
   */
  private class ServerHandler implements Runnable {
    private ObjectInputStream ois;
    
    public ServerHandler() {
      try {
        ois = new ObjectInputStream(sock.getInputStream());
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error in creating server handler");
        table.printChat("(Clinet) Error in creating server handler !!!");
      }
    }
    
    @Override
    public void run() {
      CardGameMessage msg;
      try {
        while ((msg = (CardGameMessage) ois.readObject()) != null)
          parseMessage(msg);
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Disconnected from server");
        sock = null;
        table.printChat("\n(Client) You are disconnected from server !!!\n");
        table.disable();
      }      
    }  
  }
  
  /**
   * a method for returning a valid hand from the specified list of cards 
   * of the player
   * 
   * @param player player
   * @param cards cards
   * @return a valid hand if any
   *       null if no valid hand
   */
  public static Hand composeHand(CardGamePlayer player, CardList cards) {
    Hand validHand = null;
    
    if (cards.size() == 1) {
      
      validHand = new Single(player, cards);
      if (validHand.isValid())
        return validHand;
      
    } else if (cards.size() == 2) {
      
      validHand = new Pair(player, cards);
      if (validHand.isValid())
        return validHand;
      
    } else if (cards.size() == 3) {
      
      validHand = new Triple(player, cards);
      if (validHand.isValid())
        return validHand;
      
    } else if (cards.size() == 5) {
      
      validHand = new Straight(player, cards);
      if (validHand.isValid())
        return validHand;
      
      validHand = new Flush(player, cards);
      if (validHand.isValid())
        return validHand;
      
      validHand = new FullHouse(player, cards);
      if (validHand.isValid())
        return validHand;
      
      validHand = new Quad(player, cards);
      if (validHand.isValid())
        return validHand;
      
      validHand = new StraightFlush(player, cards);
      if (validHand.isValid())
        return validHand;
    }
    return null;
  }
}
