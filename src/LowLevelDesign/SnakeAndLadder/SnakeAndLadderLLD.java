package LowLevelDesign.SnakeAndLadder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class SnakeAndLadderLLD {
    public static void main(String[] args) {
        SnakeAndLadderService slService = new SnakeAndLadderService(100);

        List<Snake> snakes = new ArrayList<>();
        snakes.add(new Snake(17, 7));
        snakes.add(new Snake(54, 34));
        snakes.add(new Snake(62, 19));
        snakes.add(new Snake(64, 60));
        snakes.add(new Snake(87, 36));
        snakes.add(new Snake(93, 73));
        snakes.add(new Snake(98, 79));
        slService.setSnakes(snakes);

        List<Ladder> ladders = new ArrayList<>();
        ladders.add(new Ladder(1, 38));
        ladders.add(new Ladder(4, 14));
        ladders.add(new Ladder(9, 31));
        ladders.add(new Ladder(21, 42));
        ladders.add(new Ladder(28, 84));
        ladders.add(new Ladder(51, 67));
        ladders.add(new Ladder(72, 91));
        ladders.add(new Ladder(80, 99));
        slService.setLadders(ladders);

        List<Player> players = new ArrayList<>();
        players.add(new Player("Kunal", "#1"));
        players.add(new Player("Shubham", "#2"));
        slService.setPlayers(players);

        slService.startGame();
    }
}

enum GameStatus {
    ONGOING, OVER
}

@AllArgsConstructor @Getter
class Snake {
    private int from;
    private int to;
}

@AllArgsConstructor @Getter
class Ladder {
    private int from;
    private int to;
}

@AllArgsConstructor @Getter
class Player {
    private String name;
    private String playerId;
}

@Getter @Setter
class Board {
    private int size;
    private List<Snake> snakes;
    private List<Ladder> ladders;
    private Map<String, Integer> playerPositions;

    public Board(int size) {
        this.size = size;
        snakes = new ArrayList<>();
        ladders = new ArrayList<>();
        playerPositions = new HashMap<>();
    }
}

interface Dice {
    int rollDice();
}

class Dice6 implements Dice {
    @Override
    public int rollDice() {
        return new Random().nextInt(6) + 1;
    }
}

class SnakeAndLadderService {
    private final Board board;
    private final ArrayDeque<Player> players;

    public SnakeAndLadderService(int boardSize) {
        this.board = new Board(boardSize);
        this.players = new ArrayDeque<>();
    }

    public void setPlayers(List<Player> players) {
        Map<String, Integer> map = new HashMap<>();

        for (Player player : players) {
            this.players.add(player);
            map.put(player.getPlayerId(), 0);
        }

        this.board.setPlayerPositions(map);
    }

    public void setSnakes(List<Snake> snakes) {
        this.board.setSnakes(snakes);
    }

    public void setLadders(List<Ladder> ladders) {
        this.board.setLadders(ladders);
    }

    public void startGame() {
        Dice dice = new Dice6();
        GameStatus status = GameStatus.ONGOING;

        while (status == GameStatus.ONGOING) {
            Player currPlayer = players.removeFirst();

            int diceRoll = dice.rollDice();
            movePlayer(currPlayer, diceRoll);
            System.out.println(currPlayer.getName() + " rolls dice, gets " + diceRoll
                    + " new position is " + board.getPlayerPositions().get(currPlayer.getPlayerId()));

            if (playerWon(currPlayer)) {
                System.out.println(currPlayer.getName() + " won!");
                status = GameStatus.OVER;
            } else {
                players.addLast(currPlayer);
            }
        }
    }

    private void movePlayer(Player currPlayer, int diceRoll) {
        int currPosition = board.getPlayerPositions().get(currPlayer.getPlayerId());

        if (currPosition + diceRoll <= board.getSize()) {
            currPosition = checkSnakeLadder(currPosition + diceRoll);
            board.getPlayerPositions().put(currPlayer.getPlayerId(), currPosition);
        }
    }

    private int checkSnakeLadder(int currPosition) {
        int oldPosition;

        do {
            oldPosition = currPosition;

            for (Snake snake : board.getSnakes()) {
                if (snake.getFrom() == currPosition) {
                    currPosition = snake.getTo();
                    System.out.println("Snake");
                }
            }

            for (Ladder ladder : board.getLadders()) {
                if (ladder.getFrom() == currPosition) {
                    currPosition = ladder.getTo();
                    System.out.println("Ladder");
                }
            }

        } while (currPosition != oldPosition);

        return currPosition;
    }

    private boolean playerWon(Player player) {
        return board.getPlayerPositions().get(player.getPlayerId()) == board.getSize();
    }
}
