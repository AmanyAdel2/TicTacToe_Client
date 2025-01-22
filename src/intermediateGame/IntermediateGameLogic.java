/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intermediateGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntermediateGameLogic {

    private char[][] board;
    private Random random = new Random();

    public IntermediateGameLogic() {
        resetGame();
    }

    public void resetGame() {
        board = new char[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = '-';
            }
        }
    }

    public boolean makeMove(int row, int col, char player) {
        if (board[row][col] == '-') {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public char[][] getBoard() {
        return board;
    }

    public List<int[]> checkWinner(char player) {
        List<int[]> winningCells = new ArrayList<>();

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                winningCells.add(new int[]{i, 0});
                winningCells.add(new int[]{i, 1});
                winningCells.add(new int[]{i, 2});
                return winningCells;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                winningCells.add(new int[]{0, i});
                winningCells.add(new int[]{1, i});
                winningCells.add(new int[]{2, i});
                return winningCells;
            }
        }

        // Check first diagonal
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            winningCells.add(new int[]{0, 0});
            winningCells.add(new int[]{1, 1});
            winningCells.add(new int[]{2, 2});
            return winningCells;
        }

        // Check second diagonal
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            winningCells.add(new int[]{0, 2});
            winningCells.add(new int[]{1, 1});
            winningCells.add(new int[]{2, 0});
            return winningCells;
        }

        return winningCells; // Empty list means no winner
    }

    public boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] findBestMove(char player) {
        // Check for a winning move
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = player;
                    if (checkWinner(player).size() > 0) {
                        board[i][j] = '-';
                        return new int[]{i, j};
                    }
                    board[i][j] = '-';
                }
            }
        }

        // Check to block the opponent's winning move
        char opponent = (player == 'X') ? 'O' : 'X';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = opponent;
                    if (checkWinner(opponent).size() > 0) {
                        board[i][j] = '-';
                        return new int[]{i, j};
                    }
                    board[i][j] = '-';
                }
            }
        }

        // Random move with a 20% chance
        if (random.nextDouble() < 0.2) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        return new int[]{i, j};
                    }
                }
            }
        }

        // Default move (first available cell)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return new int[]{i, j};
                }
            }
        }

        return null;
    }
}